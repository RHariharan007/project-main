/* admin-orders.js — view all orders and update their status on admin-orders.html */

const STATUSES = ["PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"];

function statusSelectHtml(orderId, currentStatus) {
  return `
    <select id="status-select" onchange="updateStatus(${orderId}, this.value)">
      ${STATUSES.map(s => `<option value="${s}" ${s === currentStatus ? "selected" : ""}>${s}</option>`).join("")}
    </select>
  `;
}

async function loadOrders() {
  hideAlert("alert-box");
  try {
    const orders = await apiRequest("/admin/orders");
    const tbody = document.getElementById("orders-tbody");

    if (!orders.length) {
      tbody.innerHTML = `<tr><td colspan="6" class="muted">No orders placed yet.</td></tr>`;
      return;
    }

    tbody.innerHTML = orders.map(order => `
      <tr>
        <td>#${order.id}</td>
        <td>${order.user ? order.user.fullName : "—"}</td>
        <td>${formatDate(order.orderDate)}</td>
        <td>${order.items.reduce((n, i) => n + i.quantity, 0)} items</td>
        <td>${formatCurrency(order.totalAmount)}</td>
        <td>${statusSelectHtml(order.id, order.status)}</td>
      </tr>
    `).join("");
  } catch (err) {
    showAlert("alert-box", "Could not load orders: " + err.message);
  }
}

async function updateStatus(orderId, status) {
  try {
    await apiRequest(`/admin/orders/${orderId}/status`, { method: "PUT", body: { status } });
  } catch (err) {
    showAlert("alert-box", err.message);
    loadOrders(); // revert the dropdown to the real status
  }
}

Session.requireAdmin();
renderHeader("admin-orders");
loadOrders();
