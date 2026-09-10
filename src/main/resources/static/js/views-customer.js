
VIEW_RENDERERS.myvehicles = function (content) {
    content.innerHTML = '';

    // ---- Hero welcome banner ----
    const hero = el('div', { class: 'hero' }, [
        el('span', { class: 'hero-eyebrow' }, 'SMART GARAGE'),
        el('h2', { class: 'hero-title' }, ['Welcome back, ', el('span', { class: 'accent' }, state.username || 'there'), '.']),
        el('p', { class: 'hero-sub' }, 'Track every vehicle you own, book your next service, and keep an eye on invoices — all in one place.'),
        el('div', { class: 'hero-quick' }, [
            quickPill('Book a Service', null, () => setView('bookservice', 'Book a Service')),
            quickPill('My Bookings', null, () => setView('mybookings', 'My Bookings')),
            quickPill('My Invoices', null, () => setView('myinvoices', 'My Invoices'))
        ])
    ]);
    content.appendChild(hero);

    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, el('h2', {}, 'My Vehicles')));

    loadOptions('vehiclemodels', '/vehicle-models', r => (r.make ? r.make.name + ' ' : '') + r.modelName)
        .then(function (modelOpts) {

            const form = renderForm([
                { name: 'modelId', label: 'Model', type: 'select', required: true, options: modelOpts },
                { name: 'plateNumber', label: 'Plate Number', required: true },
                { name: 'chassisNo', label: 'Chassis No' },
                { name: 'color', label: 'Color' },
                { name: 'mileage', label: 'Mileage (km)', type: 'number' }
            ], function (values) {
                return new Promise(function (resolve, reject) {
                    if (!state.customerId) { reject(new Error('Your customer profile is still loading — try again in a moment.')); return; }

                    $.ajax({
                        url: API_BASE + '/vehicles',
                        type: 'POST',
                        contentType: 'application/json',
                        headers: { 'Authorization': 'Bearer ' + state.token },
                        data: JSON.stringify(Object.assign({ customerId: Number(state.customerId) }, values)),
                        success: function (response) {
                            toast('Vehicle added.');
                            renderList();
                            resolve(response.body);
                        },
                        error: function (xhr) {
                            reject(new Error((xhr.responseJSON && xhr.responseJSON.message) || 'Could not add vehicle'));
                        }
                    });
                });
            }, '+ Add Vehicle');
            panel.appendChild(form);

            const listWrap = el('div');
            panel.appendChild(listWrap);
            content.appendChild(panel);

            function renderList() {
                listWrap.innerHTML = '<div class="loading">Loading…</div>';
                if (!state.customerId) { listWrap.innerHTML = ''; return; }

                $.ajax({
                    url: API_BASE + '/vehicles?customerId=' + state.customerId,
                    type: 'GET',
                    contentType: 'application/json',
                    headers: { 'Authorization': 'Bearer ' + state.token },
                    success: function (response) {
                        listWrap.innerHTML = '';
                        listWrap.appendChild(renderTable([
                            { key: 'plateNumber', label: 'Plate' },
                            { key: 'model', label: 'Model', render: r => r.model ? esc((r.model.make ? r.model.make.name + ' ' : '') + r.model.modelName) : '—' },
                            { key: 'color', label: 'Color' },
                            { key: 'mileage', label: 'Mileage' },
                            { key: '_qr', label: 'QR', render: r => qrButton(r.id) }
                        ], response.body, { emptyText: "You haven't added a vehicle yet." }));
                    },
                    error: function (xhr) {
                        listWrap.innerHTML = '';
                        toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not load vehicles', true);
                    }
                });
            }
            renderList();
        });
};

VIEW_RENDERERS.bookservice = function (content) {
    content.innerHTML = '';
    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, el('h2', {}, 'Book a Service')));

    if (!state.customerId) {
        content.appendChild(panel);
        panel.appendChild(el('p', { class: 'helper' }, 'Loading your profile…'));
        return;
    }

    Promise.all([
        new Promise(function (resolve, reject) {
            $.ajax({
                url: API_BASE + '/vehicles?customerId=' + state.customerId,
                type: 'GET',
                contentType: 'application/json',
                headers: { 'Authorization': 'Bearer ' + state.token },
                success: function (response) { resolve(response.body); },
                error: function (xhr) { reject(new Error((xhr.responseJSON && xhr.responseJSON.message) || 'Could not load vehicles')); }
            });
        }),
        new Promise(function (resolve, reject) {
            $.ajax({
                url: API_BASE + '/service-types',
                type: 'GET',
                contentType: 'application/json',
                headers: { 'Authorization': 'Bearer ' + state.token },
                success: function (response) { resolve(response.body); },
                error: function (xhr) { reject(new Error((xhr.responseJSON && xhr.responseJSON.message) || 'Could not load service types')); }
            });
        })
    ]).then(function (results) {
        const vehicles = results[0], serviceTypes = results[1];

        if (vehicles.length === 0) {
            panel.appendChild(el('p', { class: 'helper' }, 'Add a vehicle under "My Vehicles" before booking a service.'));
            content.appendChild(panel);
            return;
        }

        const vehicleSelect = el('select', {}, vehicles.map(v => el('option', { value: v.id }, v.plateNumber)));
        const dateInput = el('input', { type: 'datetime-local', required: 'required' });
        const notesInput = el('input', { type: 'text', placeholder: 'Anything the mechanic should know?' });

        const offerGrid = el('div', { class: 'offer-grid' });
        const checkedIds = new Set();
        serviceTypes.forEach(st => {
            const card = el('div', { class: 'offer-card' }, [
                el('div', { class: 'offer-check' }),
                el('div', { class: 'offer-name' }, st.name),
                el('div', { class: 'offer-price' }, fmtMoney(st.basePrice))
            ]);
            card.addEventListener('click', () => {
                if (checkedIds.has(st.id)) { checkedIds.delete(st.id); card.classList.remove('checked'); }
                else { checkedIds.add(st.id); card.classList.add('checked'); }
            });
            offerGrid.appendChild(card);
        });

        panel.appendChild(el('div', { class: 'form-grid' }, [
            el('div', { class: 'field' }, [el('label', {}, 'Vehicle'), vehicleSelect]),
            el('div', { class: 'field' }, [el('label', {}, 'Preferred Date & Time'), dateInput]),
            el('div', { class: 'field' }, [el('label', {}, 'Notes'), notesInput])
        ]));
        panel.appendChild(el('div', { class: 'section-label', style: 'margin-top:8px' }, 'SERVICES NEEDED — TAP TO SELECT'));
        panel.appendChild(offerGrid);

        const submitBtn = el('button', { class: 'btn btn-primary', style: 'margin-top:18px' }, 'Submit Booking');
        submitBtn.addEventListener('click', function () {
            if (checkedIds.size === 0) { toast('Select at least one service.', true); return; }
            if (!dateInput.value) { toast('Choose a date and time.', true); return; }

            $.ajax({
                url: API_BASE + '/bookings',
                type: 'POST',
                contentType: 'application/json',
                headers: { 'Authorization': 'Bearer ' + state.token },
                data: JSON.stringify({
                    customerId: Number(state.customerId),
                    vehicleId: Number(vehicleSelect.value),
                    bookingDate: dateInput.value,
                    serviceTypeIds: Array.from(checkedIds),
                    notes: notesInput.value
                }),
                success: function () {
                    toast('Booking submitted — we\'ll confirm it shortly.');
                    checkedIds.clear();
                    offerGrid.querySelectorAll('.offer-card').forEach(c => c.classList.remove('checked'));
                },
                error: function (xhr) {
                    toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not submit booking', true);
                }
            });
        });
        panel.appendChild(submitBtn);
        content.appendChild(panel);
    });
};

VIEW_RENDERERS.mybookings = function (content) {
    content.innerHTML = '';
    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, [
        el('h2', {}, 'My Bookings'),
        el('span', { class: 'helper' }, 'Track each booking from submitted through to completed.')
    ]));
    const listWrap = el('div');
    panel.appendChild(listWrap);
    content.appendChild(panel);

    if (!state.customerId) {
        listWrap.appendChild(el('div', { class: 'loading' }, "You haven't booked a service yet."));
        return;
    }

    $.ajax({
        url: API_BASE + '/bookings?customerId=' + state.customerId,
        type: 'GET',
        contentType: 'application/json',
        headers: { 'Authorization': 'Bearer ' + state.token },
        success: function (response) {
            const rows = response.body || [];
            listWrap.innerHTML = '';
            if (rows.length === 0) {
                listWrap.appendChild(el('div', { class: 'loading' }, "You haven't booked a service yet."));
                return;
            }
            rows.forEach(r => listWrap.appendChild(bookingCard(r)));
        },
        error: function (xhr) {
            toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not load bookings', true);
        }
    });
};

function bookingCard(r) {
    const services = r.bookingServices ? r.bookingServices.map(bs => bs.serviceType ? bs.serviceType.name : '').filter(Boolean).join(', ') : '—';
    const card = el('div', { class: 'panel', style: 'margin-bottom:14px' }, [
        el('div', { class: 'panel-head' }, [
            el('div', {}, [
                el('h2', { style: 'font-size:16px' }, (r.vehicle ? esc(r.vehicle.plateNumber) : 'Booking #' + r.id)),
                el('span', { class: 'helper' }, esc((r.bookingDate || '').replace('T', ' ')) + ' · ' + esc(services))
            ]),
            statusStamp ? el('span', { html: statusStamp(r.status) }) : null
        ])
    ]);
    card.appendChild(bookingTracker(r.status));
    return card;
}

function bookingTracker(status) {
    if (status === 'CANCELLED') {
        return el('div', { class: 'tracker-cancelled' }, 'This booking was cancelled.');
    }
    const steps = ['PENDING', 'CONFIRMED', 'COMPLETED'];
    const labels = ['Booked', 'Confirmed', 'Completed'];
    const currentIdx = steps.indexOf(status);
    const tracker = el('div', { class: 'tracker' });
    steps.forEach((s, i) => {
        const done = currentIdx > i || (currentIdx === i && i === steps.length - 1);
        const active = currentIdx === i && i !== steps.length - 1;
        const cls = 'tracker-step' + (done ? ' done' : '') + (active ? ' active' : '');
        tracker.appendChild(el('div', { class: cls }, [
            el('div', { class: 'tracker-line' }),
            el('div', { class: 'tracker-dot' }),
            el('div', { class: 'tracker-label' }, labels[i])
        ]));
    });
    return tracker;
}

VIEW_RENDERERS.myinvoices = function (content) {
    content.innerHTML = '';
    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, el('h2', {}, 'My Invoices')));
    content.appendChild(panel);

    $.ajax({
        url: API_BASE + '/invoices',
        type: 'GET',
        contentType: 'application/json',
        headers: { 'Authorization': 'Bearer ' + state.token },
        success: function (response) {
            const all = response.body;
            const mine = all.filter(inv =>
                inv.jobCard && inv.jobCard.booking && inv.jobCard.booking.customer &&
                String(inv.jobCard.booking.customer.id) === String(state.customerId)
            );
            panel.appendChild(renderTable([
                { key: 'invoiceNumber', label: 'Invoice #' },
                { key: 'issueDate', label: 'Issued' },
                { key: 'totalAmount', label: 'Total', render: r => fmtMoney(r.totalAmount) },
                { key: 'status', label: 'Status', render: r => statusStamp(r.status) }
            ], mine, { emptyText: 'No invoices yet.' }));
        },
        error: function () {
            panel.appendChild(renderTable([], [], { emptyText: 'No invoices yet.' }));
        }
    });
};
