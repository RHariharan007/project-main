/* admin-dashboard.js — pulls headline stats for admin-dashboard.html */

async function loadStats() {
  try {
    const stats = await apiRequest("/admin/dashboard");
    document.getElementById("stat-grid").innerHTML = `
      <div class="card stat-card">
        <div class="stat-label">Total orders</div>
        <div class="stat-value">${stats.totalOrders}</div>
      </div>
      <div class="card stat-card">
        <div class="stat-label">Total revenue</div>
        <div class="stat-value">${formatCurrency(stats.totalRevenue)}</div>
      </div>
      <div class="card stat-card">
        <div class="stat-label">Pending orders</div>
        <div class="stat-value">${stats.pendingOrders}</div>
      </div>
    `;
  } catch (err) {
    showAlert("alert-box", "Could not load dashboard stats: " + err.message);
  }
}

Session.requireAdmin();
renderHeader("admin-dashboard");
loadStats();
