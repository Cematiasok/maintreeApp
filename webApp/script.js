document.getElementById("loginForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const errorMessage = document.getElementById("errorMessage");

    // Simulación de credenciales válidas
    const usuarioValido = "usuario123";
    const passwordValida = "123456";

    if (email === usuarioValido && password === passwordValida) {
        errorMessage.textContent = "";
        alert("Inicio de sesión exitoso ✅"); 
        // Aquí puedes redirigir al dashboard
        // window.location.href = "dashboard.html";
    } else {
        errorMessage.textContent = "Credenciales inválidas";
        errorMessage.style.color = "red";
        errorMessage.style.textAlign = "center";
        errorMessage.style.marginTop = "10px";
    }
});

// Redirección a recuperación de contraseña
document.querySelector(".forgot").addEventListener("click", function(e) {
    e.preventDefault();
    window.location.href = "recuperar.html"; // Nueva página de recuperación
});
