
VIEW_RENDERERS.purchaseorders = function (content) {
    content.innerHTML = '';
    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, el('h2', {}, 'Purchase Orders')));

    Promise.all([
        loadOptions('suppliers', '/suppliers', r => r.name),
        loadOptions('spareparts', '/spare-parts', r => r.partName + ' (' + r.partNumber + ')')
    ]).then(function (results) {
        const supplierOpts = results[0], partOpts = results[1];

        const supplierSelect = el('select', {}, supplierOpts.map(o => el('option', { value: o.value }, o.label)));
        const itemsWrap = el('div', { style: 'margin:10px 0' });
        let items = [];

        function renderItemRows() {
            itemsWrap.innerHTML = '';
            items.forEach((item, idx) => {
                const row = el('div', { class: 'form-grid', style: 'margin-bottom:8px' });
                const partSel = el('select', {}, partOpts.map(o => el('option', { value: o.value, selected: String(o.value) === String(item.partId) }, o.label)));
                partSel.addEventListener('change', () => items[idx].partId = Number(partSel.value));
                const qtyInput = el('input', { type: 'number', min: '1', value: item.quantity });
                qtyInput.addEventListener('input', () => items[idx].quantity = Number(qtyInput.value));
                const costInput = el('input', { type: 'number', min: '0', step: '0.01', value: item.unitCost });
                costInput.addEventListener('input', () => items[idx].unitCost = Number(costInput.value));
                const rmBtn = el('button', { class: 'btn btn-danger', type: 'button' }, 'Remove');
                rmBtn.addEventListener('click', () => { items.splice(idx, 1); renderItemRows(); });

                row.appendChild(el('div', { class: 'field' }, [el('label', {}, 'Part'), partSel]));
                row.appendChild(el('div', { class: 'field' }, [el('label', {}, 'Qty'), qtyInput]));
                row.appendChild(el('div', { class: 'field' }, [el('label', {}, 'Unit Cost'), costInput]));
                row.appendChild(el('div', { class: 'field' }, [el('label', {}, '\u00A0'), rmBtn]));
                itemsWrap.appendChild(row);
            });
        }

        const addItemBtn = el('button', { class: 'btn btn-secondary btn-sm', type: 'button' }, '+ Add Item');
        addItemBtn.addEventListener('click', () => {
            items.push({ partId: partOpts[0] ? partOpts[0].value : null, quantity: 1, unitCost: 0 });
            renderItemRows();
        });

        const submitBtn = el('button', { class: 'btn btn-primary', type: 'button' }, 'Create Purchase Order');
        submitBtn.addEventListener('click', function () {
            if (items.length === 0) { toast('Add at least one item.', true); return; }

            $.ajax({
                url: API_BASE + '/purchase-orders',
                type: 'POST',
                contentType: 'application/json',
                headers: { 'Authorization': 'Bearer ' + state.token },
                data: JSON.stringify({ supplierId: Number(supplierSelect.value), items: items }),
                success: function () {
                    toast('Purchase order created.');
                    items = []; renderItemRows(); renderList();
                },
                error: function (xhr) {
                    toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not create purchase order', true);
                }
            });
        });

        panel.appendChild(el('div', { class: 'field', style: 'max-width:320px' }, [el('label', {}, 'Supplier'), supplierSelect]));
        panel.appendChild(itemsWrap);
        panel.appendChild(el('div', { style: 'display:flex;gap:10px;margin-bottom:10px' }, [addItemBtn, submitBtn]));

        const listWrap = el('div');
        panel.appendChild(listWrap);
        content.appendChild(panel);

        function renderList() {
            listWrap.innerHTML = '<div class="loading">Loading…</div>';

            $.ajax({
                url: API_BASE + '/purchase-orders',
                type: 'GET',
                contentType: 'application/json',
                headers: { 'Authorization': 'Bearer ' + state.token },
                success: function (response) {
                    const rows = response.body;
                    listWrap.innerHTML = '';
                    listWrap.appendChild(renderTable([
                        { key: 'id', label: 'ID' },
                        { key: 'supplier', label: 'Supplier', render: r => r.supplier ? esc(r.supplier.name) : '—' },
                        { key: 'orderDate', label: 'Ordered', render: r => esc((r.orderDate || '').replace('T', ' ')) },
                        { key: 'totalAmount', label: 'Total', render: r => fmtMoney(r.totalAmount) },
                        { key: 'status', label: 'Status', render: r => statusStamp(r.status) },
                        { key: '_actions', label: '', render: (row) => {
                                const wrap = el('div', { style: 'display:flex;gap:6px' });
                                if (row.status === 'PENDING') {
                                    const rec = el('button', { class: 'btn btn-primary btn-sm' }, 'Receive');
                                    rec.addEventListener('click', function () {
                                        $.ajax({
                                            url: API_BASE + '/purchase-orders/' + row.id + '/receive',
                                            type: 'PATCH',
                                            contentType: 'application/json',
                                            headers: { 'Authorization': 'Bearer ' + state.token },
                                            success: function () { toast('Received — stock updated.'); invalidateCache('spareparts'); renderList(); },
                                            error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not receive order', true); }
                                        });
                                    });
                                    const can = el('button', { class: 'btn btn-danger' }, 'Cancel');
                                    can.addEventListener('click', function () {
                                        $.ajax({
                                            url: API_BASE + '/purchase-orders/' + row.id + '/cancel',
                                            type: 'PATCH',
                                            contentType: 'application/json',
                                            headers: { 'Authorization': 'Bearer ' + state.token },
                                            success: function () { toast('Cancelled.'); renderList(); },
                                            error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not cancel order', true); }
                                        });
                                    });
                                    wrap.appendChild(rec); wrap.appendChild(can);
                                }
                                return wrap;
                            }}
                    ], rows));
                },
                error: function (xhr) {
                    listWrap.innerHTML = '';
                    toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not load purchase orders', true);
                }
            });
        }

        renderList();
    });
};

// ============================================================
// Invoices (admin) — generated from completed job cards + payments
// ============================================================

VIEW_RENDERERS.invoices = function (content) {
    content.innerHTML = '';
    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, [
        el('h2', {}, 'Invoices'),
        el('span', { class: 'helper' }, 'Generate invoices from the Job Cards tab once a job is Completed.')
    ]));

    const listWrap = el('div');
    panel.appendChild(listWrap);
    content.appendChild(panel);

    function renderList() {
        listWrap.innerHTML = '<div class="loading">Loading…</div>';

        $.ajax({
            url: API_BASE + '/invoices',
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) {
                const rows = response.body;
                listWrap.innerHTML = '';
                listWrap.appendChild(renderTable([
                    { key: 'invoiceNumber', label: 'Invoice #' },
                    { key: 'customer', label: 'Customer', render: r => (r.jobCard && r.jobCard.booking && r.jobCard.booking.customer) ? esc(r.jobCard.booking.customer.fullName) : '—' },
                    { key: 'issueDate', label: 'Issued' },
                    { key: 'subtotal', label: 'Subtotal', render: r => fmtMoney(r.subtotal) },
                    { key: 'tax', label: 'Tax', render: r => fmtMoney(r.tax) },
                    { key: 'totalAmount', label: 'Total', render: r => fmtMoney(r.totalAmount) },
                    { key: 'status', label: 'Status', render: r => statusStamp(r.status) },
                    { key: '_pay', label: '', render: paymentAction }
                ], rows));
            },
            error: function (xhr) {
                listWrap.innerHTML = '';
                toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not load invoices', true);
            }
        });
    }

    function paymentAction(row) {
        if (row.status === 'PAID') return document.createTextNode('—');
        const wrap = el('div', { style: 'display:flex;gap:6px;align-items:center' });
        const amountInput = el('input', { type: 'number', min: '0', step: '0.01', placeholder: 'Amount', style: 'width:90px' });
        const methodSelect = el('select', {}, ['CASH', 'CARD', 'BANK_TRANSFER'].map(m => el('option', { value: m }, m)));
        const payBtn = el('button', { class: 'btn btn-primary btn-sm' }, 'Record Payment');
        payBtn.addEventListener('click', function () {
            if (!amountInput.value) { toast('Enter an amount.', true); return; }

            $.ajax({
                url: API_BASE + '/payments',
                type: 'POST',
                contentType: 'application/json',
                headers: { 'Authorization': 'Bearer ' + state.token },
                data: JSON.stringify({ invoiceId: row.id, amount: Number(amountInput.value), paymentMethod: methodSelect.value }),
                success: function () { toast('Payment recorded.'); renderList(); },
                error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not record payment', true); }
            });
        });
        wrap.appendChild(amountInput); wrap.appendChild(methodSelect); wrap.appendChild(payBtn);
        return wrap;
    }

    renderList();
};
