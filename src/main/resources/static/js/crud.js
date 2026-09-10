
function genericCrudView(cfg) {
    return function (content) {
        content.innerHTML = '';

        const panel = el('div', { class: 'panel' });
        panel.appendChild(el('div', { class: 'panel-head' }, el('h2', {}, cfg.title)));

        if (cfg.subtitle) panel.appendChild(el('p', { class: 'helper', style: 'margin:-8px 0 16px' }, cfg.subtitle));

        return resolveSelectOptions(cfg.fields).then(function (fields) {

            if (cfg.canCreate !== false) {
                const form = renderForm(fields, function (values) {
                    return new Promise(function (resolve, reject) {
                        const payload = cfg.buildPayload ? cfg.buildPayload(values) : values;
                        const query = cfg.createQuery ? cfg.createQuery(values) : '';

                        $.ajax({
                            url: API_BASE + cfg.endpoint + query,
                            type: 'POST',
                            contentType: 'application/json',
                            headers: { 'Authorization': 'Bearer ' + state.token },
                            data: JSON.stringify(payload),
                            success: function (response) {
                                toast(cfg.title + ' added.');
                                if (cfg.invalidates) cfg.invalidates.forEach(invalidateCache);
                                renderList();
                                resolve(response.body);
                            },
                            error: function (xhr) {
                                const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Could not add ' + cfg.title;
                                reject(new Error(msg));
                            }
                        });
                    });
                }, '+ Add');
                panel.appendChild(form);
            }

            const listWrap = el('div');
            panel.appendChild(listWrap);
            content.appendChild(panel);

            function renderList() {
                listWrap.innerHTML = '<div class="loading">Loading…</div>';

                $.ajax({
                    url: API_BASE + cfg.endpoint,
                    type: 'GET',
                    contentType: 'application/json',
                    headers: { 'Authorization': 'Bearer ' + state.token },
                    success: function (response) {
                        const rows = response.body;
                        const columns = cfg.columns.slice();
                        if (cfg.canDelete !== false) {
                            columns.push({
                                key: '_actions', label: '', render: function (row) {
                                    const btn = el('button', { class: 'btn btn-danger' }, 'Delete');
                                    btn.addEventListener('click', function () {
                                        if (!confirm('Delete this record?')) return;

                                        $.ajax({
                                            url: API_BASE + cfg.endpoint + '/' + row.id,
                                            type: 'DELETE',
                                            contentType: 'application/json',
                                            headers: { 'Authorization': 'Bearer ' + state.token },
                                            success: function () {
                                                toast('Deleted.');
                                                if (cfg.invalidates) cfg.invalidates.forEach(invalidateCache);
                                                renderList();
                                            },
                                            error: function (xhr) {
                                                const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Delete failed';
                                                toast(msg, true);
                                            }
                                        });
                                    });
                                    return btn;
                                }
                            });
                        }
                        listWrap.innerHTML = '';
                        listWrap.appendChild(renderTable(columns, rows));
                    },
                    error: function (xhr) {
                        listWrap.innerHTML = '';
                        const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Could not load ' + cfg.title;
                        toast(msg, true);
                    }
                });
            }

            renderList();
        });
    };
}


function resolveSelectOptions(rawFields) {
    const promises = rawFields.map(function (f) {
        if (f.type === 'select' && f.optionsSource) {
            return loadOptions(f.optionsSource.key, f.optionsSource.endpoint, f.optionsSource.labelFn)
                .then(function (opts) { return Object.assign({}, f, { options: opts }); });
        }
        return Promise.resolve(f);
    });
    return Promise.all(promises);
}
