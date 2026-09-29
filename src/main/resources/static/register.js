/* register.js — handles new customer sign-up on register.html */

document.getElementById("register-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert("alert-box");

  const payload = {
    fullName: document.getElementById("fullName").value.trim(),
    email: document.getElementById("email").value.trim(),
    phone: document.getElementById("phone").value.trim(),
    address: document.getElementById("address").value.trim(),
    password: document.getElementById("password").value
  };

  const submitBtn = document.getElementById("submit-btn");
  submitBtn.disabled = true;
  submitBtn.textContent = "Creating account...";

  try {
    const auth = await apiRequest("/auth/register", {
      method: "POST",
      auth: false,
      body: payload
    });

    Session.setAuth(auth);
    window.location.href = "products.html";

  } catch (err) {
    showAlert("alert-box", err.message || "Could not create account");
    submitBtn.disabled = false;
    submitBtn.textContent = "Create account";
  }
});
