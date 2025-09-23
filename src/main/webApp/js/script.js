// Espera a que todo el contenido del HTML se cargue
document.addEventListener('DOMContentLoaded', function() {

    // 1. Obtener el formulario
    const loginForm = document.getElementById('loginForm');

    // 2. Escuchar el evento 'submit' del formulario
    loginForm.addEventListener('submit', function(event) {

        // Previene que el formulario se envíe de la forma tradicional (recargando la página)
        event.preventDefault();

        // 3. Capturar los datos de los inputs
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const errorMessageDiv = document.getElementById('errorMessage');

        // 4. Enviar los datos al backend usando fetch
        fetch('http://localhost:8080/main.html', { // <-- ¡URL CLAVE! Apunta a tu Controlador Java
            method: 'POST', // Usamos POST para enviar datos sensibles
            headers: {
                'Content-Type': 'application/json' // Le decimos al backend que enviaremos JSON
            },
            body: JSON.stringify({ // Convertimos los datos de JS a un string JSON
                email: email,
                password: password
            })
        })
        .then(response => response.json()) // Esperamos una respuesta JSON del backend
        .then(data => {
            // 5. Procesar la respuesta del backend
            if (data.success) {
                // Si el login es exitoso, redirigimos a otra página
                window.location.href = 'dashboard.html';
            } else {
                // Si falla, mostramos el mensaje de error que nos envió el backend
                errorMessageDiv.textContent = data.message;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            errorMessageDiv.textContent = 'Ocurrió un error al conectar con el servidor.';
        });
    });
});