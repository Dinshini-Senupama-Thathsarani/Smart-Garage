VIEW_RENDERERS.home = function (content) {
    content.innerHTML = '';

    const hero = el('div', { class: 'site-hero' }, [
        el('div', { class: 'site-hero-copy' }, [
            el('span', { class: 'hero-eyebrow' }, 'SMART SERVICE · BETTER CARE'),
            el('h1', { class: 'site-hero-title' }, ['Everything your car needs, ', el('span', { class: 'accent' }, 'under one roof'), '.']),
            el('p', { class: 'hero-sub' }, 'Genuine spare parts, transparent job cards, and a workshop team that keeps you posted at every step — from booking to the final invoice.'),
            el('div', { class: 'hero-actions' }, [
                (() => { const b = el('button', { class: 'btn btn-primary' }, 'Explore Services'); b.addEventListener('click', () => setView('sparepartscatalog', 'Spare Parts Catalog')); return b; })(),
                (() => { const b = el('button', { class: 'btn btn-secondary' }, 'Contact Us'); b.addEventListener('click', () => setView('contact', 'Contact Us')); return b; })()
            ])
        ]),
        el('div', { html:
                '<svg class="site-hero-art" viewBox="0 0 400 160">' +
                '<path d="M18 118 Q8 88 42 82 L72 52 Q102 30 165 30 L258 30 Q300 30 332 58 L368 82 Q390 88 384 118 Z" fill="none" stroke="#E23784" stroke-width="3"/>' +
                '<path d="M96 82 L128 50 Q148 38 178 38 L232 38" fill="none" stroke="#E23784" stroke-width="2.5"/>' +
                '<circle cx="104" cy="122" r="24" fill="none" stroke="#E23784" stroke-width="4"/>' +
                '<circle cx="300" cy="122" r="24" fill="none" stroke="#E23784" stroke-width="4"/>' +
                '</svg>'
        })
    ]);
    content.appendChild(hero);

    // ---- Live counters (real data, no invented numbers) ----
    const countsWrap = el('div', { class: 'stat-strip' });
    content.appendChild(countsWrap);

    // ---- Brand story ----
    const story = el('div', { class: 'panel story-panel' }, [
        el('span', { class: 'section-label' }, 'HOW SMART GARAGE STARTED'),
        el('p', { class: 'story-text' },
            'Smart Garage began as a single service bay and a promise: no guesswork, no inflated bills, no vehicle leaving without the owner knowing exactly what was done to it. ' +
            'That promise grew into a full workshop — spare parts sourced and tracked properly, job cards logged part by part, and every invoice matching the work on paper. ' +
            'The tools changed; the promise didn\u2019t.'),
        el('p', { class: 'helper', style: 'margin-top:10px' }, 'Placeholder copy — swap this paragraph for your garage\u2019s real story in views-site.js.')
    ]);
    content.appendChild(story);

    // ---- Quick links ----
    content.appendChild(el('div', { class: 'section-label' }, 'GET STARTED'));
    const quickGrid = el('div', { class: 'service-grid' });
    const quickLinks = [
        { title: 'Spare Parts Catalog', desc: 'Browse genuine parts by category before you book.', view: 'sparepartscatalog', label: 'Spare Parts Catalog' },
        { title: state.role === 'CUSTOMER' ? 'My Vehicles' : (state.role === 'MECHANIC' ? 'Job Cards' : 'Dashboard'), desc: state.role === 'CUSTOMER' ? 'Manage your registered vehicles and QR passes.' : (state.role === 'MECHANIC' ? 'See what\u2019s open, in progress, and completed.' : 'The full front-desk and workshop overview.'), view: state.role === 'CUSTOMER' ? 'myvehicles' : (state.role === 'MECHANIC' ? 'jobcards' : 'dashboard'), label: state.role === 'CUSTOMER' ? 'My Vehicles' : (state.role === 'MECHANIC' ? 'Job Cards' : 'Dashboard') },
        { title: 'Contact Us', desc: 'Address, phone, email and socials in one place.', view: 'contact', label: 'Contact Us' }
    ];
    quickLinks.forEach(q => {
        const card = el('div', { class: 'service-card', style: 'cursor:pointer' }, [
            el('div', { class: 'sc-name' }, q.title),
            el('div', { class: 'sc-meta' }, q.desc)
        ]);
        card.addEventListener('click', () => setView(q.view, q.label));
        quickGrid.appendChild(card);
    });
    content.appendChild(quickGrid);

    // ---- Customer reviews (placeholder — swap for real reviews) ----
    content.appendChild(el('div', { class: 'section-label' }, 'WHAT CUSTOMERS SAY'));
    const REVIEWS = [
        { quote: 'Booked online, watched the job card move from pending to completed, and the invoice matched exactly what was discussed.', author: 'Sample review', vehicle: 'Replace with a real customer quote' },
        { quote: 'Genuine parts, clear pricing on the spare parts catalog before I even booked the service.', author: 'Sample review', vehicle: 'Replace with a real customer quote' },
        { quote: 'Easy to track my vehicle\u2019s service history and know exactly what was done each visit.', author: 'Sample review', vehicle: 'Replace with a real customer quote' }
    ];
    const reviewGrid = el('div', { class: 'review-grid' });
    REVIEWS.forEach(r => {
        reviewGrid.appendChild(el('div', { class: 'review-card' }, [
            el('span', { class: 'sample-badge' }, 'SAMPLE — REPLACE ME'),
            el('div', { class: 'review-stars' }, '★★★★★'),
            el('p', { class: 'review-quote' }, '\u201C' + r.quote + '\u201D'),
            el('div', { class: 'review-author' }, [r.author, el('span', { class: 'veh' }, r.vehicle)])
        ]));
    });
    content.appendChild(reviewGrid);

    // Fill the live counters (fire-and-forget; page already rendered)
    $.ajax({
        url: API_BASE + '/vehicles', type: 'GET', contentType: 'application/json',
        headers: { 'Authorization': 'Bearer ' + state.token },
        success: function (r) { countsWrap.appendChild(statCard((r.body || []).length, 'Vehicles Registered')); },
        error: function () {}
    });
    $.ajax({
        url: API_BASE + '/service-types', type: 'GET', contentType: 'application/json',
        headers: { 'Authorization': 'Bearer ' + state.token },
        success: function (r) { countsWrap.appendChild(statCard((r.body || []).length, 'Services Offered')); },
        error: function () {}
    });
    $.ajax({
        url: API_BASE + '/spare-parts', type: 'GET', contentType: 'application/json',
        headers: { 'Authorization': 'Bearer ' + state.token },
        success: function (r) { countsWrap.appendChild(statCard((r.body || []).length, 'Parts In Catalog')); },
        error: function () {}
    });
};

// ---------- Spare Parts Catalog (customer-facing browsing) ----------
// Shop-style light cards on purpose — easier to scan prices/stock than the
// dark admin tables. "Condition" isn't a real field on SparePart yet, so it's
// read from the part name (see detectCondition below) rather than invented;
// add a real column later if you want structured filtering by condition.
VIEW_RENDERERS.sparepartscatalog = function (content) {
    content.innerHTML = '';

    content.appendChild(el('p', { class: 'helper', style: 'margin:-4px 0 16px' },
        'Tip: name parts like "Head Lamp (Brand New)" or "Tail Lamp (Recondition)" — the condition tag below is read from the part name.'));

    const layout = el('div', { class: 'catalog-layout' });
    const sidebar = el('aside', { class: 'catalog-sidebar' });
    sidebar.appendChild(el('h3', { class: 'section-label' }, 'CATEGORIES'));
    const catList = el('div', { class: 'category-list' });
    sidebar.appendChild(catList);

    const mainCol = el('div', { class: 'catalog-main' });
    const grid = el('div', { class: 'catalog-grid light' });
    mainCol.appendChild(grid);

    layout.appendChild(sidebar);
    layout.appendChild(mainCol);
    content.appendChild(layout);

    let allParts = [];
    let activeCategory = 'All';

    function renderCategoryList(categories) {
        catList.innerHTML = '';
        ['All'].concat(categories.map(c => c.name)).forEach(name => {
            const btn = el('button', { class: 'category-pill' + (name === activeCategory ? ' active' : '') }, name);
            btn.addEventListener('click', () => { activeCategory = name; renderCategoryList(categories); renderGrid(); });
            catList.appendChild(btn);
        });
    }

    function renderGrid() {
        const filtered = activeCategory === 'All' ? allParts : allParts.filter(p => p.category && p.category.name === activeCategory);
        grid.innerHTML = '';
        if (filtered.length === 0) {
            grid.appendChild(el('div', { class: 'loading' }, 'No parts in this category yet.'));
            return;
        }
        filtered.forEach(p => {
            const low = p.stockQty <= (p.reorderLevel || 0);
            const out = p.stockQty <= 0;
            const cond = detectCondition(p.partName);
            grid.appendChild(el('div', { class: 'part-card' }, [
                el('div', { class: 'part-card-icon', html: categoryIcon(p.category ? p.category.name : '') }),
                el('div', { class: 'part-card-tag' }, p.category ? p.category.name : 'Uncategorised'),
                el('div', { class: 'part-card-name' }, p.partName),
                el('div', { class: 'part-card-num mono' }, 'PN: ' + p.partNumber),
                el('span', { class: 'condition-badge ' + cond.cls, style: 'margin-top:8px' }, cond.label),
                el('div', { class: 'part-card-foot' }, [
                    el('span', { class: 'part-card-price mono' }, fmtMoney(p.unitPrice)),
                    el('span', { class: 'part-card-stock ' + (out ? 'out' : (low ? 'low' : 'ok')) },
                        out ? 'Out of stock' : (low ? 'Low stock' : 'In stock'))
                ])
            ]));
        });
    }

    Promise.all([
        new Promise(resolve => $.ajax({
            url: API_BASE + '/spare-part-categories', type: 'GET', contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: r => resolve(r.body || []), error: () => resolve([])
        })),
        new Promise(resolve => $.ajax({
            url: API_BASE + '/spare-parts', type: 'GET', contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: r => resolve(r.body || []), error: () => resolve([])
        }))
    ]).then(([categories, parts]) => {
        allParts = parts;
        renderCategoryList(categories);
        renderGrid();
    });
};

function detectCondition(name) {
    const n = (name || '').toLowerCase();
    if (n.includes('recondition') || n.includes('recon')) return { cls: 'recon', label: 'Reconditioned' };
    if (n.includes('used')) return { cls: 'used', label: 'Used' };
    return { cls: 'new', label: 'Brand New' };
}

function categoryIcon(name) {
    const n = (name || '').toLowerCase();
    const icons = {
        lights: '<svg viewBox="0 0 24 24" width="34" height="34"><circle cx="12" cy="10" r="6" fill="none" stroke="currentColor" stroke-width="1.6"/><path d="M9 20h6M10 22h4" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/><path d="M12 6v2M8.5 8.5l1.4 1.4M15.5 8.5l-1.4 1.4" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/></svg>',
        brake: '<svg viewBox="0 0 24 24" width="34" height="34"><circle cx="12" cy="12" r="8" fill="none" stroke="currentColor" stroke-width="1.6"/><circle cx="12" cy="12" r="3" fill="none" stroke="currentColor" stroke-width="1.6"/><path d="M12 4v3M12 17v3M4 12h3M17 12h3" stroke="currentColor" stroke-width="1.4"/></svg>',
        filter: '<svg viewBox="0 0 24 24" width="34" height="34"><path d="M4 5h16l-6 8v6l-4 2v-8L4 5z" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/></svg>',
        engine: '<svg viewBox="0 0 24 24" width="34" height="34"><circle cx="12" cy="12" r="3.4" fill="none" stroke="currentColor" stroke-width="1.6"/><path d="M12 3v2.4M12 18.6V21M21 12h-2.4M5.4 12H3M18 6l-1.6 1.6M7.6 16.4L6 18M18 18l-1.6-1.6M7.6 7.6L6 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>',
        electric: '<svg viewBox="0 0 24 24" width="34" height="34"><path d="M13 2 4 14h6l-1 8 9-12h-6l1-8z" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/></svg>',
        suspension: '<svg viewBox="0 0 24 24" width="34" height="34"><path d="M7 3v4M7 8l3 1-3 1 3 1-3 1 3 1-3 1v4" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"/><circle cx="17" cy="18" r="3" fill="none" stroke="currentColor" stroke-width="1.6"/></svg>',
        default: '<svg viewBox="0 0 24 24" width="34" height="34"><path d="M14.7 6.3a4 4 0 0 0-5.4 5.4L4 17l3 3 5.3-5.3a4 4 0 0 0 5.4-5.4l-2.5 2.5-2-2 2.5-2.5z" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"/></svg>'
    };
    if (n.includes('light')) return icons.lights;
    if (n.includes('brake')) return icons.brake;
    if (n.includes('filter')) return icons.filter;
    if (n.includes('engine') || n.includes('cooling') || n.includes('transmission')) return icons.engine;
    if (n.includes('electric')) return icons.electric;
    if (n.includes('suspension') || n.includes('steering')) return icons.suspension;
    return icons.default;
}

// ---------- Contact Us ----------
VIEW_RENDERERS.contact = function (content) {
    content.innerHTML = '';

    // NOTE: placeholder details — replace with the real ones for your garage
    // (the footer on every page uses the same values in index.html).
    const CONTACT = {
        address: '123 Workshop Road, Colombo',
        email: 'support@smartgarage.lk',
        phone: '+94 77 000 0000'
    };

    const wrap = el('div', { class: 'contact-grid' });

    wrap.appendChild(el('div', { class: 'panel' }, [
        el('span', { class: 'section-label' }, 'NEED HELP?'),
        contactRow('Address', CONTACT.address),
        contactRow('Email', CONTACT.email, 'mailto:' + CONTACT.email),
        contactRow('Phone', CONTACT.phone, 'tel:' + CONTACT.phone.replace(/\s+/g, '')),
        el('div', { class: 'section-label', style: 'margin-top:22px' }, 'CONNECT WITH US'),
        el('div', { class: 'social-row' }, ['facebook', 'instagram', 'whatsapp'].map(socialIcon))
    ]));

    wrap.appendChild(el('div', { class: 'panel' }, [
        el('span', { class: 'section-label' }, 'SEND A MESSAGE'),
        el('p', { class: 'helper', style: 'margin-bottom:14px' }, 'This opens your email app addressed to us — there\u2019s no message inbox wired up on the server yet.'),
        (() => {
            const form = el('form');
            const nameInput = el('input', { type: 'text', placeholder: 'Your name', style: 'margin-bottom:10px' });
            const msgInput = el('textarea', { placeholder: 'How can we help?', rows: '4', style: 'width:100%;padding:10px 12px;border:1.5px solid var(--line);border-radius:var(--radius);background:var(--surface-2);color:var(--text);resize:vertical' });
            const sendBtn = el('button', { type: 'submit', class: 'btn btn-primary', style: 'margin-top:12px' }, 'Send');
            Object.assign(nameInput.style, { width: '100%', padding: '10px 12px', border: '1.5px solid var(--line)', borderRadius: 'var(--radius)', background: 'var(--surface-2)', color: 'var(--text)' });
            form.appendChild(nameInput);
            form.appendChild(msgInput);
            form.appendChild(sendBtn);
            form.addEventListener('submit', e => {
                e.preventDefault();
                const subject = encodeURIComponent('Message from ' + (nameInput.value || 'website visitor'));
                const body = encodeURIComponent(msgInput.value || '');
                window.location.href = 'mailto:' + CONTACT.email + '?subject=' + subject + '&body=' + body;
            });
            return form;
        })()
    ]));

    content.appendChild(wrap);
};

function contactRow(label, value, href) {
    const valueNode = href ? el('a', { href: href }, value) : el('span', {}, value);
    return el('div', { style: 'margin-bottom:14px' }, [
        el('div', { class: 'helper', style: 'text-transform:uppercase;letter-spacing:.05em;font-size:10.5px;margin-bottom:2px' }, label),
        el('div', { style: 'font-weight:600;color:var(--text)' }, valueNode)
    ]);
}

function socialIcon(name) {
    const paths = {
        facebook: '<path d="M13 21v-8h2.6l.4-3H13V8.2c0-.9.3-1.5 1.6-1.5H16V4.1C15.6 4 14.6 4 13.6 4 11.4 4 10 5.3 10 7.8V10H7.4v3H10v8h3z" fill="currentColor"/>',
        instagram: '<rect x="4" y="4" width="16" height="16" rx="5" fill="none" stroke="currentColor" stroke-width="1.6"/><circle cx="12" cy="12" r="3.6" fill="none" stroke="currentColor" stroke-width="1.6"/><circle cx="16.6" cy="7.4" r="1" fill="currentColor"/>',
        whatsapp: '<path d="M12 3a9 9 0 0 0-7.8 13.5L3 21l4.7-1.2A9 9 0 1 0 12 3z" fill="none" stroke="currentColor" stroke-width="1.5"/><path d="M8.5 8.6c.2-.4.5-.4.7-.4h.5c.2 0 .4 0 .6.4.2.5.7 1.6.7 1.7.1.1.1.3 0 .4-.1.2-.2.3-.3.5-.1.1-.3.3-.1.6.2.4 1 1.5 2.1 2.4 1.4 1.1 1.7 1 1.9.9.2-.1.9-1 1.1-1.3.2-.3.4-.2.7-.1.3.1 1.7.8 2 .9.3.2.5.2.6.4.1.2.1.9-.2 1.5-.3.6-1.6 1.3-2.2 1.3-.6 0-1.4.1-4.5-1.9-2.5-1.6-3.7-3.9-3.9-4.3-.2-.4-1.2-1.8-1.2-3.1 0-1.3.6-1.9.8-2.2z" fill="currentColor"/>'
    };
    return el('a', { class: 'social-icon', href: '#', 'aria-label': name, html: '<svg viewBox="0 0 24 24" width="18" height="18">' + paths[name] + '</svg>' });
}
