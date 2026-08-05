const app = document.querySelector('#app');
const modalRoot = document.querySelector('#modalRoot');
const toastRoot = document.querySelector('#toastRoot');
const state = { cache: {}, search: '', complianceTab: 'kyc-documents', currentResource: null, latestNavByScheme: {}, schemeFilters: { status: '', type: '' } };
const demoMode = location.protocol === 'file:';
const demoSeed = {
    investors: [{ id:1, fullName:'Aarav Mehta', email:'aarav@example.com', phone:'9876543210', panNumber:'ABCDE1234F', status:'ACTIVE' }],
    'kyc-documents': [{ id:1, investorId:1, documentType:'PAN', documentNumber:'ABCDE1234F', documentUrl:'', status:'VERIFIED' }],
    'bank-mandates': [{ id:1, investorId:1, bankName:'HDFC Bank', accountNumber:'501234567890', ifscCode:'HDFC0001234', accountType:'SAVINGS' }],
    nominees: [{ id:1, investorId:1, fullName:'Anaya Mehta', relationship:'SPOUSE', allocationPercentage:100 }],
    schemes: [{ id:1, schemeCode:'FW-EQ-01', schemeName:'FundWise Equity Growth', schemeType:'EQUITY', riskLevel:'HIGH', launchDate:'2024-01-01', minInvestment:500, expenseRatio:0.72, status:'ACTIVE' }],
    folios: [{ id:1, investorId:1, schemeId:1, folioNumber:'FW100001', status:'ACTIVE', currentUnits:674.08, averageNav:140.25, currentValue:100000 }],
    transactions: [{ id:1, folioId:1, transactionType:'PURCHASE', transactionDate:new Date().toISOString().slice(0,10), amount:100000, nav:148.35, units:674.08, targetSchemeId:null, status:'COMPLETED' }],
    statements: [{ id:1, folioId:1, generatedAt:new Date().toISOString(), statementType:'SUMMARY' }],
    navHistory: { 1: [{ id:1, schemeId:1, navDate:new Date().toISOString().slice(0,10), navValue:148.35 }] }
};
let demoStore = demoMode ? loadDemoStore() : null;

const resources = {
    investors: {
        singular: 'Investor', icon: '◎', description: 'Investor profiles and account status',
        fields: [
            field('fullName', 'Full name', 'text', true), field('email', 'Email address', 'email', true),
            field('phone', 'Phone number', 'tel', false, { pattern:'[0-9]{10}', maxlength:10, title:'Enter exactly 10 digits' }), field('panNumber', 'PAN number', 'text', true, { pattern:'[A-Za-z]{5}[0-9]{4}[A-Za-z]', maxlength:10, title:'PAN must be 5 letters, 4 digits and 1 letter' }),
            select('status', 'Status', ['ACTIVE', 'PENDING', 'INACTIVE'], true)
        ],
        columns: [nameColumn('fullName', 'email'), col('panNumber', 'PAN'), col('phone', 'Phone'), statusColumn()]
    },
    'kyc-documents': {
        singular: 'KYC document', icon: '✓', description: 'Identity documents and verification status',
        fields: [ref('investorId', 'Investor', 'investors', 'fullName'), select('documentType', 'Document type', ['PAN', 'AADHAAR', 'PASSPORT', 'VOTER ID'], true), field('documentNumber', 'Document number', 'text', true, { maxlength:20 }), field('documentUrl', 'Document URL', 'url'), select('status', 'Status', ['VERIFIED', 'PENDING', 'REJECTED'], true)],
        columns: [refColumn('investorId', 'Investor', 'investors', 'fullName'), col('documentType', 'Document'), col('documentNumber', 'Number'), statusColumn()]
    },
    'bank-mandates': {
        singular: 'Bank mandate', icon: '▣', description: 'Registered payout and debit bank accounts',
        fields: [ref('investorId', 'Investor', 'investors', 'fullName'), field('bankName', 'Bank name', 'text', true), field('accountNumber', 'Account number', 'text', true, { pattern:'[0-9]{9,18}', maxlength:18, title:'Account number must contain 9 to 18 digits' }), field('ifscCode', 'IFSC code', 'text', true, { pattern:'[A-Za-z]{4}0[A-Za-z0-9]{6}', maxlength:11, title:'Enter a valid 11-character IFSC code' }), select('accountType', 'Account type', ['SAVINGS', 'CURRENT', 'NRE', 'NRO'])],
        columns: [refColumn('investorId', 'Investor', 'investors', 'fullName'), col('bankName', 'Bank'), maskedColumn('accountNumber', 'Account'), col('ifscCode', 'IFSC'), col('accountType', 'Type')]
    },
    nominees: {
        singular: 'Nominee', icon: '♙', description: 'Investor nominees and allocation percentages',
        fields: [ref('investorId', 'Investor', 'investors', 'fullName'), field('fullName', 'Nominee name', 'text', true), select('relationship', 'Relationship', ['SPOUSE', 'CHILD', 'PARENT', 'SIBLING', 'OTHER']), field('allocationPercentage', 'Allocation (%)', 'number', true, { min: 0, max: 100, step: .01 })],
        columns: [nameColumn('fullName'), refColumn('investorId', 'Investor', 'investors', 'fullName'), col('relationship', 'Relationship'), valueColumn('allocationPercentage', 'Allocation', v => `${number(v)}%`)]
    },
    schemes: {
        singular: 'Scheme', icon: '◇', description: 'Mutual fund schemes, pricing and NAV records',
        fields: [field('schemeCode', 'Scheme code', 'text', true), field('schemeName', 'Scheme name', 'text', true), select('schemeType', 'Scheme type', ['EQUITY', 'DEBT', 'HYBRID'], true), select('riskLevel', 'Risk level', ['LOW', 'MODERATE', 'HIGH', 'VERY_HIGH'], true), field('launchDate', 'Launch date', 'date', true), field('minInvestment', 'Minimum investment (₹)', 'number', true, { min:.01, step:.01 }), field('expenseRatio', 'Expense ratio (%)', 'number', true, { min:0, max:100, step:.01 }), select('status', 'Status', ['ACTIVE', 'INACTIVE', 'CLOSED'], true)],
        columns: [nameColumn('schemeName', 'schemeCode'), col('schemeType', 'Type'), valueColumn('latestNav', 'Current NAV', money), col('riskLevel', 'Risk level'), valueColumn('expenseRatio', 'Expense ratio', v => `${number(v)}%`), statusColumn()],
        special: 'nav'
    },
    folios: {
        singular: 'Folio', icon: '▤', description: 'Investor holdings mapped to mutual fund schemes',
        fields: [ref('investorId', 'Investor', 'investors', 'fullName'), ref('schemeId', 'Scheme', 'schemes', 'schemeName'), field('folioNumber', 'Folio number', 'text', true), select('status', 'Status', ['ACTIVE', 'FROZEN', 'CLOSED'], true), field('currentUnits', 'Current units', 'number', true, { min: 0, step: .0001 }), field('averageNav', 'Average NAV', 'number', true, { min: 0, step: .0001 }), field('currentValue', 'Current value', 'number', true, { min: 0, step: .01 })],
        columns: [nameColumn('folioNumber'), refColumn('investorId', 'Investor', 'investors', 'fullName'), refColumn('schemeId', 'Scheme', 'schemes', 'schemeName'), valueColumn('currentUnits', 'Units', number), valueColumn('currentValue', 'Value', money), statusColumn()]
    },
    transactions: {
        singular: 'Transaction', icon: '⇄', description: 'Purchases, redemptions, SIPs and switches',
        fields: [ref('folioId', 'Folio', 'folios', 'folioNumber'), select('transactionType', 'Transaction type', ['PURCHASE', 'REDEMPTION', 'SIP', 'SWITCH'], true), field('transactionDate', 'Transaction date', 'date', true), field('amount', 'Amount', 'number', true, { min: 0, step: .01 }), field('nav', 'NAV', 'number', false, { min: 0, step: .0001 }), field('units', 'Units', 'number', false, { min: 0, step: .0001 }), ref('targetSchemeId', 'Target scheme (switch only)', 'schemes', 'schemeName', false), select('status', 'Status', ['COMPLETED', 'PENDING', 'FAILED', 'CANCELLED'], true)],
        columns: [col('transactionDate', 'Date'), col('transactionType', 'Type'), refColumn('folioId', 'Folio', 'folios', 'folioNumber'), valueColumn('amount', 'Amount', money), valueColumn('units', 'Units', number), statusColumn()]
    },
    statements: {
        singular: 'Statement', icon: '≡', description: 'Generated investment statement records',
        fields: [ref('folioId', 'Folio', 'folios', 'folioNumber'), select('statementType', 'Statement type', ['SUMMARY', 'DETAILED', 'TAX', 'TRANSACTION'])],
        columns: [refColumn('folioId', 'Folio', 'folios', 'folioNumber'), col('statementType', 'Type'), valueColumn('generatedAt', 'Generated', dateTime)],
        noEdit: true, special: 'statement'
    }
};

function field(name, label, type = 'text', required = false, attrs = {}) { return { name, label, type, required, attrs }; }
function select(name, label, options, required = false) { return { name, label, type: 'select', options, required }; }
function ref(name, label, resource, display, required = true) { return { name, label, type: 'ref', resource, display, required }; }
function col(key, label) { return { key, label, render: row => escapeHtml(row[key] ?? '—') }; }
function valueColumn(key, label, formatter) { return { key, label, render: row => formatter(row[key]) }; }
function nameColumn(key, sub) { return { key, label: key === 'folioNumber' ? 'Folio' : 'Name', render: row => `<div class="name-cell"><span class="initial">${initials(row[key])}</span><span><strong>${escapeHtml(row[key] ?? '—')}</strong>${sub ? `<small>${escapeHtml(row[sub] ?? '')}</small>` : ''}</span></div>` }; }
function statusColumn() { return { key: 'status', label: 'Status', render: row => badge(row.status) }; }
function maskedColumn(key, label) { return { key, label, render: row => `•••• ${escapeHtml(String(row[key] || '').slice(-4))}` }; }
function refColumn(key, label, resource, display) { return { key, label, render: row => escapeHtml(lookup(resource, row[key], display)) }; }

function escapeHtml(value) { return String(value).replace(/[&<>'"]/g, c => ({ '&':'&amp;', '<':'&lt;', '>':'&gt;', "'":'&#39;', '"':'&quot;' }[c])); }
function initials(value = '') { return String(value).split(/\s|-/).filter(Boolean).slice(0,2).map(x => x[0]).join('').toUpperCase() || '#'; }
function number(value) { return value == null || value === '' ? '—' : new Intl.NumberFormat('en-IN', { maximumFractionDigits: 4 }).format(value); }
function money(value) { return value == null || value === '' ? '—' : new Intl.NumberFormat('en-IN', { style:'currency', currency:'INR', maximumFractionDigits:2 }).format(value); }
function dateTime(value) { return value ? new Intl.DateTimeFormat('en-IN', { dateStyle:'medium', timeStyle:'short' }).format(new Date(value)) : '—'; }
function badge(status = '') { const s = String(status); const cls = /pending|frozen/i.test(s) ? 'warn' : /inactive|failed|rejected|closed|cancel/i.test(s) ? 'bad' : !s ? 'neutral' : ''; return `<span class="badge ${cls}">${escapeHtml(s || 'Unknown')}</span>`; }
function lookup(resource, id, display) { const item = (state.cache[resource] || []).find(x => String(x.id) === String(id)); return item ? item[display] : (id == null ? '—' : `#${id}`); }

async function api(path, options = {}) {
    if (demoMode) return demoApi(path, options);
    const response = await fetch(path, { cache:'no-store', headers: { 'Content-Type':'application/json', ...(options.headers || {}) }, ...options });
    if (!response.ok) {
        let message = `Request failed (${response.status})`;
        try { const body = await response.json(); message = body.message || body.error || message; } catch (_) {}
        throw new Error(message);
    }
    return response.status === 204 ? null : response.json();
}

function loadDemoStore() {
    try { return JSON.parse(localStorage.getItem('fundwise-demo')) || structuredClone(demoSeed); }
    catch (_) { return structuredClone(demoSeed); }
}

function saveDemoStore() {
    try { localStorage.setItem('fundwise-demo', JSON.stringify(demoStore)); } catch (_) {}
}

function demoApi(path, options = {}) {
    const method = (options.method || 'GET').toUpperCase();
    const parts = path.replace(/^\//,'').split('/');
    if (parts[0] === 'schemes' && parts[2] === 'nav-history') {
        const schemeId = Number(parts[1]);
        if (method === 'GET') return Promise.resolve(demoStore.navHistory[schemeId] || []);
        const payload = JSON.parse(options.body || '{}');
        const list = demoStore.navHistory[schemeId] ||= [];
        const record = { id:nextId(list), schemeId, ...payload }; list.push(record); saveDemoStore(); return Promise.resolve(record);
    }
    if (parts[0] === 'statements' && parts[1] === 'folio') {
        const folioId = Number(parts[2]);
        return Promise.resolve({ folio:(demoStore.folios || []).find(x=>x.id===folioId) || null, transactions:(demoStore.transactions || []).filter(x=>x.folioId===folioId), generatedAt:new Date().toISOString() });
    }
    const resource = parts[0], id = parts[1] ? Number(parts[1]) : null, list = demoStore[resource];
    if (!Array.isArray(list)) return Promise.reject(new Error('Demo resource not found'));
    if (method === 'GET') return Promise.resolve(id ? list.find(x=>x.id===id) : structuredClone(list));
    if (method === 'POST') { const record={ id:nextId(list), ...JSON.parse(options.body||'{}') }; if(resource==='statements') record.generatedAt=new Date().toISOString(); list.push(record); saveDemoStore(); return Promise.resolve(record); }
    if (method === 'PUT') { const index=list.findIndex(x=>x.id===id); const record={ id, ...JSON.parse(options.body||'{}') }; if(index>=0) list[index]=record; saveDemoStore(); return Promise.resolve(record); }
    if (method === 'DELETE') { const index=list.findIndex(x=>x.id===id); if(index>=0) list.splice(index,1); saveDemoStore(); return Promise.resolve(null); }
}

function nextId(list) { return Math.max(0,...list.map(x=>Number(x.id)||0))+1; }

async function load(resource, force = false) {
    if (!force && state.cache[resource]) return state.cache[resource];
    const data = await api(`/${resource}`);
    state.cache[resource] = Array.isArray(data) ? data : [];
    if (resource === 'schemes') await loadLatestNavs(state.cache[resource]);
    return state.cache[resource];
}

async function loadLatestNavs(schemes) {
    const results = await Promise.all(schemes.map(async scheme => {
        try {
            const nav = await api(`/schemes/${scheme.id}/nav-history/latest`);
            return [scheme.id, nav];
        } catch (_) {
            return [scheme.id, null];
        }
    }));

    state.latestNavByScheme = Object.fromEntries(results);
    schemes.forEach(scheme => {
        scheme.latestNav = state.latestNavByScheme[scheme.id]?.navValue ?? null;
    });
}

function currentRoute() { return (location.hash.replace(/^#\//, '').split(/[?\/]/)[0] || 'dashboard'); }
function pageHead(eyebrow, title, description, actions = '') { return `<div class="page-head"><div><p class="eyebrow">${eyebrow}</p><h1>${title}</h1><p>${description}</p></div><div class="head-actions">${actions}</div></div>`; }
function loadingCard() { return `<div class="card">${Array.from({length:6}, () => '<div class="skeleton"></div>').join('')}</div>`; }

async function router(force = false) {
    clearTimeout(state.retryTimer);
    const route = currentRoute();
    document.querySelectorAll('.nav a').forEach(a => a.classList.toggle('active', a.dataset.route === route));
    document.querySelector('#sidebar').classList.remove('open');
    state.search = '';
    document.querySelector('#globalSearch').value = '';
    app.innerHTML = loadingCard();
    try {
        if (route === 'dashboard') await renderDashboard(force);
        else if (route === 'compliance') await renderCompliance(force);
        else if (resources[route]) await renderResource(route, force);
        else location.hash = '#/dashboard';
        setConnection(true);
    } catch (error) {
        setConnection(false, error.message);
        renderError(error);
    }
}

async function preload(resourcesToLoad, force = false) {
    const settled = await Promise.allSettled(resourcesToLoad.map(r => load(r, force)));
    const failed = settled.filter(x => x.status === 'rejected');
    if (settled.length > 0 && failed.length === settled.length) throw failed[0].reason;
}

async function renderDashboard(force = false) {
    const names = Object.keys(resources);
    await preload(names, force);
    const investors = state.cache.investors || [], schemes = state.cache.schemes || [], folios = state.cache.folios || [], transactions = state.cache.transactions || [];
    const aum = folios.reduce((sum, f) => sum + Number(f.currentValue || 0), 0);
    const invested = transactions.filter(t => /purchase|sip/i.test(t.transactionType)).reduce((sum, t) => sum + Number(t.amount || 0), 0);
    const recent = [...transactions].sort((a,b) => String(b.transactionDate).localeCompare(String(a.transactionDate))).slice(0,5);
    app.innerHTML = `${pageHead('Portfolio command centre', 'Good day, Administrator', 'Here is the latest view of your fund operations.', '<a class="button" href="#/statements">View statements</a><button class="button primary" data-create="transactions">+ New transaction</button>')}
    <div class="metric-grid">
      ${metric('₹', money(aum), 'Assets under management', 'Live holdings value')}
      ${metric('◎', number(investors.length), 'Registered investors', `${investors.filter(x => /active/i.test(x.status)).length} active accounts`)}
      ${metric('◇', number(schemes.length), 'Available schemes', `${schemes.filter(x => /active/i.test(x.status)).length} open for investment`)}
      ${metric('⇄', money(invested), 'Gross investments', `${transactions.length} total transactions`)}
    </div>
    <div class="dashboard-grid">
      <div class="card"><div class="section-head"><div><h2>Transaction activity</h2><p>Monthly purchases and redemptions</p></div><a href="#/transactions" class="button small">View all</a></div>${renderChart(transactions)}</div>
      <div class="card"><div class="section-head"><div><h2>Quick actions</h2><p>Common operational tasks</p></div></div><div class="quick-grid">
        ${quick('◎','Add investor','investors')}${quick('◇','Create scheme','schemes')}${quick('▤','Open folio','folios')}${quick('⇄','Record transaction','transactions')}
      </div></div>
      <div class="card"><div class="section-head"><div><h2>Recent transactions</h2><p>Latest activity across all folios</p></div></div><div class="activity-list">${recent.length ? recent.map(activity).join('') : emptyInline('No transactions recorded yet.')}</div></div>
      <div class="card"><div class="section-head"><div><h2>Operations snapshot</h2><p>Items needing attention</p></div></div><div class="activity-list">
        ${snapshot('KYC pending', (state.cache['kyc-documents'] || []).filter(x => /pending/i.test(x.status)).length, 'Review documents')}
        ${snapshot('Transactions pending', transactions.filter(x => /pending/i.test(x.status)).length, 'Track settlements')}
        ${snapshot('Active folios', folios.filter(x => /active/i.test(x.status)).length, 'Investment accounts')}
      </div></div>
    </div>`;
    bindPageActions();
}

function metric(icon, value, label, note) { return `<div class="metric-card"><div class="metric-top"><span class="metric-icon">${icon}</span><span class="trend">Live</span></div><strong>${value}</strong><span>${label} · ${note}</span></div>`; }
function quick(icon, label, resource) { return `<a href="#/${resource}" class="quick-link" data-create="${resource}"><span>${icon}</span><strong>${label}</strong></a>`; }
function activity(t) { const positive = /purchase|sip/i.test(t.transactionType); return `<div class="activity"><span class="activity-icon">${positive ? '↙' : '↗'}</span><span><strong>${escapeHtml(t.transactionType)}</strong><small>${escapeHtml(lookup('folios',t.folioId,'folioNumber'))} · ${escapeHtml(t.transactionDate)}</small></span><span class="amount">${positive ? '+' : '−'}${money(t.amount)}</span></div>`; }
function snapshot(label, value, note) { return `<div class="activity"><span class="activity-icon">${value}</span><span><strong>${label}</strong><small>${note}</small></span><span>${value ? badge('Action') : badge('Clear')}</span></div>`; }
function emptyInline(text) { return `<div class="empty"><div class="empty-icon">✓</div><h3>All clear</h3><p>${text}</p></div>`; }
function renderChart(items) {
    const months = [];
    const now = new Date();
    for (let i=5;i>=0;i--) { const d = new Date(now.getFullYear(), now.getMonth()-i, 1); months.push({ key:`${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}`, label:d.toLocaleDateString('en-IN',{month:'short'}), buy:0, sell:0 }); }
    items.forEach(t => { const m = months.find(x => String(t.transactionDate).startsWith(x.key)); if (m) (/redemption/i.test(t.transactionType) ? m.sell += Number(t.amount) : m.buy += Number(t.amount)); });
    const max = Math.max(1, ...months.flatMap(m => [m.buy,m.sell]));
    return `<div class="chart-wrap"><div class="bar-chart">${months.map(m => `<div class="bar-group" title="${money(m.buy)} invested · ${money(m.sell)} redeemed"><i class="bar" style="height:${Math.max(3,m.buy/max*100)}%"></i><i class="bar alt" style="height:${Math.max(3,m.sell/max*100)}%"></i><label>${m.label}</label></div>`).join('')}</div></div>`;
}

async function renderCompliance(force = false) {
    await preload(['investors','kyc-documents','bank-mandates','nominees'], force);
    const tabs = [['kyc-documents','KYC documents'],['bank-mandates','Bank mandates'],['nominees','Nominees']];
    app.innerHTML = `${pageHead('Client management', 'KYC & banking', 'Maintain compliance documents, bank mandates and nominees.')}
    <div class="tabs">${tabs.map(([id,label]) => `<button class="tab ${state.complianceTab===id?'active':''}" data-compliance-tab="${id}">${label}</button>`).join('')}</div>
    <div id="resourceArea"></div>`;
    renderResourceArea(state.complianceTab);
    document.querySelectorAll('[data-compliance-tab]').forEach(btn => btn.addEventListener('click', () => { state.complianceTab = btn.dataset.complianceTab; document.querySelectorAll('.tab').forEach(x => x.classList.toggle('active',x===btn)); renderResourceArea(state.complianceTab); }));
}

async function renderResource(resource, force = false) {
    const refs = resources[resource].fields.filter(f => f.type === 'ref').map(f => f.resource);
    await preload([...new Set([resource,...refs])], force);
    state.currentResource = resource;
    const c = resources[resource];
    app.innerHTML = `${pageHead('FundWise operations', plural(c.singular), c.description, `<button class="button primary" data-create="${resource}">+ Add ${c.singular.toLowerCase()}</button>`)}<div id="resourceArea"></div>`;
    renderResourceArea(resource);
}

function renderResourceArea(resource) {
    state.currentResource = resource;
    const c = resources[resource], allRows = state.cache[resource] || [];
    const q = state.search.toLowerCase();
    const rows = q ? allRows.filter(row => Object.values(row).some(v => String(v ?? '').toLowerCase().includes(q))) : allRows;
    const area = document.querySelector('#resourceArea');
    area.innerHTML = `<div class="card">
    <div class="toolbar"><div class="toolbar-search"><span>⌕</span><input data-table-search placeholder="Search ${plural(c.singular).toLowerCase()}…" value="${escapeHtml(state.search)}"></div><button class="button small" data-refresh-resource="${resource}">↻ Refresh</button><button class="button small primary" data-create="${resource}">+ Add new</button></div>
    ${resource === 'schemes' ? schemeFilterControls() : ''}
    ${rows.length ? table(c, rows, resource) : emptyState(c, resource, q)}
    ${rows.length ? `<div class="table-footer"><span>Showing ${rows.length} of ${allRows.length}</span><span>Live data from /${resource}</span></div>` : ''}
  </div>`;
    bindPageActions();
    area.querySelector('[data-table-search]')?.addEventListener('input', e => { state.search = e.target.value; renderResourceArea(resource); });
    area.querySelectorAll('[data-scheme-filter]').forEach(control => control.addEventListener('change', async event => {
        state.schemeFilters[event.target.dataset.schemeFilter] = event.target.value;
        await applySchemeFilters();
    }));
}

function schemeFilterControls() {
    const { status, type } = state.schemeFilters;
    return `<div class="scheme-filters"><label>Status <select data-scheme-filter="status"><option value="">All statuses</option>${['ACTIVE','INACTIVE','CLOSED'].map(value => `<option value="${value}" ${status===value?'selected':''}>${value}</option>`).join('')}</select></label><label>Type <select data-scheme-filter="type"><option value="">All types</option>${['EQUITY','DEBT','HYBRID'].map(value => `<option value="${value}" ${type===value?'selected':''}>${value}</option>`).join('')}</select></label></div>`;
}

async function applySchemeFilters() {
    const params = new URLSearchParams();
    if (state.schemeFilters.status) params.set('status', state.schemeFilters.status);
    if (state.schemeFilters.type) params.set('type', state.schemeFilters.type);
    const query = params.toString();
    const schemes = await api(`/schemes${query ? `?${query}` : ''}`);
    state.cache.schemes = Array.isArray(schemes) ? schemes : [];
    await loadLatestNavs(state.cache.schemes);
    renderResourceArea('schemes');
}

function table(c, rows, resource) { return `<div class="table-wrap"><table><thead><tr>${c.columns.map(x => `<th>${x.label}</th>`).join('')}<th></th></tr></thead><tbody>${rows.map(row => `<tr>${c.columns.map(x => `<td>${x.render(row)}</td>`).join('')}<td><div class="row-actions">${c.special === 'nav' ? `<button title="Maintain NAV" data-nav="${row.id}">NAV</button>` : ''}${c.special === 'statement' ? `<button title="View statement" data-statement="${row.folioId}">▤</button>` : `<button title="View" data-view="${resource}:${row.id}">○</button>`}${!c.noEdit ? `<button title="Edit" data-edit="${resource}:${row.id}">✎</button>` : ''}<button title="Delete" data-delete="${resource}:${row.id}">×</button></div></td></tr>`).join('')}</tbody></table></div>`; }
function emptyState(c, resource, searching) { return `<div class="empty"><div class="empty-icon">${searching ? '⌕' : c.icon}</div><h2>${searching ? 'No matching records' : `No ${plural(c.singular).toLowerCase()} yet`}</h2><p>${searching ? 'Try a different search term.' : `Add your first ${c.singular.toLowerCase()} to get started.`}</p>${searching ? '' : `<button class="button primary" data-create="${resource}">+ Add ${c.singular.toLowerCase()}</button>`}</div>`; }
function plural(value) { if (/history$/i.test(value)) return value; if (/y$/i.test(value)) return value.slice(0,-1)+'ies'; return value+'s'; }

function bindPageActions() {
    document.querySelectorAll('[data-create]').forEach(el => el.addEventListener('click', e => { e.preventDefault(); openForm(el.dataset.create); }));
    document.querySelectorAll('[data-edit]').forEach(el => el.addEventListener('click', () => { const [r,id] = el.dataset.edit.split(':'); openForm(r,id); }));
    document.querySelectorAll('[data-view]').forEach(el => el.addEventListener('click', () => { const [r,id] = el.dataset.view.split(':'); openDetails(r,id); }));
    document.querySelectorAll('[data-delete]').forEach(el => el.addEventListener('click', () => { const [r,id] = el.dataset.delete.split(':'); confirmDelete(r,id); }));
    document.querySelectorAll('[data-nav]').forEach(el => el.addEventListener('click', () => openNavHistory(el.dataset.nav)));
    document.querySelectorAll('[data-statement]').forEach(el => el.addEventListener('click', () => openStatement(el.dataset.statement)));
    document.querySelectorAll('[data-refresh-resource]').forEach(el => el.addEventListener('click', async () => { await load(el.dataset.refreshResource, true); renderResourceArea(el.dataset.refreshResource); toast('Data refreshed'); }));
}

async function openForm(resource, id = null) {
    const c = resources[resource], record = id ? (state.cache[resource] || []).find(x => String(x.id) === String(id)) : {};
    await preload([...new Set(c.fields.filter(f => f.type === 'ref').map(f => f.resource))]);
    modalRoot.innerHTML = `<div class="modal-backdrop" data-close-modal><div class="modal" role="dialog" aria-modal="true"><div class="modal-head"><div><h2>${id?'Edit':'Add'} ${c.singular.toLowerCase()}</h2><p>${id?'Update the existing record.':'Complete the details below.'}</p></div><button class="close-button" data-close>×</button></div><form id="entityForm"><div class="modal-body"><div class="form-grid">${c.fields.map(f => renderField(f, record?.[f.name])).join('')}</div></div><div class="modal-actions"><button type="button" class="button" data-close>Cancel</button><button class="button primary" type="submit">${id?'Save changes':'Create record'}</button></div></form></div></div>`;
    bindModalClose();
    document.querySelector('#entityForm').addEventListener('submit', async e => {
        e.preventDefault(); const button = e.submitter; button.disabled = true; button.textContent = 'Saving…';
        try {
            const data = Object.fromEntries(new FormData(e.target).entries());
            const validation = validatePayload(resource, data);
            if (validation) {
                const control = e.target.elements[validation.field];
                control?.setCustomValidity(validation.message);
                control?.reportValidity();
                control?.addEventListener('input', () => control.setCustomValidity(''), { once:true });
                button.disabled = false; button.textContent = id?'Save changes':'Create record';
                return;
            }
            if (data.panNumber) data.panNumber = data.panNumber.toUpperCase();
            if (data.ifscCode) data.ifscCode = data.ifscCode.toUpperCase();
            if (data.email) data.email = data.email.toLowerCase();
            c.fields.forEach(f => { if (['number','ref'].includes(f.type)) data[f.name] = data[f.name] === '' ? null : Number(data[f.name]); });
            await api(`/${resource}${id?`/${id}`:''}`, { method:id?'PUT':'POST', body:JSON.stringify(data) });
            await load(resource,true); closeModal(); toast(`${c.singular} ${id?'updated':'created'} successfully`); await refreshCurrent();
        } catch (error) { toast(error.message,true); button.disabled = false; button.textContent = id?'Save changes':'Create record'; }
    });
}

function validatePayload(resource, data) {
    if (resource === 'investors') {
        if (data.phone && !/^\d{10}$/.test(data.phone)) return { field:'phone', message:'Phone number must contain exactly 10 digits.' };
        if (!/^[A-Z]{5}\d{4}[A-Z]$/i.test(data.panNumber || '')) return { field:'panNumber', message:'PAN must contain 5 letters, 4 digits and 1 final letter (example: ABCDE1234F).' };
        const duplicate = (state.cache.investors || []).find(x => String(x.id) !== String(data.id || '') && (x.email?.toLowerCase() === data.email?.toLowerCase() || x.panNumber?.toUpperCase() === data.panNumber?.toUpperCase()));
        if (duplicate) return { field: duplicate.email?.toLowerCase() === data.email?.toLowerCase() ? 'email' : 'panNumber', message:'This email or PAN is already registered.' };
    }
    if (resource === 'kyc-documents') {
        const value = (data.documentNumber || '').replace(/\s/g,'').toUpperCase();
        if (data.documentType === 'AADHAAR' && !/^\d{12}$/.test(value)) return { field:'documentNumber', message:'Aadhaar number must contain exactly 12 digits.' };
        if (data.documentType === 'PAN' && !/^[A-Z]{5}\d{4}[A-Z]$/.test(value)) return { field:'documentNumber', message:'Enter a valid 10-character PAN number.' };
        if (data.documentType === 'PASSPORT' && !/^[A-Z][0-9]{7}$/.test(value)) return { field:'documentNumber', message:'Passport number must contain 1 letter followed by 7 digits.' };
        if (data.documentType === 'VOTER ID' && !/^[A-Z]{3}[0-9]{7}$/.test(value)) return { field:'documentNumber', message:'Voter ID must contain 3 letters followed by 7 digits.' };
        data.documentNumber = value;
    }
    if (resource === 'bank-mandates') {
        if (!/^\d{9,18}$/.test(data.accountNumber || '')) return { field:'accountNumber', message:'Account number must contain 9 to 18 digits.' };
        if (!/^[A-Z]{4}0[A-Z0-9]{6}$/i.test(data.ifscCode || '')) return { field:'ifscCode', message:'IFSC must contain 4 letters, 0, then 6 letters or digits.' };
    }
    if (resource === 'nominees' && (Number(data.allocationPercentage) < 0 || Number(data.allocationPercentage) > 100)) return { field:'allocationPercentage', message:'Allocation must be between 0 and 100 percent.' };
    return null;
}

function renderField(f, value = '') {
    const val = value ?? '';
    if (f.type === 'select') return `<div class="field"><label for="${f.name}">${f.label}${f.required?' *':''}</label><select id="${f.name}" name="${f.name}" ${f.required?'required':''}><option value="">Select ${f.label.toLowerCase()}</option>${f.options.map(o => `<option value="${escapeHtml(o)}" ${String(val)===String(o)?'selected':''}>${escapeHtml(o.replaceAll('_',' '))}</option>`).join('')}</select></div>`;
    if (f.type === 'ref') return `<div class="field"><label for="${f.name}">${f.label}${f.required?' *':''}</label><select id="${f.name}" name="${f.name}" ${f.required?'required':''}><option value="">Select ${f.label.toLowerCase()}</option>${(state.cache[f.resource]||[]).map(o => `<option value="${o.id}" ${String(val)===String(o.id)?'selected':''}>${escapeHtml(o[f.display])} (#${o.id})</option>`).join('')}</select></div>`;
    const attrs = Object.entries(f.attrs || {}).map(([k,v]) => `${k}="${v}"`).join(' ');
    return `<div class="field"><label for="${f.name}">${f.label}${f.required?' *':''}</label><input id="${f.name}" name="${f.name}" type="${f.type}" value="${escapeHtml(val)}" ${f.required?'required':''} ${attrs}></div>`;
}

async function openDetails(resource, id) {
    const c = resources[resource];
    let record = (state.cache[resource] || []).find(x => String(x.id) === String(id));
    if (!record) return;

    if (resource === 'schemes') {
        try {
            record = await api(`/schemes/${id}`);
            const index = state.cache.schemes.findIndex(x => String(x.id) === String(id));
            if (index >= 0) state.cache.schemes[index] = { ...state.cache.schemes[index], ...record };
        } catch (error) {
            toast(error.message, true);
            return;
        }
    }

    modalRoot.innerHTML = `<div class="modal-backdrop" data-close-modal><div class="modal" role="dialog" aria-modal="true"><div class="modal-head"><div><h2>${c.singular} details</h2><p>Record #${id}</p></div><button class="close-button" data-close>×</button></div><div class="modal-body"><div class="detail-grid">${Object.entries(record).map(([k,v]) => `<div class="detail-item"><small>${humanize(k)}</small><strong>${escapeHtml(formatDetail(k,v))}</strong></div>`).join('')}</div></div><div class="modal-actions"><button class="button" data-close>Close</button>${c.noEdit?'':`<button class="button primary" data-modal-edit>Edit record</button>`}</div></div></div>`;
    bindModalClose(); document.querySelector('[data-modal-edit]')?.addEventListener('click', () => openForm(resource,id));
}
function humanize(value) { return value.replace(/([A-Z])/g,' $1').replace(/^./,x=>x.toUpperCase()); }
function formatDetail(key,value) { if (/value|amount|nav$/i.test(key) && !/number/i.test(key)) return money(value); if (/At$/i.test(key)) return dateTime(value); if (/Id$/i.test(key)) return `#${value}`; return value ?? '—'; }

function confirmDelete(resource,id) {
    const c = resources[resource];
    modalRoot.innerHTML = `<div class="modal-backdrop" data-close-modal><div class="modal"><div class="modal-head"><div><h2>Delete ${c.singular.toLowerCase()}?</h2><p>This permanently removes record #${id}.</p></div><button class="close-button" data-close>×</button></div><div class="modal-body"><p>This action cannot be undone. Related records may need to be removed first.</p></div><div class="modal-actions"><button class="button" data-close>Cancel</button><button class="button danger" data-confirm-delete>Delete record</button></div></div></div>`;
    bindModalClose(); document.querySelector('[data-confirm-delete]').addEventListener('click', async e => { e.target.disabled=true; try { await api(`/${resource}/${id}`,{method:'DELETE'}); await load(resource,true); closeModal(); toast(`${c.singular} deleted`); await refreshCurrent(); } catch(error) { toast(error.message,true); e.target.disabled=false; } });
}

async function openNavHistory(schemeId) {
    const scheme = (state.cache.schemes || []).find(x => String(x.id) === String(schemeId));
    let history = []; try { history = await api(`/schemes/${schemeId}/nav-history`); } catch(error) { toast(error.message,true); return; }
    modalRoot.innerHTML = `<div class="modal-backdrop" data-close-modal><div class="modal wide"><div class="modal-head"><div><h2>NAV history</h2><p>${escapeHtml(scheme?.schemeName || `Scheme #${schemeId}`)}</p></div><button class="close-button" data-close>×</button></div><div class="modal-body"><form id="navForm"><div class="form-grid"><div class="field"><label>NAV date *</label><input type="date" name="navDate" required value="${new Date().toISOString().slice(0,10)}"></div><div class="field"><label>NAV value *</label><input type="number" name="navValue" min="0.0001" step="0.0001" required></div></div><div style="margin-top:12px"><button class="button primary">+ Add NAV</button></div></form><div class="table-wrap" style="margin-top:20px"><table><thead><tr><th>Date</th><th>NAV</th></tr></thead><tbody>${history.sort((a,b)=>String(b.navDate).localeCompare(String(a.navDate))).map(x=>`<tr><td>${escapeHtml(x.navDate)}</td><td>${money(x.navValue)}</td></tr>`).join('') || '<tr><td colspan="2">No NAV history yet.</td></tr>'}</tbody></table></div></div></div></div>`;
    bindModalClose(); document.querySelector('#navForm').addEventListener('submit', async e => { e.preventDefault(); const data=Object.fromEntries(new FormData(e.target).entries()); data.navValue=Number(data.navValue); try { await api(`/schemes/${schemeId}/nav-history`,{method:'POST',body:JSON.stringify(data)}); await load('schemes', true); await refreshCurrent(); toast('NAV history saved'); openNavHistory(schemeId); } catch(error){ toast(error.message,true); } });
}

async function openStatement(folioId) {
    try {
        const data = await api(`/statements/folio/${folioId}`);
        const folio = data.folio || {};
        const transactions = Array.isArray(data.transactions) ? data.transactions : [];
        modalRoot.innerHTML = `<div class="modal-backdrop" data-close-modal><div class="modal wide statement-modal"><div class="modal-head"><div><h2>Folio statement</h2><p>${escapeHtml(folio.folioNumber || lookup('folios',folioId,'folioNumber'))}</p></div><button class="close-button" data-close>×</button></div><div class="modal-body"><div class="statement-sheet">
      <div class="statement-brand"><div><p class="eyebrow">FundWise statement</p><h2>Investment account summary</h2></div><div><small>Generated on</small><strong>${dateTime(new Date().toISOString())}</strong></div></div>
      <div class="detail-grid statement-summary">
        <div class="detail-item"><small>Folio number</small><strong>${escapeHtml(folio.folioNumber || `#${folioId}`)}</strong></div>
        <div class="detail-item"><small>Status</small><strong>${escapeHtml(folio.status || '—')}</strong></div>
        <div class="detail-item"><small>Current value</small><strong>${money(folio.currentValue)}</strong></div>
        <div class="detail-item"><small>Current units</small><strong>${number(folio.currentUnits)}</strong></div>
        <div class="detail-item"><small>Average NAV</small><strong>${money(folio.averageNav)}</strong></div>
        <div class="detail-item"><small>Investor ID</small><strong>#${escapeHtml(folio.investorId || '—')}</strong></div>
      </div>
      <h3 class="statement-section-title">Transaction history</h3>
      <div class="table-wrap"><table><thead><tr><th>Date</th><th>Type</th><th>Amount</th><th>NAV</th><th>Units</th><th>Status</th></tr></thead><tbody>${transactions.length ? transactions.map(t => `<tr><td>${escapeHtml(t.transactionDate || '—')}</td><td>${escapeHtml(t.transactionType || '—')}</td><td>${money(t.amount)}</td><td>${money(t.nav)}</td><td>${number(t.units)}</td><td>${badge(t.status)}</td></tr>`).join('') : '<tr><td colspan="6">No transactions are available for this folio.</td></tr>'}</tbody></table></div>
    </div></div><div class="modal-actions"><button class="button" data-close>Close</button><button class="button primary" onclick="window.print()">Print statement</button></div></div></div>`;
        bindModalClose();
    } catch(error) { toast(error.message,true); }
}

function bindModalClose() { document.querySelectorAll('[data-close]').forEach(x=>x.addEventListener('click',closeModal)); document.querySelector('[data-close-modal]')?.addEventListener('click',e=>{if(e.target===e.currentTarget)closeModal();}); }
function closeModal() { modalRoot.innerHTML=''; }
async function refreshCurrent() { const route=currentRoute(); if(route==='compliance') await renderCompliance(); else if(resources[route]) await renderResource(route); else await renderDashboard(); }
function toast(message,error=false) { const el=document.createElement('div'); el.className=`toast ${error?'error':''}`; el.textContent=message; toastRoot.append(el); setTimeout(()=>el.remove(),3500); }
function setConnection(ok,message='') { document.querySelector('#gatewayStatus').textContent=demoMode?'Offline demo data':ok?'Connected · port 8080':'Services unavailable'; document.querySelector('.status-dot').classList.toggle('offline',!ok); const banner=document.querySelector('#connectionBanner'); banner.classList.toggle('hidden',ok); banner.textContent=message ? `Backend connection issue: ${message}. Start the FundWise services, then refresh.` : ''; }
function renderError(error) { app.innerHTML=`${pageHead('Connecting to services','FundWise is starting','The interface will reconnect automatically as services become available.')}<div class="card empty"><div class="empty-icon">↻</div><h2>Waiting for the backend</h2><p>${escapeHtml(error.message)}. Retrying automatically in a few seconds…</p><button class="button primary" id="retryButton">Retry now</button></div>`; document.querySelector('#retryButton').addEventListener('click',()=>router(true)); state.retryTimer=setTimeout(()=>router(true),3000); }

window.addEventListener('hashchange',()=>router());
document.querySelector('#refreshButton').addEventListener('click',()=>router(true));
document.querySelector('#menuButton').addEventListener('click',()=>document.querySelector('#sidebar').classList.toggle('open'));
document.querySelector('#globalSearch').addEventListener('input',e=>{ state.search=e.target.value; if(state.currentResource && document.querySelector('#resourceArea')) renderResourceArea(state.currentResource); });
document.addEventListener('keydown',e=>{ if((e.ctrlKey||e.metaKey)&&e.key.toLowerCase()==='k'){e.preventDefault();document.querySelector('#globalSearch').focus();} if(e.key==='Escape')closeModal(); });
if(!location.hash) location.hash='#/dashboard'; else router();
