/* product-detail.js — loads a single product by its ?id= query param */

function getProductIdFromUrl() {
  return new URLSearchParams(window.location.search).get("id");
}

function renderProduct(p) {
  const el = document.getElementById("product-detail");
  el.innerHTML = `
    <div class="detail-image">
      ${p.imageUrl ? `<img src="${p.imageUrl}" alt="${p.name}">` : `<span>No image available</span>`}
    </div>
    <div>
      <div class="detail-category">${p.category ? p.category.name : "Uncategorized"}</div>
      <h1 class="detail-title">${p.name}</h1>
      <div class="detail-price">${formatCurrency(p.price)}</div>
      <p class="detail-description">${p.description || "No description provided."}</p>
      <div class="detail-stock ${p.stock === 0 ? "muted" : ""}">
        ${p.stock > 0 ? p.stock + " units in stock" : "Currently out of stock"}
      </div>
      <div class="qty-row">
        <label for="qty" class="muted">Qty</label>
        <input type="number" id="qty" min="1" max="${p.stock}" value="1" ${p.stock === 0 ? "disabled" : ""}>
      </div>
      <button class="btn btn-primary" id="add-btn" ${p.stock === 0 ? "disabled" : ""}>Add to cart</button>
    </div>
  `;

  document.getElementById("add-btn")?.addEventListener("click", async () => {
    if (!Session.isLoggedIn()) {
      window.location.href = "login.html";
      return;
    }
    const quantity = parseInt(document.getElementById("qty").value, 10) || 1;
    try {
      await apiRequest("/cart", { method: "POST", body: { productId: p.id, quantity } });
      updateCartBadge();
      window.location.href = "cart.html";
    } catch (err) {
      showAlert("alert-box", err.message);
    }
  });
}

async function loadProduct() {
  const id = getProductIdFromUrl();
  if (!id) {
    showAlert("alert-box", "No product specified.");
    return;
  }
  try {
    const product = await apiRequest("/products/" + id, { auth: false });
    renderProduct(product);
  } catch (err) {
    showAlert("alert-box", "Could not load product: " + err.message);
  }
}

renderHeader("products");
loadProduct();
