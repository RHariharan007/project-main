/* orders.js — customer's own order history on orders.html */

function renderOrders(orders) {
  const el = document.getElementById("orders-list");

  if (!orders.length) {
    el.innerHTML = `
      <div class="empty-orders">
        <p class="muted">You haven't placed any orders yet.</p>
        <a href="products.html" class="btn btn-dark mt-24">Start shopping</a>
      </div>`;
    return;
  }

  el.innerHTML = orders.map(order => `
    <div class="card order-card">
      <div class="order-head">
        <div>
          <div class="order-id">Order #${order.id}</div>
          <div class="order-date">${formatDate(order.orderDate)}</div>
        </div>
        <span class="status-pill status-${order.status}">${order.status}</span>
      </div>
      ${order.items.map(item => `
        <div class="order-line">
          <span>${item.product.name} × ${item.quantity}</span>
          <span>${formatCurrency(item.priceAtPurchase * item.quantity)}</span>
        </div>
      `).join("")}
      <div class="order-footer">
        <span class="muted" style="font-weight:400;">Ship to: ${order.shippingAddress}</span>
        <span>${formatCurrency(order.totalAmount)}</span>
      </div>
    </div>
  `).join("");
}

async function loadOrders() {
  try {
    const orders = await apiRequest("/orders/my");
    renderOrders(orders);
  } catch (err) {
    showAlert("alert-box", "Could not load orders: " + err.message);
  }
}

// Show a one-time success banner if we just came from checkout
const placedId = new URLSearchParams(window.location.search).get("placed");
if (placedId) {
  showAlert("success-box", `Order #${placedId} placed successfully!`, "success");
}

Session.requireAuth();
renderHeader("orders");
loadOrders();
