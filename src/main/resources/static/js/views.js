
const VIEW_RENDERERS = {};

// ---------- Simple CRUD modules ----------

VIEW_RENDERERS.customers = genericCrudView({
    title: 'Customers',
    endpoint: '/customers',
    fields: [
        { name: 'fullName', label: 'Full Name', required: true },
        { name: 'nic', label: 'NIC' },
        { name: 'phone', label: 'Phone', required: true },
        { name: 'address', label: 'Address' }
    ],
    columns: [
        { key: 'id', label: 'ID' },
        { key: 'fullName', label: 'Name' },
        { key: 'nic', label: 'NIC' },
        { key: 'phone', label: 'Phone' },
        { key: 'address', label: 'Address' }
    ],
    invalidates: ['customers']
});

VIEW_RENDERERS.vehiclemakes = genericCrudView({
    title: 'Vehicle Makes',
    subtitle: 'e.g. Toyota, Nissan, Honda — the brand a model belongs to.',
    endpoint: '/vehicle-makes',
    fields: [{ name: 'name', label: 'Make Name', required: true }],
    columns: [{ key: 'id', label: 'ID' }, { key: 'name', label: 'Name' }],
    canDelete: false,
    invalidates: ['vehiclemakes']
});

VIEW_RENDERERS.vehiclemodels = genericCrudView({
    title: 'Vehicle Models',
    endpoint: '/vehicle-models',
    fields: [
        { name: 'makeId', label: 'Make', type: 'select', required: true, optionsSource: { key: 'vehiclemakes', endpoint: '/vehicle-makes', labelFn: r => r.name } },
        { name: 'modelName', label: 'Model Name', required: true },
        { name: 'yearFrom', label: 'Year From', type: 'number' }
    ],
    createQuery: (values) => '?makeId=' + values.makeId,
    buildPayload: (values) => ({ modelName: values.modelName, yearFrom: values.yearFrom }),
    columns: [
        { key: 'id', label: 'ID' },
        { key: 'modelName', label: 'Model' },
        { key: 'yearFrom', label: 'Year From' },
        { key: 'make', label: 'Make', render: r => r.make ? esc(r.make.name) : '—' }
    ],
    canDelete: false,
    invalidates: ['vehiclemodels']
});

VIEW_RENDERERS.servicecategories = genericCrudView({
    title: 'Service Categories',
    endpoint: '/service-categories',
    fields: [
        { name: 'name', label: 'Category Name', required: true },
        { name: 'description', label: 'Description' }
    ],
    columns: [{ key: 'id', label: 'ID' }, { key: 'name', label: 'Name' }, { key: 'description', label: 'Description' }],
    invalidates: ['servicecategories']
});

VIEW_RENDERERS.servicetypes = genericCrudView({
    title: 'Service Types',
    endpoint: '/service-types',
    fields: [
        { name: 'categoryId', label: 'Category', type: 'select', required: true, optionsSource: { key: 'servicecategories', endpoint: '/service-categories', labelFn: r => r.name } },
        { name: 'name', label: 'Service Name', required: true },
        { name: 'basePrice', label: 'Base Price (Rs.)', type: 'number', step: '0.01', required: true },
        { name: 'estimatedMinutes', label: 'Est. Minutes', type: 'number' }
    ],
    columns: [
        { key: 'id', label: 'ID' },
        { key: 'name', label: 'Name' },
        { key: 'category', label: 'Category', render: r => r.category ? esc(r.category.name) : '—' },
        { key: 'basePrice', label: 'Price', render: r => fmtMoney(r.basePrice) },
        { key: 'estimatedMinutes', label: 'Est. Mins' }
    ],
    invalidates: ['servicetypes']
});

VIEW_RENDERERS.suppliers = genericCrudView({
    title: 'Suppliers',
    endpoint: '/suppliers',
    fields: [
        { name: 'name', label: 'Supplier Name', required: true },
        { name: 'contactPerson', label: 'Contact Person' },
        { name: 'phone', label: 'Phone' },
        { name: 'email', label: 'Email' },
        { name: 'address', label: 'Address' }
    ],
    columns: [
        { key: 'id', label: 'ID' }, { key: 'name', label: 'Name' }, { key: 'contactPerson', label: 'Contact' },
        { key: 'phone', label: 'Phone' }, { key: 'email', label: 'Email' }
    ],
    invalidates: ['suppliers']
});

VIEW_RENDERERS.sparepartcategories = genericCrudView({
    title: 'Spare Part Categories',
    endpoint: '/spare-part-categories',
    fields: [{ name: 'name', label: 'Category Name', required: true }],
    columns: [{ key: 'id', label: 'ID' }, { key: 'name', label: 'Name' }],
    canDelete: false,
    invalidates: ['sparepartcategories']
});

VIEW_RENDERERS.spareparts = genericCrudView({
    title: 'Spare Parts Inventory',
    subtitle: 'Rows with stock at or below the reorder level are flagged.',
    endpoint: '/spare-parts',
    fields: [
        { name: 'categoryId', label: 'Category', type: 'select', required: true, optionsSource: { key: 'sparepartcategories', endpoint: '/spare-part-categories', labelFn: r => r.name } },
        { name: 'supplierId', label: 'Supplier', type: 'select', optionsSource: { key: 'suppliers', endpoint: '/suppliers', labelFn: r => r.name } },
        { name: 'partName', label: 'Part Name', required: true },
        { name: 'partNumber', label: 'Part Number', required: true },
        { name: 'unitPrice', label: 'Unit Price', type: 'number', step: '0.01', required: true },
        { name: 'stockQty', label: 'Stock Qty', type: 'number', required: true },
        { name: 'reorderLevel', label: 'Reorder Level', type: 'number' }
    ],
    columns: [
        { key: 'partName', label: 'Part' },
        { key: 'partNumber', label: 'Part #' },
        { key: 'category', label: 'Category', render: r => r.category ? esc(r.category.name) : '—' },
        { key: 'unitPrice', label: 'Price', render: r => fmtMoney(r.unitPrice) },
        { key: 'stockQty', label: 'Stock', render: r => (r.stockQty <= r.reorderLevel)
                ? '<span class="stamp stamp-pending">' + r.stockQty + ' LOW</span>' : r.stockQty },
        { key: 'reorderLevel', label: 'Reorder At' }
    ],
    invalidates: ['spareparts']
});

VIEW_RENDERERS.mechanics = genericCrudView({
    title: 'Mechanics',
    subtitle: 'Mechanic accounts are created via Register (account type: Mechanic).',
    endpoint: '/mechanics',
    fields: [],
    canCreate: false,
    canDelete: false,
    columns: [
        { key: 'id', label: 'ID' }, { key: 'fullName', label: 'Name' },
        { key: 'specialization', label: 'Specialization' }, { key: 'phone', label: 'Phone' }, { key: 'hireDate', label: 'Hired' }
    ]
});

// ---------- Dashboard (admin) ----------
// 5 separate, fully inline $.ajax calls (no shared helper) - each with its own success/error.
VIEW_RENDERERS.dashboard = function (content) {
    content.innerHTML = '<div class="loading">Loading…</div>';

    let customers = [], vehicles = [], pendingBookings = [], lowStock = [], invoices = [];

    const customersPromise = new Promise(function (resolve) {
        $.ajax({
            url: API_BASE + '/customers',
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) { customers = response.body; resolve(); },
            error: function () { resolve(); }
        });
    });

    const vehiclesPromise = new Promise(function (resolve) {
        $.ajax({
            url: API_BASE + '/vehicles',
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) { vehicles = response.body; resolve(); },
            error: function () { resolve(); }
        });
    });

    const pendingBookingsPromise = new Promise(function (resolve) {
        $.ajax({
            url: API_BASE + '/bookings?status=PENDING',
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) { pendingBookings = response.body; resolve(); },
            error: function () { resolve(); }
        });
    });

    const lowStockPromise = new Promise(function (resolve) {
        $.ajax({
            url: API_BASE + '/spare-parts/low-stock',
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) { lowStock = response.body; resolve(); },
            error: function () { resolve(); }
        });
    });

    const invoicesPromise = new Promise(function (resolve) {
        $.ajax({
            url: API_BASE + '/invoices',
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) { invoices = response.body; resolve(); },
            error: function () { resolve(); }
        });
    });

    let serviceTypes = [];
    const serviceTypesPromise = new Promise(function (resolve) {
        $.ajax({
            url: API_BASE + '/service-types',
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) { serviceTypes = response.body || []; resolve(); },
            error: function () { resolve(); }
        });
    });

    Promise.all([customersPromise, vehiclesPromise, pendingBookingsPromise, lowStockPromise, invoicesPromise, serviceTypesPromise]).then(function () {
        const unpaid = invoices.filter(i => i.status !== 'PAID');

        content.innerHTML = '';

        // ---- Hero ----
        const hero = el('div', { class: 'hero' }, [
            el('span', { class: 'hero-eyebrow' }, 'SMART GARAGE · ' + (state.username || 'admin').toUpperCase()),
            el('h2', { class: 'hero-title' }, ['Everything on the floor, ', el('span', { class: 'accent' }, 'in one view'), '.']),
            el('p', { class: 'hero-sub' }, 'Vehicles checked in, bookings waiting on confirmation, and stock that needs reordering — the whole garage at a glance.'),
            el('div', { class: 'hero-quick' }, [
                quickPill('Vehicles', vehicles.length, () => setView('vehicles', 'Vehicles')),
                quickPill('Pending Bookings', pendingBookings.length, () => setView('bookings', 'Bookings')),
                quickPill('Job Cards', null, () => setView('jobcards', 'Job Cards')),
                quickPill('Unpaid Invoices', unpaid.length, () => setView('invoices', 'Invoices'))
            ])
        ]);
        content.appendChild(hero);

        // ---- Services on offer ----
        if (serviceTypes.length > 0) {
            content.appendChild(el('div', { class: 'section-label' }, 'SERVICES ON OFFER'));
            const grid = el('div', { class: 'service-grid' });
            serviceTypes.slice(0, 8).forEach(st => {
                grid.appendChild(el('div', { class: 'service-card' }, [
                    el('div', { class: 'sc-name' }, st.name),
                    el('div', { class: 'sc-price' }, fmtMoney(st.basePrice)),
                    el('div', { class: 'sc-meta' }, st.category ? esc(st.category.name) : '\u00A0')
                ]));
            });
            content.appendChild(grid);
        }

        // ---- Key numbers (vehicles + bookings first) ----
        const strip = el('div', { class: 'stat-strip' }, [
            statCard(vehicles.length, 'Vehicles Registered'),
            statCard(pendingBookings.length, 'Pending Bookings'),
            statCard(customers.length, 'Customers'),
            statCard(lowStock.length, 'Low Stock Parts', lowStock.length > 0),
            statCard(unpaid.length, 'Unpaid Invoices', unpaid.length > 0)
        ]);
        content.appendChild(strip);

        const panel = el('div', { class: 'panel' });
        panel.appendChild(el('div', { class: 'panel-head' }, el('h2', {}, 'Low Stock Parts')));
        panel.appendChild(renderTable(
            [{ key: 'partName', label: 'Part' }, { key: 'partNumber', label: 'Part #' },
                { key: 'stockQty', label: 'Stock' }, { key: 'reorderLevel', label: 'Reorder At' }],
            lowStock, { emptyText: 'Stock levels look healthy.' }
        ));
        content.appendChild(panel);
    });
};

function quickPill(label, count, onClick) {
    const item = el('div', { class: 'hero-quick-item' }, [
        count === null ? null : el('span', { class: 'n mono' }, String(count)),
        label
    ]);
    item.addEventListener('click', onClick);
    return item;
}

function statCard(num, label, warn) {
    return el('div', { class: 'stat-card' + (warn ? ' warn' : '') }, [
        el('div', { class: 'num mono' }, String(num)),
        el('div', { class: 'label' }, label)
    ]);
}
