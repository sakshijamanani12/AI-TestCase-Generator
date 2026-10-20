import * as pdfjsLib from 'pdfjs-dist';
import type { StoryDraft } from '../types';

pdfjsLib.GlobalWorkerOptions.workerSrc = new URL('pdfjs-dist/build/pdf.worker.min.mjs', import.meta.url).toString();

export async function readStoryDocument(file: File): Promise<{ text: string; draft: StoryDraft; notice?: string }> {
  const text = file.name.toLowerCase().endsWith('.pdf')
    ? await readPdf(file)
    : await file.text();
  return { text, ...parseStoryText(text) };
}

async function readPdf(file: File) {
  const buffer = await file.arrayBuffer();
  const document = await pdfjsLib.getDocument({ data: buffer }).promise;
  const pages: string[] = [];
  for (let pageNumber = 1; pageNumber <= document.numPages; pageNumber += 1) {
    const page = await document.getPage(pageNumber);
    const content = await page.getTextContent();
    pages.push(content.items.map((item) => ('str' in item ? item.str : '')).join(' '));
  }
  return pages.join('\n\n');
}

function parseStoryText(text: string): { draft: StoryDraft; notice?: string } {
  const sections: Record<string, string[]> = { feature: [], userStory: [], expectedBehavior: [] };
  let active: keyof typeof sections | null = null;
  const heading = /^(feature(?:\s+name)?|user\s+story|expected\s+behavior|acceptance\s+criteria)\s*:?\s*(.*)$/i;

  const normalizedText = text.replace(/\s+(?=(?:feature(?:\s+name)?|user\s+story|expected\s+behavior|acceptance\s+criteria)\s*:)/gi, '\n');
  for (const rawLine of normalizedText.split(/\r?\n/)) {
    const line = rawLine.trim();
    const match = line.match(heading);
    if (match) {
      const label = match[1].toLowerCase();
      active = label.startsWith('feature') ? 'feature' : label.startsWith('user') ? 'userStory' : 'expectedBehavior';
      if (match[2]) sections[active].push(match[2].trim());
    } else if (active && line) {
      sections[active].push(line);
    }
  }

  const draft: StoryDraft = {
    feature: sections.feature.join(' ').trim(),
    userStory: sections.userStory.join(' ').trim(),
    expectedBehavior: sections.expectedBehavior.join(' ').trim(),
  };
  const hasHeadings = Object.values(draft).some(Boolean);
  if (!hasHeadings) {
    return { draft: { feature: '', userStory: text.trim(), expectedBehavior: '' }, notice: 'This document has no recognized headings. Review the story fields before generating.' };
  }
  return { draft, notice: Object.values(draft).some((value) => !value) ? 'Some sections were not found. Complete the missing fields before generating.' : undefined };
}
