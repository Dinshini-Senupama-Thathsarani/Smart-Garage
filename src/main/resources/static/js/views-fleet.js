
VIEW_RENDERERS.vehicles = function (content) {
    content.innerHTML = '';
    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, el('h2', {}, 'Vehicles')));

    Promise.all([
        loadOptions('customers', '/customers', r => r.fullName + ' (#' + r.id + ')'),
        loadOptions('vehiclemodels', '/vehicle-models', r => (r.make ? r.make.name + ' ' : '') + r.modelName)
    ]).then(function (results) {
        const customerOpts = results[0], modelOpts = results[1];

        const form = renderForm([
            { name: 'customerId', label: 'Customer', type: 'select', required: true, options: customerOpts },
            { name: 'modelId', label: 'Model', type: 'select', required: true, options: modelOpts },
            { name: 'plateNumber', label: 'Plate Number', required: true },
            { name: 'chassisNo', label: 'Chassis No' },
            { name: 'color', label: 'Color' },
            { name: 'mileage', label: 'Mileage (km)', type: 'number' }
        ], function (values) {
            return new Promise(function (resolve, reject) {
                $.ajax({
                    url: API_BASE + '/vehicles',
                    type: 'POST',
                    contentType: 'application/json',
                    headers: { 'Authorization': 'Bearer ' + state.token },
                    data: JSON.stringify(values),
                    success: function (response) {
                        toast('Vehicle registered.');
                        renderList();
                        resolve(response.body);
                    },
                    error: function (xhr) {
                        const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Could not register vehicle';
                        reject(new Error(msg));
                    }
                });
            });
        }, '+ Register Vehicle');
        panel.appendChild(form);

        const listWrap = el('div');
        panel.appendChild(listWrap);
        content.appendChild(panel);

        function renderList() {
            listWrap.innerHTML = '<div class="loading">Loading…</div>';

            $.ajax({
                url: API_BASE + '/vehicles',
                type: 'GET',
                contentType: 'application/json',
                headers: { 'Authorization': 'Bearer ' + state.token },
                success: function (response) {
                    const rows = response.body;
                    listWrap.innerHTML = '';
                    listWrap.appendChild(renderTable([
                        { key: 'plateNumber', label: 'Plate' },
                        { key: 'model', label: 'Model', render: r => r.model ? esc((r.model.make ? r.model.make.name + ' ' : '') + r.model.modelName) : '—' },
                        { key: 'color', label: 'Color' },
                        { key: 'mileage', label: 'Mileage' },
                        { key: 'customer', label: 'Owner', render: r => r.customer ? esc(r.customer.fullName) : '—' },
                        { key: '_qr', label: 'QR', render: r => qrButton(r.id) },
                        { key: '_del', label: '', render: r => deleteButton('/vehicles/' + r.id, renderList) }
                    ], rows));
                },
                error: function (xhr) {
                    listWrap.innerHTML = '';
                    const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Could not load vehicles';
                    toast(msg, true);
                }
            });
        }

        renderList();
    });
};

function qrButton(vehicleId) {
    const btn = el('button', { class: 'btn btn-secondary btn-sm' }, 'View QR');
    btn.addEventListener('click', () => showQrModal(vehicleId));
    return btn;
}

function deleteButton(endpoint, refresh) {
    const btn = el('button', { class: 'btn btn-danger' }, 'Delete');
    btn.addEventListener('click', function () {
        if (!confirm('Delete this record?')) return;

        $.ajax({
            url: API_BASE + endpoint,
            type: 'DELETE',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function () {
                toast('Deleted.');
                refresh();
            },
            error: function (xhr) {
                const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Delete failed';
                toast(msg, true);
            }
        });
    });
    return btn;
}

function showQrModal(vehicleId) {
    const url = API_BASE + '/vehicles/' + vehicleId + '/qrcode';
    // Binary image data - kept as a plain fetch() (jQuery's $.ajax isn't
    // well-suited to blob responses), everything else uses $.ajax.
    fetch(url, { headers: { Authorization: 'Bearer ' + state.token } })
        .then(res => { if (!res.ok) throw new Error('Could not generate QR (' + res.status + ')'); return res.blob(); })
        .then(blob => {
            const imgUrl = URL.createObjectURL(blob);
            const win = window.open('', '_blank', 'width=380,height=460');
            win.document.write(
                '<title>Vehicle QR Code</title>' +
                '<body style="font-family:sans-serif;text-align:center;padding:24px;">' +
                '<h3>Vehicle #' + vehicleId + ' QR Code</h3>' +
                '<img src="' + imgUrl + '" style="max-width:100%;border:1px solid #ccc" />' +
                '<p style="color:#888;font-size:12px">Scan to open this vehicle\'s record.</p>' +
                '</body>'
            );
        })
        .catch(err => toast(err.message, true));
}

// ============================================================
// Bookings (admin) — list, filter by status, status update, spawn job card
// ============================================================

VIEW_RENDERERS.bookings = function (content) {
    content.innerHTML = '';
    const panel = el('div', { class: 'panel' });
    panel.appendChild(el('div', { class: 'panel-head' }, el('h2', {}, 'Bookings')));

    const tabs = el('div', { class: 'tabs' });
    const statuses = ['ALL', 'PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED'];
    let activeStatus = 'ALL';
    statuses.forEach(s => {
        const tabBtn = el('button', { class: 'tab-btn' + (s === activeStatus ? ' active' : ''), type: 'button' }, s);
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

    function renderList() {
        listWrap.innerHTML = '<div class="loading">Loading…</div>';
        const query = activeStatus === 'ALL' ? '' : ('?status=' + activeStatus);

        $.ajax({
            url: API_BASE + '/bookings' + query,
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) {
                const rows = response.body;
                listWrap.innerHTML = '';
                listWrap.appendChild(renderTable([
                    { key: 'id', label: 'ID' },
                    { key: 'customer', label: 'Customer', render: r => r.customer ? esc(r.customer.fullName) : '—' },
                    { key: 'vehicle', label: 'Vehicle', render: r => r.vehicle ? esc(r.vehicle.plateNumber) : '—' },
                    { key: 'bookingDate', label: 'Date', render: r => esc((r.bookingDate || '').replace('T', ' ')) },
                    { key: 'services', label: 'Services', render: r => r.bookingServices ? r.bookingServices.map(bs => esc(bs.serviceType ? bs.serviceType.name : '')).join(', ') : '—' },
                    { key: 'status', label: 'Status', render: r => statusStamp(r.status) },
                    { key: '_actions', label: '', render: bookingActions }
                ], rows));
            },
            error: function (xhr) {
                listWrap.innerHTML = '';
                const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Could not load bookings';
                toast(msg, true);
            }
        });
    }

    function bookingActions(row) {
        const wrap = el('div', { style: 'display:flex;gap:6px;flex-wrap:wrap' });

        if (row.status === 'PENDING') {
            const confirmBtn = el('button', { class: 'btn btn-secondary btn-sm' }, 'Confirm');
            confirmBtn.addEventListener('click', function () {
                $.ajax({
                    url: API_BASE + '/bookings/' + row.id + '/status',
                    type: 'PATCH',
                    contentType: 'application/json',
                    headers: { 'Authorization': 'Bearer ' + state.token },
                    data: JSON.stringify({ status: 'CONFIRMED' }),
                    success: function () { toast('Booking confirmed.'); renderList(); },
                    error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not confirm booking', true); }
                });
            });
            wrap.appendChild(confirmBtn);
        }

        if (row.status === 'CONFIRMED') {
            const jobBtn = el('button', { class: 'btn btn-primary btn-sm' }, 'Create Job Card');
            jobBtn.addEventListener('click', function () {
                $.ajax({
                    url: API_BASE + '/job-cards',
                    type: 'POST',
                    contentType: 'application/json',
                    headers: { 'Authorization': 'Bearer ' + state.token },
                    data: JSON.stringify({ bookingId: row.id }),
                    success: function () { toast('Job card created.'); },
                    error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not create job card', true); }
                });
            });
            wrap.appendChild(jobBtn);
        }

        if (row.status === 'PENDING' || row.status === 'CONFIRMED') {
            const cancelBtn = el('button', { class: 'btn btn-danger' }, 'Cancel');
            cancelBtn.addEventListener('click', function () {
                $.ajax({
                    url: API_BASE + '/bookings/' + row.id + '/status',
                    type: 'PATCH',
                    contentType: 'application/json',
                    headers: { 'Authorization': 'Bearer ' + state.token },
                    data: JSON.stringify({ status: 'CANCELLED' }),
                    success: function () { toast('Booking cancelled.'); renderList(); },
                    error: function (xhr) { toast((xhr.responseJSON && xhr.responseJSON.message) || 'Could not cancel booking', true); }
                });
            });
            wrap.appendChild(cancelBtn);
        }

        return wrap;
    }

    renderList();
};
