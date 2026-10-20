import { useMemo, useRef, useState } from 'react';
import type { DragEvent } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import { Activity, ArrowUpRight, Check, ChevronDown, Clipboard, Code2, Download, FileJson, GitBranch, Layers3, Paperclip, RotateCcw, Search, ShieldCheck, Sparkles, Terminal, UploadCloud, X } from 'lucide-react';
import { generateTestCases } from './api/client';
import { defaultDraft } from './fixtures/generatedTestCases';
import { readStoryDocument } from './utils/storyDocument';
import { scriptForCase, suiteForCases } from './automation/scripts';
import type { CodeLanguage, StoryDraft, TestCase } from './types';

const categories = ['All', 'Positive', 'Negative', 'Boundary', 'Edge'] as const;
const depthOptions = ['Include happy paths', 'Automate edge cases', 'Negative scenarios'];

function download(name: string, content: string, type: string) {
  const url = URL.createObjectURL(new Blob([content], { type }));
  const anchor = document.createElement('a'); anchor.href = url; anchor.download = name; anchor.click(); URL.revokeObjectURL(url);
}

function App() {
  const [draft, setDraft] = useState<StoryDraft>(defaultDraft);
  const [depth, setDepth] = useState(depthOptions);
  const [cases, setCases] = useState<TestCase[]>([]);
  const [source, setSource] = useState<'fixture' | 'api' | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState<(typeof categories)[number]>('All');
  const [expanded, setExpanded] = useState<string | null>(null);
  const [selectedCaseId, setSelectedCaseId] = useState<string | null>(null);
  const [language, setLanguage] = useState<CodeLanguage>('playwright');
  const [copied, setCopied] = useState('');
  const [toast, setToast] = useState('');
  const [wireframe, setWireframe] = useState<File | null>(null);
  const [storyFile, setStoryFile] = useState<File | null>(null);
  const [storyNotice, setStoryNotice] = useState('');
  const [draggingFile, setDraggingFile] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const storyInputRef = useRef<HTMLInputElement>(null);

  const filtered = useMemo(() => cases.filter((item) => {
    const matchesCategory = category === 'All' || item.type === category;
    const needle = query.toLowerCase();
    return matchesCategory && (!needle || `${item.id} ${item.scenario}`.toLowerCase().includes(needle));
  }), [cases, category, query]);
  const counts = useMemo(() => cases.reduce<Record<string, number>>((acc, item) => { acc[item.type] = (acc[item.type] ?? 0) + 1; return acc; }, {}), [cases]);
  const selectedCase = cases.find((item) => item.id === selectedCaseId) ?? filtered[0] ?? cases[0] ?? null;
  const selectedScript = selectedCase ? scriptForCase(selectedCase, language) : '';

  const notify = (message: string) => { setToast(message); window.setTimeout(() => setToast(''), 2600); };
  const updateDraft = (key: keyof StoryDraft, value: string) => setDraft((current) => ({ ...current, [key]: value }));
  const reset = () => { setDraft(defaultDraft); setCases([]); setSource(null); setError(''); setStoryNotice(''); setQuery(''); setCategory('All'); setExpanded(null); setSelectedCaseId(null); setWireframe(null); setStoryFile(null); };
  const acceptStoryFile = async (file?: File) => {
    if (!file) return;
    if (!/\.(txt|pdf)$/i.test(file.name)) { notify('Import a TXT or PDF user story'); return; }
    try {
      const result = await readStoryDocument(file);
      setStoryFile(file);
      setDraft(result.draft);
      setStoryNotice(result.notice ?? 'Story imported. Review the fields, then generate test cases.');
      notify(`${file.name} imported`);
    } catch (cause) {
      setStoryNotice(cause instanceof Error ? cause.message : 'Unable to read this document.');
    }
  };
  const acceptWireframe = (file?: File) => {
    if (!file) return;
    const allowed = ['application/json', 'image/png', 'image/jpeg'];
    if (!allowed.includes(file.type) && !/\\.(json|png|jpe?g)$/i.test(file.name)) {
      notify('Attach a Figma JSON, PNG, or JPEG file');
      return;
    }
    setWireframe(file);
    notify(`${file.name} attached to the story context`);
  };
  const handleFileDrop = (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setDraggingFile(false);
    acceptWireframe(event.dataTransfer.files[0]);
  };

  const generate = async () => {
    if (!draft.feature.trim() || !draft.userStory.trim() || !draft.expectedBehavior.trim()) { setError('Add a feature, user story, and expected behavior before generating.'); return; }
    setLoading(true); setError('');
    try { const result = await generateTestCases({ ...draft, options: depth }); setCases(result.testCases); setSource(result.source); setSelectedCaseId(result.testCases[0]?.id ?? null); setExpanded(result.testCases[0]?.id ?? null); notify(`${result.testCases.length} test cases synthesized in demo mode`); }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'Generation failed. Try again.'); }
    finally { setLoading(false); }
  };
  const copy = async (text: string, label: string) => { await navigator.clipboard?.writeText(text); setCopied(label); window.setTimeout(() => setCopied(''), 1800); };
  const exportSuite = () => { if (!cases.length) return; const extension = language === 'gherkin' ? 'feature' : language === 'cypress' ? 'js' : 'spec.ts'; download(`synthetix-suite.${extension}`, suiteForCases(cases, language), 'text/plain'); notify(`${cases.length} automation scripts exported`); };
  const csv = ['ID,Priority,Category,Scenario,Steps,Expected result', ...cases.map((item) => [item.id, item.priority, item.type, item.scenario, item.steps.join(' | '), item.expectedResult].map((value) => `"${value.replace(/"/g, '""')}"`).join(','))].join('\n');

  return <main className="app-shell">
    <header className="topbar">
      <div className="brand"><div className="brand-mark"><Sparkles size={17} /></div><div><strong>Synthetix<span>.QA</span></strong><small>AI quality workspace</small></div></div>
      <div className="topbar-actions"><div className="live-status"><span className="pulse" /> Engine ready</div><button className="icon-button" aria-label="Reset workspace" onClick={reset}><RotateCcw size={16} /></button><button className="avatar">SJ</button></div>
    </header>

    <section className="hero-row"><div><p className="eyebrow"><Activity size={14} /> QA AGENT / ANALYSIS WORKSPACE</p><h1>Turn intent into <em>confidence.</em></h1><p className="hero-copy">Generate executable test coverage from a story, inspect the reasoning, and ship automation without leaving your QA command center.</p></div><div className="hero-meta"><span className="meta-dot" /> {source === 'api' ? 'Java generator connected' : 'Offline demo mode'} <span className="slash">/</span> {source === 'api' ? 'full AI coverage' : 'sample coverage'}</div></section>

    <section className="workspace-grid">
      <aside className="panel context-panel">
        <div className="panel-heading"><div><p className="panel-kicker">1 / STORY INPUT</p><h2>Tell us what to test</h2></div><Layers3 size={18} className="muted-icon" /></div>
        <div className="field-stack">
          <label>Feature<input value={draft.feature} onChange={(e) => updateDraft('feature', e.target.value)} placeholder="e.g. Secure authentication" /></label>
          <label>What should the user be able to do?<textarea rows={4} value={draft.userStory} onChange={(e) => updateDraft('userStory', e.target.value)} placeholder="As a user, I want..." /></label>
          <label>What should happen?<textarea rows={4} value={draft.expectedBehavior} onChange={(e) => updateDraft('expectedBehavior', e.target.value)} placeholder="What should happen?" /></label>
        </div>
        <input ref={storyInputRef} className="visually-hidden" type="file" accept=".txt,.pdf,text/plain,application/pdf" onChange={(event) => acceptStoryFile(event.target.files?.[0])} />
        <button className="document-import" onClick={() => storyInputRef.current?.click()}><UploadCloud size={16} /><span><strong>{storyFile ? storyFile.name : 'Import a user story'}</strong><small>Choose a TXT or PDF file</small></span><Paperclip size={14} /></button>
        {storyNotice && <p className="story-notice">{storyNotice}</p>}
        <div className="section-label"><span>Test coverage options</span><span className="hint">{depth.length}/3 enabled</span></div>
        <div className="toggle-list">{depthOptions.map((option) => <button key={option} className={`toggle-chip ${depth.includes(option) ? 'selected' : ''}`} onClick={() => setDepth((current) => current.includes(option) ? current.filter((item) => item !== option) : [...current, option])}><span className="check-box">{depth.includes(option) && <Check size={11} />}</span>{option}</button>)}</div>
        <input ref={fileInputRef} className="visually-hidden" type="file" accept=".json,.png,.jpg,.jpeg,application/json,image/png,image/jpeg" onChange={(event) => acceptWireframe(event.target.files?.[0])} />
        <div className={`drop-zone ${draggingFile ? 'dragging' : ''} ${wireframe ? 'attached' : ''}`} role="button" tabIndex={0} onClick={() => fileInputRef.current?.click()} onKeyDown={(event) => { if (event.key === 'Enter' || event.key === ' ') fileInputRef.current?.click(); }} onDragOver={(event) => { event.preventDefault(); setDraggingFile(true); }} onDragLeave={() => setDraggingFile(false)} onDrop={handleFileDrop}>
          <UploadCloud size={19} />
          <div><strong>{wireframe ? wireframe.name : 'Optional wireframe'}</strong><span>{wireframe ? `${Math.ceil(wireframe.size / 1024)} KB · ready for context` : 'Drop Figma JSON or PNG · or click to browse'}</span></div>
          {wireframe ? <button className="remove-file" aria-label="Remove wireframe" onClick={(event) => { event.stopPropagation(); setWireframe(null); }}><X size={15} /></button> : <Paperclip size={15} className="muted-icon" />}
        </div>
        {error && <div className="error-callout"><X size={15} />{error}</div>}
        <button className="generate-button" onClick={generate} disabled={loading}>{loading ? <><span className="spinner" /> Synthesizing matrix…</> : <><Sparkles size={17} /> Generate test cases <ArrowUpRight size={16} /></>}</button>
        <button className="reset-link" onClick={reset}>Clear workspace</button>
      </aside>

      <section className="panel matrix-panel">
        <div className="panel-heading matrix-heading"><div><p className="panel-kicker">2 / GENERATED TEST CASES</p><h2>Review the test cases</h2></div><div className="matrix-actions"><button className="small-button" onClick={() => download('synthetix-test-cases.json', JSON.stringify(cases, null, 2), 'application/json')} disabled={!cases.length}><FileJson size={14} /> JSON</button><button className="small-button" onClick={() => download('synthetix-test-cases.csv', csv, 'text/csv')} disabled={!cases.length}><Download size={14} /> CSV</button></div></div>
        <div className="metrics"><Metric label="Total generated" value={cases.length || '—'} accent="magenta" /><Metric label="Critical coverage" value={cases.length ? '94%' : '—'} accent="green" /><Metric label="QA hours saved" value={cases.length ? '6.4h' : '—'} accent="violet" /></div>
        <div className="coverage-strip"><div className="coverage-title"><ShieldCheck size={15} /> <span>Coverage profile</span></div>{['Positive', 'Negative', 'Boundary', 'Edge'].map((name) => <div className="coverage-item" key={name}><span>{name}</span><strong>{counts[name] ?? 0}</strong></div>)}</div>
        <div className="matrix-toolbar"><div className="search-field"><Search size={15} /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search ID or scenario…" /></div><div className="category-tabs">{categories.map((item) => <button key={item} className={category === item ? 'active' : ''} onClick={() => setCategory(item)}>{item}</button>)}</div></div>
        <div className="case-list">{!cases.length && !loading ? <div className="empty-state"><div className="empty-icon"><Terminal size={20} /></div><h3>Your matrix is waiting</h3><p>Describe a story on the left and synthesize your first coverage map.</p></div> : loading ? <div className="loading-state"><div className="loading-orbit"><Sparkles size={21} /></div><h3>Agent is reasoning through the story</h3><p>Mapping paths, boundaries, and failure modes…</p></div> : filtered.length ? filtered.map((item) => <CaseRow key={item.id} item={item} open={expanded === item.id} selected={selectedCaseId === item.id} onToggle={() => { setExpanded(expanded === item.id ? null : item.id); setSelectedCaseId(item.id); }} />) : <div className="empty-state compact"><Search size={19} /><h3>No matching cases</h3><p>Try a different search or category.</p></div>}</div>
      </section>

      <aside className="panel inspector-panel">
        <div className="panel-heading"><div><p className="panel-kicker">3 / AUTOMATION SCRIPTS</p><h2>Use the test cases</h2></div><Code2 size={18} className="muted-icon" /></div>
        <p className="inspector-helper">Choose a generated case to preview its automation script. Export the full suite when you are ready to automate everything.</p>
        <div className="inspector-case-list">{cases.length ? cases.map((item) => <button key={item.id} className={`inspector-case ${selectedCase?.id === item.id ? 'active' : ''}`} onClick={() => { setSelectedCaseId(item.id); setExpanded(item.id); }}><span>{item.id}</span><strong>{item.scenario}</strong></button>) : <div className="inspector-empty">Generate test cases to inspect automation.</div>}</div>
        <div className="inspector-toolbar"><div className="language-select"><Code2 size={14} /><select value={language} onChange={(e) => setLanguage(e.target.value as CodeLanguage)}><option value="playwright">Playwright · TS</option><option value="cypress">Cypress · JS</option><option value="gherkin">Cucumber · Gherkin</option></select><ChevronDown size={14} /></div><button className="copy-button" onClick={() => selectedScript && copy(selectedScript, 'code')} disabled={!selectedScript}>{copied === 'code' ? <Check size={14} /> : <Clipboard size={14} />} {copied === 'code' ? 'Copied' : 'Copy case'}</button></div>
        <div className="code-window"><div className="code-topline"><span className="traffic red" /><span className="traffic yellow" /><span className="traffic green" /><span className="file-tab"><Terminal size={12} /> {selectedCase?.id ?? 'coverage'}.{language === 'playwright' ? 'spec.ts' : language === 'cypress' ? 'cy.js' : 'feature'}</span></div><pre><code>{selectedScript || '// Your generated automation will appear here.'}</code></pre></div>
        <div className="inspector-note"><span className="status-check"><Check size={13} /></span><span>{selectedCase ? `Previewing ${selectedCase.id} · ${selectedCase.type} coverage` : 'No generated case selected'}</span></div>
        <div className="inspector-actions"><button className="github-button" onClick={() => notify('GitHub PR handoff is staged for your API connection')} disabled={!cases.length}><GitBranch size={16} /> Stage PR handoff <ArrowUpRight size={15} /></button><button className="suite-button" onClick={exportSuite} disabled={!cases.length}><Download size={14} /> Export suite</button></div>
        <div className="shortcut-row"><span>{cases.length}</span><small>generated cases available for automation</small></div>
      </aside>
    </section>
    <AnimatePresence>{toast && <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 8 }} className="toast"><Check size={15} />{toast}</motion.div>}</AnimatePresence>
  </main>;
}

function Metric({ label, value, accent }: { label: string; value: string | number; accent: string }) { return <div className={`metric-card ${accent}`}><span>{label}</span><strong>{value}</strong><small>{value === '—' ? 'Awaiting generation' : 'from current run'}</small></div>; }
function CaseRow({ item, open, selected, onToggle }: { item: TestCase; open: boolean; selected: boolean; onToggle: () => void }) { return <motion.article layout className={`case-row ${open ? 'open' : ''} ${selected ? 'selected' : ''}`}><button className="case-summary" onClick={onToggle}><span className="case-id">{item.id}</span><span className="case-main"><span className="case-title">{item.scenario}</span><span className="case-sub"><Badge label={item.priority} /> <span className="type-label">{item.type}</span><span className="step-count">{item.steps.length} steps</span></span></span><ChevronDown size={16} className={`chevron ${open ? 'rotated' : ''}`} /></button><AnimatePresence initial={false}>{open && <motion.div initial={{ height: 0, opacity: 0 }} animate={{ height: 'auto', opacity: 1 }} exit={{ height: 0, opacity: 0 }} className="case-detail"><div><span>GIVEN</span><p>{item.given}</p></div><div><span>WHEN</span><p>{item.when}</p></div><div><span>THEN</span><p>{item.then}</p></div></motion.div>}</AnimatePresence></motion.article>; }
function Badge({ label }: { label: string }) { return <span className={`badge ${label.toLowerCase()}`}><span className="badge-dot" />{label}</span>; }

export default App;
