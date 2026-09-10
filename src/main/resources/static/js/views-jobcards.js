

VIEW_RENDERERS.jobcards = function (content) {
    content.innerHTML = '';
    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, [
        el('h2', {}, 'Job Cards'),
        el('span', { class: 'helper' }, 'Assign mechanics, log parts used (auto-deducts stock), and complete work orders.')
    ]));

    const tabs = el('div', { class: 'tabs' });
    const statuses = ['ALL', 'OPEN', 'IN_PROGRESS', 'COMPLETED'];
    let activeStatus = 'ALL';
    statuses.forEach(s => {
        const tabBtn = el('button', { class: 'tab-btn' + (s === activeStatus ? ' active' : ''), type: 'button' }, s.replace('_', ' '));
        tabBtn.addEventListener('click', () => {
            activeStatus = s;
            tabs.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            tabBtn.classList.add('active');
            renderList();
        });
        tabs.appendChild(tabBtn);
    });
    panel.appendChild(tabs);

    const listWrap = el('div');
    panel.appendChild(listWrap);
    content.appendChild(panel);

    Promise.all([
        loadOptions('mechanics', '/mechanics', r => r.fullName),
        loadOptions('spareparts', '/spare-parts', r => r.partName + ' (' + r.partNumber + ', stock ' + r.stockQty + ')')
    ]).then(function (results) {
        const mechanicOpts = results[0], partOpts = results[1];

        function renderList() {
            listWrap.innerHTML = '<div class="loading">Loading…</div>';
            const query = activeStatus === 'ALL' ? '' : ('?status=' + activeStatus);

            $.ajax({
                url: API_BASE + '/job-cards' + query,
                type: 'GET',
                contentType: 'application/json',
                headers: { 'Authorization': 'Bearer ' + state.token },
                success: function (response) {
                    const rows = response.body;
                    listWrap.innerHTML = '';
                    if (rows.length === 0) {
                        listWrap.appendChild(el('div', { class: 'loading' }, 'No job cards in this status.'));
                        return;
                    }
                    rows.forEach(jc => listWrap.appendChild(jobCardCard(jc)));
                },
                error: function (xhr) {
                    listWrap.innerHTML = '';
                    toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not load job cards', true);
                }
            });
        }

        function jobCardCard(jc) {
            const booking = jc.booking || {};
            const vehicle = booking.vehicle || {};
            const customer = booking.customer || {};

            const card = el('div', { class: 'panel', style: 'margin-bottom:14px;' });
            card.appendChild(el('div', { class: 'panel-head' }, [
                el('div', {}, [
                    el('h2', { style: 'font-size:16px' }, 'Job Card #' + jc.id + ' — ' + esc(vehicle.plateNumber || '—')),
                    el('span', { class: 'helper' }, esc(customer.fullName || '') + ' · Labor: ' + fmtMoney(jc.laborCost) + ' · Total: ' + fmtMoney(jc.totalCost))
                ]),
                statusStampNode(jc.status)
            ]));

            const mechList = (jc.jobCardMechanics || []).map(m => (m.mechanic ? m.mechanic.fullName : '?') + (m.roleInJob ? ' (' + m.roleInJob + ')' : '')).join(', ') || 'None assigned yet';
            card.appendChild(el('p', { class: 'helper' }, 'Mechanics: ' + esc(mechList)));

            const partsList = (jc.jobCardParts || []).map(p => (p.part ? p.part.partName : '?') + ' x' + p.quantityUsed).join(', ') || 'None logged yet';
            card.appendChild(el('p', { class: 'helper' }, 'Parts used: ' + esc(partsList)));

            if (jc.status !== 'COMPLETED') {
                const actionsRow = el('div', { class: 'form-grid', style: 'align-items:end;margin-top:10px' });

                const mechSelect = el('select', {}, mechanicOpts.map(o => el('option', { value: o.value }, o.label)));
                const roleInput = el('input', { type: 'text', placeholder: 'Role (optional)' });
                const assignBtn = el('button', { class: 'btn btn-secondary btn-sm' }, 'Assign Mechanic');
                assignBtn.addEventListener('click', function () {
                    $.ajax({
                        url: API_BASE + '/job-cards/' + jc.id + '/mechanics',
                        type: 'POST',
                        contentType: 'application/json',
                        headers: { 'Authorization': 'Bearer ' + state.token },
                        data: JSON.stringify({ mechanicId: Number(mechSelect.value), roleInJob: roleInput.value }),
                        success: function () { toast('Mechanic assigned.'); renderList(); },
                        error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not assign mechanic', true); }
                    });
                });
                actionsRow.appendChild(el('div', { class: 'field' }, [el('label', {}, 'Mechanic'), mechSelect]));
                actionsRow.appendChild(el('div', { class: 'field' }, [el('label', {}, 'Role'), roleInput]));
                actionsRow.appendChild(el('div', { class: 'field' }, [el('label', {}, '\u00A0'), assignBtn]));

                const partSelect = el('select', {}, partOpts.map(o => el('option', { value: o.value }, o.label)));
                const qtyInput = el('input', { type: 'number', min: '1', value: '1' });
                const useBtn = el('button', { class: 'btn btn-secondary btn-sm' }, 'Log Part Used');
                useBtn.addEventListener('click', function () {
                    $.ajax({
                        url: API_BASE + '/job-cards/' + jc.id + '/parts',
                        type: 'POST',
                        contentType: 'application/json',
                        headers: { 'Authorization': 'Bearer ' + state.token },
                        data: JSON.stringify({ partId: Number(partSelect.value), quantity: Number(qtyInput.value) }),
                        success: function () { toast('Part logged, stock updated.'); invalidateCache('spareparts'); renderList(); },
                        error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not log part', true); }
                    });
                });
                actionsRow.appendChild(el('div', { class: 'field' }, [el('label', {}, 'Spare Part'), partSelect]));
                actionsRow.appendChild(el('div', { class: 'field' }, [el('label', {}, 'Qty'), qtyInput]));
                actionsRow.appendChild(el('div', { class: 'field' }, [el('label', {}, '\u00A0'), useBtn]));

                card.appendChild(actionsRow);

                const statusRow = el('div', { style: 'display:flex;gap:8px;margin-top:12px' });
                if (jc.status === 'OPEN') {
                    const b = el('button', { class: 'btn btn-secondary btn-sm' }, 'Start Work (In Progress)');
                    b.addEventListener('click', () => updateStatus(jc.id, 'IN_PROGRESS'));
                    statusRow.appendChild(b);
                }
                if (jc.status === 'IN_PROGRESS') {
                    const b = el('button', { class: 'btn btn-primary btn-sm' }, 'Mark Completed');
                    b.addEventListener('click', () => updateStatus(jc.id, 'COMPLETED'));
                    statusRow.appendChild(b);
                }
                card.appendChild(statusRow);
            } else {
                const row = el('div', { style: 'display:flex;gap:8px;margin-top:10px;flex-wrap:wrap' });
                const aiBtn = el('button', { class: 'btn btn-secondary btn-sm' }, '✨ AI Summary');
                const aiBox = el('div');
                aiBtn.addEventListener('click', function () {
                    aiBox.innerHTML = '<div class="loading">Generating…</div>';
                    $.ajax({
                        url: API_BASE + '/job-cards/' + jc.id + '/ai-summary',
                        type: 'GET',
                        contentType: 'application/json',
                        headers: { 'Authorization': 'Bearer ' + state.token },
                        success: function (response) {
                            aiBox.innerHTML = '';
                            aiBox.appendChild(el('div', { class: 'ai-summary-box' }, response.body.summary));
                        },
                        error: function (xhr) {
                            aiBox.innerHTML = '';
                            toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not generate AI summary', true);
                        }
                    });
                });
                const invBtn = el('button', { class: 'btn btn-primary btn-sm' }, 'Generate Invoice');
                invBtn.addEventListener('click', function () {
                    $.ajax({
                        url: API_BASE + '/invoices',
                        type: 'POST',
                        contentType: 'application/json',
                        headers: { 'Authorization': 'Bearer ' + state.token },
                        data: JSON.stringify({ jobCardId: jc.id, tax: 0 }),
                        success: function (response) {
                            toast('Invoice ' + response.body.invoiceNumber + ' generated.');
                        },
                        error: function (xhr) {
                            toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not generate invoice', true);
                        }
                    });
                });
                row.appendChild(aiBtn);
                row.appendChild(invBtn);
                card.appendChild(row);
                card.appendChild(aiBox);
            }

            function updateStatus(id, status) {
                $.ajax({
                    url: API_BASE + '/job-cards/' + id + '/status',
                    type: 'PATCH',
                    contentType: 'application/json',
                    headers: { 'Authorization': 'Bearer ' + state.token },
                    data: JSON.stringify({ status: status }),
                    success: function () { toast('Status updated.'); renderList(); },
                    error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not update status', true); }
                });
            }

            return card;
        }

        function statusStampNode(status) {
            const span = document.createElement('span');
            span.innerHTML = statusStamp(status);
            return span.firstChild || span;
        }

        renderList();
    });
};
