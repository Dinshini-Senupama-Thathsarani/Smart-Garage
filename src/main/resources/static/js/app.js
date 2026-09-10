
const API_BASE = window.location.origin + '/api/v1';

const state = {
    token: sessionStorage.getItem('sg_token') || null,
    role: sessionStorage.getItem('sg_role') || null,
    username: sessionStorage.getItem('sg_username') || null,
    userId: sessionStorage.getItem('sg_userId') || null,
    customerId: sessionStorage.getItem('sg_customerId') || null,
    currentView: null,
    cache: {} // lookup lists (makes, models, categories...) reused across forms
};

// ---------- Toast ----------
function toast(message, isError) {
    const el = document.createElement('div');
    el.className = 'toast' + (isError ? ' error' : '');
    el.textContent = message;
    document.getElementById('toastHost').appendChild(el);
    setTimeout(() => el.remove(), 4200);
}

// ---------- Auth persistence ----------
function saveSession(authResponse) {
    state.token = authResponse.token;
    state.role = authResponse.role;
    state.username = authResponse.username;
    state.userId = authResponse.userId;
    sessionStorage.setItem('sg_token', state.token);
    sessionStorage.setItem('sg_role', state.role);
    sessionStorage.setItem('sg_username', state.username);
    sessionStorage.setItem('sg_userId', state.userId);
}

function clearSession() {
    state.token = state.role = state.username = state.userId = state.customerId = null;
    sessionStorage.clear();
}

// ---------- Auth screen wiring ----------
const authShell = document.getElementById('authShell');
const appShell = document.getElementById('appShell');
const authError = document.getElementById('authError');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const toggleAuthBtn = document.getElementById('toggleAuthBtn');
const toggleText = document.getElementById('toggleText');
const authSubtitle = document.getElementById('authSubtitle');

let showingRegister = false;
toggleAuthBtn.addEventListener('click', () => {
    showingRegister = !showingRegister;
    loginForm.style.display = showingRegister ? 'none' : 'block';
    registerForm.style.display = showingRegister ? 'block' : 'none';
    toggleText.textContent = showingRegister ? 'Already have an account?' : "Don't have an account?";
    toggleAuthBtn.textContent = showingRegister ? 'Sign in' : 'Register';
    authSubtitle.textContent = showingRegister ? 'Set up a new account.' : 'Sign in to open a work order.';
    authError.style.display = 'none';
});

function showAuthError(msg) {
    authError.textContent = msg;
    authError.style.display = 'block';
}

loginForm.addEventListener('submit', function (e) {
    e.preventDefault();
    authError.style.display = 'none';

    let obj = JSON.stringify({
        username: document.getElementById('loginUsername').value,
        password: document.getElementById('loginPassword').value
    });

    $.ajax({
        url: API_BASE + '/auth/login',
        type: 'POST',
        contentType: 'application/json',
        data: obj,
        success: function (response) {
            saveSession(response.body);
            enterApp();
        },
        error: function (xhr) {
            const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Invalid username or password';
            showAuthError(msg);
        }
    });
});

registerForm.addEventListener('submit', function (e) {
    e.preventDefault();
    authError.style.display = 'none';

    let obj = JSON.stringify({
        fullName: document.getElementById('regFullName').value,
        username: document.getElementById('regUsername').value,
        email: document.getElementById('regEmail').value,
        phone: document.getElementById('regPhone').value,
        password: document.getElementById('regPassword').value,
        role: document.getElementById('regRole').value
    });

    $.ajax({
        url: API_BASE + '/auth/register',
        type: 'POST',
        contentType: 'application/json',
        data: obj,
        success: function (response) {
            saveSession(response.body);
            toast('Account created — welcome!');
            enterApp();
        },
        error: function (xhr) {
            const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Registration failed';
            showAuthError(msg);
        }
    });
});

document.getElementById('logoutBtn').addEventListener('click', () => {
    clearSession();
    location.reload();
});

// ---------- Navigation config per role ----------
const NAV = {
    ADMIN: [
        { section: 'Explore', items: [
                { key: 'home', label: 'Home' },
                { key: 'sparepartscatalog', label: 'Spare Parts Catalog' },
                { key: 'contact', label: 'Contact Us' }
            ]},
        { section: 'Overview', items: [
                { key: 'dashboard', label: 'Dashboard' }
            ]},
        { section: 'Front Desk', items: [
                { key: 'customers', label: 'Customers' },
                { key: 'vehicles', label: 'Vehicles' },
                { key: 'bookings', label: 'Bookings' }
            ]},
        { section: 'Workshop', items: [
                { key: 'jobcards', label: 'Job Cards' },
                { key: 'mechanics', label: 'Mechanics' }
            ]},
        { section: 'Inventory', items: [
                { key: 'spareparts', label: 'Spare Parts' },
                { key: 'sparepartcategories', label: 'Part Categories' },
                { key: 'suppliers', label: 'Suppliers' },
                { key: 'purchaseorders', label: 'Purchase Orders' }
            ]},
        { section: 'Catalog', items: [
                { key: 'servicecategories', label: 'Service Categories' },
                { key: 'servicetypes', label: 'Service Types' },
                { key: 'vehiclemakes', label: 'Vehicle Makes' },
                { key: 'vehiclemodels', label: 'Vehicle Models' }
            ]},
        { section: 'Billing', items: [
                { key: 'invoices', label: 'Invoices' }
            ]}
    ],
    CUSTOMER: [
        { section: 'Explore', items: [
                { key: 'home', label: 'Home' },
                { key: 'sparepartscatalog', label: 'Spare Parts Catalog' },
                { key: 'contact', label: 'Contact Us' }
            ]},
        { section: 'My Garage', items: [
                { key: 'myvehicles', label: 'My Vehicles' },
                { key: 'bookservice', label: 'Book a Service' },
                { key: 'mybookings', label: 'My Bookings' },
                { key: 'myinvoices', label: 'My Invoices' }
            ]}
    ],
    MECHANIC: [
        { section: 'Explore', items: [
                { key: 'home', label: 'Home' },
                { key: 'contact', label: 'Contact Us' }
            ]},
        { section: 'Workshop', items: [
                { key: 'jobcards', label: 'Job Cards' },
                { key: 'spareparts', label: 'Spare Parts' }
            ]}
    ],
    GUEST: [
        { section: 'Catalog', items: [
                { key: 'servicecategories', label: 'Service Categories' },
                { key: 'servicetypes', label: 'Service Types' }
            ]}
    ]
};

function renderNav() {
    const navList = document.getElementById('navList');
    navList.innerHTML = '';
    const groups = NAV[state.role] || NAV.GUEST;
    groups.forEach(group => {
        const label = document.createElement('div');
        label.className = 'nav-section-label';
        label.textContent = group.section;
        navList.appendChild(label);
        group.items.forEach(item => {
            const el = document.createElement('div');
            el.className = 'nav-item';
            el.dataset.key = item.key;
            el.textContent = item.label;
            el.addEventListener('click', () => setView(item.key, item.label));
            navList.appendChild(el);
        });
    });
}

function setActiveNav(key) {
    document.querySelectorAll('.nav-item').forEach(el => {
        el.classList.toggle('active', el.dataset.key === key);
    });
}

function setView(key, label) {
    state.currentView = key;
    setActiveNav(key);
    document.getElementById('pageTitle').textContent = label;
    const content = document.getElementById('content');
    content.innerHTML = '<div class="loading">Loading…</div>';
    VIEW_RENDERERS[key](content).catch(err => {
        content.innerHTML = '';
        toast(err.message, true);
    });
}

// ---------- Boot ----------
function enterApp() {
    authShell.style.display = 'none';
    appShell.style.display = 'flex';
    document.getElementById('sidebarUsername').textContent = state.username;
    const roleChip = document.getElementById('sidebarRole');
    roleChip.textContent = state.role;

    if (state.role === 'CUSTOMER') {
        $.ajax({
            url: API_BASE + '/customers/me',
            type: 'GET',
            contentType: 'application/json',
            headers: { 'Authorization': 'Bearer ' + state.token },
            success: function (response) {
                state.customerId = response.body.id;
                sessionStorage.setItem('sg_customerId', response.body.id);
                finishEnterApp();
            },
            error: function (xhr) {
                const msg = (xhr.responseJSON && xhr.responseJSON.message) || 'Could not load your customer profile';
                toast(msg, true);
                finishEnterApp();
            }
        });
    } else {
        finishEnterApp();
    }
}

function finishEnterApp() {
    renderNav();
    const defaultView = 'home';
    const defaultLabel = document.querySelector('.nav-item[data-key="' + defaultView + '"]');
    setView(defaultView, defaultLabel ? defaultLabel.textContent : 'Dashboard');
}

window.addEventListener('DOMContentLoaded', () => {
    if (state.token && state.role) {
        enterApp();
    }
    // Footer quick links (static markup in index.html) route through the same setView().
    document.querySelectorAll('.footer-link').forEach(function (node) {
        node.addEventListener('click', function () {
            setView(node.dataset.goto, node.textContent);
        });
    });
});
