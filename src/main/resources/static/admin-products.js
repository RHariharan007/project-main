/* admin-products.js — full CRUD for the product catalog on admin-products.html */

let categories = [];

async function loadCategories() {
  categories = await apiRequest("/products/categories", { auth: false });
  const select = document.getElementById("categoryId");
  select.innerHTML = categories.map(c => `<option value="${c.id}">${c.name}</option>`).join("");
}

function categoryName(id) {
  const cat = categories.find(c => c.id === id);
  return cat ? cat.name : "—";
}

async function loadProducts() {
  hideAlert("alert-box");
  try {
    const products = await apiRequest("/products", { auth: false });
    const tbody = document.getElementById("products-tbody");

    if (!products.length) {
      tbody.innerHTML = `<tr><td colspan="5" class="muted">No products yet. Add your first one.</td></tr>`;
      return;
    }

    tbody.innerHTML = products.map(p => `
      <tr>
        <td>${p.name}</td>
        <td>${p.category ? p.category.name : "—"}</td>
        <td>${formatCurrency(p.price)}</td>
        <td>${p.stock}</td>
        <td class="table-actions">
          <button class="icon-btn" onclick='openEditModal(${JSON.stringify(p).replace(/'/g, "&apos;")})'>Edit</button>
          <button class="icon-btn" style="color:var(--rose);" onclick="deleteProduct(${p.id})">Delete</button>
        </td>
      </tr>
    `).join("");
  } catch (err) {
    showAlert("alert-box", "Could not load products: " + err.message);
  }
}

function openAddModal() {
  document.getElementById("modal-title").textContent = "Add product";
  document.getElementById("product-form").reset();
  document.getElementById("product-id").value = "";
  hideAlert("modal-alert");
  document.getElementById("product-modal").classList.add("show");
}

function openEditModal(product) {
  document.getElementById("modal-title").textContent = "Edit product";
  document.getElementById("product-id").value = product.id;
  document.getElementById("name").value = product.name;
  document.getElementById("description").value = product.description || "";
  document.getElementById("price").value = product.price;
  document.getElementById("stock").value = product.stock;
  document.getElementById("imageUrl").value = product.imageUrl || "";
  document.getElementById("categoryId").value = product.category ? product.category.id : "";
  hideAlert("modal-alert");
  document.getElementById("product-modal").classList.add("show");
}

function closeModal() {
  document.getElementById("product-modal").classList.remove("show");
}

async function deleteProduct(id) {
  if (!confirm("Remove this product from the catalog?")) return;
  try {
    await apiRequest(`/admin/products/${id}`, { method: "DELETE" });
    loadProducts();
  } catch (err) {
    showAlert("alert-box", err.message);
  }
}

document.getElementById("add-product-btn").addEventListener("click", openAddModal);
document.getElementById("modal-close-btn").addEventListener("click", closeModal);

document.getElementById("product-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert("modal-alert");

  const id = document.getElementById("product-id").value;
  const payload = {
    name: document.getElementById("name").value.trim(),
    description: document.getElementById("description").value.trim(),
    price: parseFloat(document.getElementById("price").value),
    stock: parseInt(document.getElementById("stock").value, 10),
    imageUrl: document.getElementById("imageUrl").value.trim(),
    categoryId: document.getElementById("categoryId").value ? parseInt(document.getElementById("categoryId").value, 10) : null
  };

  const btn = document.getElementById("save-product-btn");
  btn.disabled = true;
  btn.textContent = "Saving...";

  try {
    if (id) {
      await apiRequest(`/admin/products/${id}`, { method: "PUT", body: payload });
    } else {
      await apiRequest("/admin/products", { method: "POST", body: payload });
    }
    closeModal();
    loadProducts();
  } catch (err) {
    showAlert("modal-alert", err.message);
  } finally {
    btn.disabled = false;
    btn.textContent = "Save product";
  }
});

Session.requireAdmin();
renderHeader("admin-products");
loadCategories().then(loadProducts);
