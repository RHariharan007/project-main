/* cart.js — view, update quantity, and remove items on cart.html */

function renderCart(items, total) {
  const container = document.getElementById("cart-items");

  if (!items.length) {
    container.innerHTML = `
      <div class="empty-cart">
        <p class="muted">Your cart is empty.</p>
        <a href="products.html" class="btn btn-dark mt-24">Continue shopping</a>
      </div>`;
    document.getElementById("checkout-btn").classList.add("btn-disabled-link");
    document.getElementById("checkout-btn").style.pointerEvents = "none";
    document.getElementById("checkout-btn").style.opacity = "0.5";
    document.getElementById("summary-total").textContent = formatCurrency(0);
    return;
  }

  container.innerHTML = items.map(item => `
    <div class="cart-item" data-id="${item.id}">
      <div class="cart-item-thumb">
        ${item.product.imageUrl ? `<img src="${item.product.imageUrl}" alt="">` : "No image"}
      </div>
      <div>
        <div class="cart-item-name">${item.product.name}</div>
        <div class="cart-item-price">${formatCurrency(item.product.price)} each</div>
      </div>
      <div class="qty-control">
        <input type="number" min="1" max="${item.product.stock}" value="${item.quantity}"
               onchange="updateQty(${item.id}, this.value)">
      </div>
      <div style="text-align:right;">
        <div style="font-weight:600;">${formatCurrency(item.product.price * item.quantity)}</div>
        <button class="remove-link" onclick="removeItem(${item.id})">Remove</button>
      </div>
    </div>
  `).join("");

  document.getElementById("summary-total").textContent = formatCurrency(total);
}

async function loadCart() {
  hideAlert("alert-box");
  try {
    const data = await apiRequest("/cart");
    renderCart(data.items, data.total);
  } catch (err) {
    showAlert("alert-box", "Could not load cart: " + err.message);
  }
}

async function updateQty(cartItemId, quantity) {
  try {
    await apiRequest(`/cart/${cartItemId}?quantity=${quantity}`, { method: "PUT" });
    loadCart();
    updateCartBadge();
  } catch (err) {
    showAlert("alert-box", err.message);
  }
}

async function removeItem(cartItemId) {
  try {
    await apiRequest(`/cart/${cartItemId}`, { method: "DELETE" });
    loadCart();
    updateCartBadge();
  } catch (err) {
    showAlert("alert-box", err.message);
  }
}

Session.requireAuth();
renderHeader();
loadCart();
