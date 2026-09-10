

function statusStamp(value) {
    if (!value) return '';
    const cls = 'stamp-' + String(value).toLowerCase();
    return '<span class="stamp ' + cls + '">' + value.replace(/_/g, ' ') + '</span>';
}

function esc(v) {
    if (v === null || v === undefined) return '';
    return String(v).replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));
}

function fmtMoney(v) {
    if (v === null || v === undefined) return '—';
    return 'Rs. ' + Number(v).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function el(tag, attrs = {}, children = []) {
    const node = document.createElement(tag);
    Object.entries(attrs).forEach(([k, v]) => {
        if (k === 'class') node.className = v;
        else if (k === 'html') node.innerHTML = v;
        else if (k.startsWith('on')) node.addEventListener(k.slice(2), v);
        else node.setAttribute(k, v);
    });
    (Array.isArray(children) ? children : [children]).forEach(c => {
        if (c === null || c === undefined) return;
        node.appendChild(typeof c === 'string' ? document.createTextNode(c) : c);
    });
    return node;
}

// Renders a data table. columns: [{key, label, render?}], rows: array of objects
function renderTable(columns, rows, opts = {}) {
    const table = el('table');
    const thead = el('thead', {}, el('tr', {}, columns.map(c => el('th', {}, c.label))));
    table.appendChild(thead);
    const tbody = el('tbody');
    if (!rows || rows.length === 0) {
        tbody.appendChild(el('tr', { class: 'empty-row' }, el('td', { colspan: columns.length }, opts.emptyText || 'No records yet.')));
    } else {
        rows.forEach(row => {
            const tr = el('tr');
            columns.forEach(c => {
                const td = el('td');
                const val = c.render ? c.render(row) : (row[c.key] ?? '—');
                if (val instanceof Node) td.appendChild(val);
                else td.innerHTML = val;
                tr.appendChild(td);
            });
            tbody.appendChild(tr);
        });
    }
    table.appendChild(tbody);
    return table;
}


function renderForm(fields, onSubmit, submitLabel = 'Save') {
    const form = el('form', { class: 'form-grid' });
    const inputs = {};
    fields.forEach(f => {
        const field = el('div', { class: 'field' });
        field.appendChild(el('label', {}, f.label));
        let input;
        if (f.type === 'select') {
            input = el('select', { required: f.required ? 'required' : null });
            if (!f.options || f.options.length === 0) {
                input.appendChild(el('option', { value: '', disabled: 'disabled', selected: 'selected' }, 'No options yet — add one first'));
                input.disabled = true;
            } else {
                (f.options || []).forEach(opt => input.appendChild(el('option', { value: opt.value }, opt.label)));
            }
        } else {
            input = el('input', {
                type: f.type || 'text',
                required: f.required ? 'required' : null,
                step: f.step || null,
                min: f.min || null,
                placeholder: f.placeholder || ''
            });
        }
        inputs[f.name] = input;
        field.appendChild(input);
        form.appendChild(field);
    });
    const actionsWrap = el('div', { class: 'field' });
    actionsWrap.appendChild(el('label', {}, '\u00A0'));
    const btn = el('button', { class: 'btn btn-primary', type: 'submit' }, submitLabel);
    actionsWrap.appendChild(btn);
    form.appendChild(actionsWrap);

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const values = {};
        fields.forEach(f => {
            let v = inputs[f.name].value;
            if (f.type === 'number') v = v === '' ? null : Number(v);
            values[f.name] = v;
        });
        btn.disabled = true;
        try {
            await onSubmit(values);
            form.reset();
        } catch (err) {
            toast(err.message, true);
        } finally {
            btn.disabled = false;
        }
    });

    return form;
}

function loadOptions(cacheKey, endpoint, labelFn) {
    if (state.cache[cacheKey]) return Promise.resolve(state.cache[cacheKey]);

    return new Promise(function (resolve, reject) {
        $.ajax({
            url: API_BASE + endpoint,
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) {
                const rows = response.body || [];
                const options = rows.map(function (r) { return { value: r.id, label: labelFn(r) }; });
                state.cache[cacheKey] = options;
                resolve(options);
            },
            error: function (xhr) {
                const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Could not load options for ' + endpoint;
                reject(new Error(msg));
            }
        });
    });
}

function invalidateCache(cacheKey) {
    delete state.cache[cacheKey];
}
