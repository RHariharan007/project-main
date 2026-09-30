/* checkout.js — reviews the cart and submits the final order on checkout.html */

async function loadReview() {
  try {
    const data = await apiRequest("/cart");
    if (!data.items.length) {
      window.location.href = "cart.html";
      return;
    }
    document.getElementById("review-items").innerHTML = data.items.map(item => `
      <div class="review-line">
        <span>${item.product.name} × ${item.quantity}</span>
        <span>${formatCurrency(item.product.price * item.quantity)}</span>
      </div>
    `).join("");
    document.getElementById("review-total").textContent = formatCurrency(data.total);
  } catch (err) {
    showAlert("alert-box", "Could not load your cart: " + err.message);
  }
}

document.getElementById("checkout-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert("alert-box");

  const shippingAddress = document.getElementById("shippingAddress").value.trim();
  const btn = document.getElementById("place-order-btn");
  btn.disabled = true;
  btn.textContent = "Placing order...";

  try {
    const order = await apiRequest("/orders/checkout", {
      method: "POST",
      body: { shippingAddress }
    });
    window.location.href = "orders.html?placed=" + order.id;
  } catch (err) {
    showAlert("alert-box", err.message);
    btn.disabled = false;
    btn.textContent = "Place order";
  }
});

Session.requireAuth();
renderHeader();
loadReview();
