/* ============================================================
   SHOPCRAFT — shared frontend helpers
   Include this file (after no other script) on every page.
   Talks to the Spring Boot API using JWT bearer auth.
   ============================================================ */

const API_BASE_URL = "http://localhost:8080/api";

/* ---------------- Session helpers ---------------- */

const Session = {
  setAuth(authResponse) {
    localStorage.setItem("token", authResponse.token);
    localStorage.setItem("userId", authResponse.userId);
    localStorage.setItem("fullName", authResponse.fullName);
    localStorage.setItem("email", authResponse.email);
    localStorage.setItem("role", authResponse.role);
  },
  getToken() { return localStorage.getItem("token"); },
  getRole() { return localStorage.getItem("role"); },
  getFullName() { return localStorage.getItem("fullName"); },
  isLoggedIn() { return !!this.getToken(); },
  isAdmin() { return this.getRole() === "ADMIN"; },
  logout() {
    localStorage.clear();
    window.location.href = "login.html";
  },
  /** Call at the top of a page that requires any logged-in user. */
  requireAuth() {
    if (!this.isLoggedIn()) {
      window.location.href = "login.html";
    }
  },
  /** Call at the top of an admin-only page. */
  requireAdmin() {
    if (!this.isLoggedIn() || !this.isAdmin()) {
      window.location.href = "login.html";
    }
  }
};

/* ---------------- API request wrapper ---------------- */

/**
 * Wraps fetch(): adds the API base URL, JSON headers, and the JWT
 * bearer token automatically. Throws an Error with the server's
 * message on non-2xx responses so callers can catch() and display it.
 */
async function apiRequest(path, { method = "GET", body = null, auth = true } = {}) {
  const headers = { "Content-Type": "application/json" };
  if (auth && Session.getToken()) {
    headers["Authorization"] = "Bearer " + Session.getToken();
  }

  const response = await fetch(API_BASE_URL + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });

  // 204 No Content
  if (response.status === 204) return null;

  let data;
  try { data = await response.json(); } catch (e) { data = null; }

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      // token missing/expired/insufficient role
      const message = (data && (data.message || Object.values(data)[0])) || "Session expired. Please log in again.";
      throw new Error(message);
    }
    const message = (data && (data.message || Object.values(data)[0])) || "Something went wrong";
    throw new Error(message);
  }
  return data;
}

/* ---------------- Shared UI helpers ---------------- */

function showAlert(elementId, message, type = "error") {
  const el = document.getElementById(elementId);
  if (!el) return;
  el.textContent = message;
  el.className = "alert show alert-" + type;
}

function hideAlert(elementId) {
  const el = document.getElementById(elementId);
  if (!el) return;
  el.className = "alert";
}

function formatCurrency(amount) {
  return "₹" + Number(amount).toFixed(2);
}

function formatDate(isoString) {
  const d = new Date(isoString);
  return d.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
}

/** Renders the shared header/nav into any element with id="site-header". */
function renderHeader(activePage = "") {
  const el = document.getElementById("site-header");
  if (!el) return;

  const loggedIn = Session.isLoggedIn();
  const isAdmin = Session.isAdmin();

  el.innerHTML = `
    <div class="container nav-row">
      <a href="products.html" class="brand">Shopcraft</a>
      <nav class="nav-links">
        <a href="products.html" ${activePage === "products" ? 'style="color:var(--ink);font-weight:600"' : ""}>Shop</a>
        ${loggedIn && !isAdmin ? `<a href="orders.html" ${activePage === "orders" ? 'style="color:var(--ink);font-weight:600"' : ""}>My Orders</a>` : ""}
        ${isAdmin ? `<a href="admin-dashboard.html" ${activePage === "admin-dashboard" ? 'style="color:var(--ink);font-weight:600"' : ""}>Dashboard</a>
                     <a href="admin-products.html" ${activePage === "admin-products" ? 'style="color:var(--ink);font-weight:600"' : ""}>Products</a>
                     <a href="admin-orders.html" ${activePage === "admin-orders" ? 'style="color:var(--ink);font-weight:600"' : ""}>Orders</a>` : ""}
        ${!isAdmin ? `<a href="cart.html" class="nav-cart">Cart <span class="cart-badge" id="cart-badge">0</span></a>` : ""}
        ${loggedIn
          ? `<a href="#" onclick="Session.logout(); return false;">Log out</a>`
          : `<a href="login.html">Log in</a>`}
      </nav>
    </div>
  `;

  if (loggedIn && !isAdmin) updateCartBadge();
}

/** Fetches the cart just to get an item count for the nav badge. Fails silently if not logged in. */
async function updateCartBadge() {
  try {
    const data = await apiRequest("/cart");
    const count = (data.items || []).reduce((sum, i) => sum + i.quantity, 0);
    const badge = document.getElementById("cart-badge");
    if (badge) badge.textContent = count;
  } catch (e) { /* not logged in yet, ignore */ }
}
