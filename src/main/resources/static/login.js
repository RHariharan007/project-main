/* login.js — handles the login form submission on login.html */

document.getElementById("login-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert("alert-box");

  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value;
  const submitBtn = document.getElementById("submit-btn");

  submitBtn.disabled = true;
  submitBtn.textContent = "Logging in...";

  try {
    const auth = await apiRequest("/auth/login", {
      method: "POST",
      auth: false,
      body: { email, password }
    });

    Session.setAuth(auth);

    // Send admins straight to their dashboard, customers to the shop.
    window.location.href = auth.role === "ADMIN" ? "admin-dashboard.html" : "products.html";

  } catch (err) {
    showAlert("alert-box", err.message || "Invalid email or password");
    submitBtn.disabled = false;
    submitBtn.textContent = "Log in";
  }
});
