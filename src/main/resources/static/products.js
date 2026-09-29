/* products.js — loads and filters the product catalog on products.html */

let allCategories = [];

async function loadCategories() {
  try {
    allCategories = await apiRequest("/products/categories", { auth: false });
    const select = document.getElementById("category-filter");
    allCategories.forEach(cat => {
      const opt = document.createElement("option");
      opt.value = cat.id;
      opt.textContent = cat.name;
      select.appendChild(opt);
    });
  } catch (e) { /* categories are optional, ignore failure */ }
}

function renderProducts(products) {
  const grid = document.getElementById("product-grid");

  if (!products.length) {
    grid.innerHTML = `<p class="muted">No products found.</p>`;
    return;
  }

  grid.innerHTML = products.map(p => `
    <div class="product-card">
      <a href="product-detail.html?id=${p.id}" class="product-thumb">
        ${p.imageUrl
          ? `<img src="${p.imageUrl}" alt="${p.name}">`
          : `<span>No image</span>`}
      </a>
      <div class="product-info">
        <a href="product-detail.html?id=${p.id}" class="product-name">${p.name}</a>
        <div class="product-price">${formatCurrency(p.price)}</div>
        <div class="product-stock">${p.stock > 0 ? p.stock + " in stock" : "Out of stock"}</div>
        <button class="btn btn-dark btn-block" ${p.stock === 0 ? "disabled" : ""}
                onclick="quickAdd(${p.id})">Add to cart</button>
      </div>
    </div>
  `).join("");
}

async function loadProducts() {
  hideAlert("alert-box");
  const keyword = document.getElementById("search-input").value.trim();
  const categoryId = document.getElementById("category-filter").value;

  let query = "";
  if (keyword) query = "?keyword=" + encodeURIComponent(keyword);
  else if (categoryId) query = "?categoryId=" + categoryId;

  try {
    const products = await apiRequest("/products" + query, { auth: false });
    renderProducts(products);
  } catch (err) {
    showAlert("alert-box", "Could not load products: " + err.message);
  }
}

async function quickAdd(productId) {
  if (!Session.isLoggedIn()) {
    window.location.href = "login.html";
    return;
  }
  try {
    await apiRequest("/cart", { method: "POST", body: { productId, quantity: 1 } });
    updateCartBadge();
  } catch (err) {
    alert(err.message);
  }
}

document.getElementById("search-input").addEventListener("input", debounce(loadProducts, 350));
document.getElementById("category-filter").addEventListener("change", loadProducts);

function debounce(fn, delay) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

renderHeader("products");
loadCategories();
loadProducts();
