document.addEventListener('DOMContentLoaded', function() {

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const errorMessageDiv = document.getElementById('errorMessage');

            fetch('/mywebapp/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, password: password })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('loginForm').style.display = 'none';
                    const messageDiv = document.getElementById('errorMessage');
                    messageDiv.textContent = '¡Sesión iniciada con éxito!';
                    messageDiv.style.color = 'green';
                } else {
                    errorMessageDiv.textContent = data.message;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                errorMessageDiv.textContent = 'Ocurrió un error al conectar con el servidor.';
            });
        });
    }

    const recoverForm = document.getElementById('recoverForm');
    if (recoverForm) {
        recoverForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const email = document.getElementById('recoverEmail').value;
            const messageContainer = document.getElementById('message-container');
            
            // Muestra un mensaje de "Procesando..."
            messageContainer.innerHTML = `<p class="info-message">Procesando su solicitud...</p>`;

            fetch('/mywebapp/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    messageContainer.innerHTML = `<p class="info-message">${data.message}</p>`;
                } else {
                    messageContainer.innerHTML = `<p class="error-message">${data.message}</p>`;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                messageContainer.innerHTML = '<p class="error-message">Ocurrió un error al conectar con el servidor.</p>';
            });
        });
    }
    
    // Código para el registro de usuarios
    const registerForm = document.getElementById('registerForm');
    // 1. Se busca el formulario de registro en la página. Si existe...
    if (registerForm) {
        // 2. Se añade un "escuchador" para el evento 'submit'. Esta función se ejecutará cuando el usuario presione el botón de registrarse.
        registerForm.addEventListener('submit', function(event) {
            // 3. ¡Línea CRÍTICA! Evita que el navegador envíe el formulario de la forma tradicional (recargando la página).
            event.preventDefault();
    
            // 4. Se busca el div donde se mostrarán los mensajes y se limpia su contenido.
            const messageDiv = document.getElementById('message');
            messageDiv.textContent = '';
    
            // 5. Se recogen todos los datos de los campos del formulario.
            const formData = new FormData(event.target);
            // 6. Se convierten los datos a un objeto JavaScript simple, ej: {nombre: "Carlos", email: "c@c.com", ...}
            const data = Object.fromEntries(formData.entries());
    
            // 7. Se envían los datos al servidor usando la API Fetch.
            fetch('/mywebapp/register', { // Esta será la URL de tu RegisterServlet
                method: 'POST', // El método es POST porque estamos creando un nuevo recurso (un usuario).
                headers: {
                    'Content-Type': 'application/json' // Se avisa al servidor que el cuerpo de la petición es un JSON.
                },
                body: JSON.stringify(data) // Se convierte el objeto JavaScript a un texto en formato JSON para poder enviarlo.
            })
            // 8. Cuando el servidor responde, esta promesa se resuelve. Se convierte la respuesta (que también es JSON) a un objeto JavaScript.
            .then(response => response.json())
            // 9. Ahora se trabaja con el objeto 'result' que envió el servidor.
            .then(result => {
                // 10. Si el servidor respondió con 'success: true'...
                if (result.success) {
                    messageDiv.textContent = '¡Registro exitoso! Redirigiendo al login...';
                    messageDiv.style.color = 'green';
                    // Se espera 2 segundos y se redirige al usuario a la página de inicio de sesión.
                    setTimeout(() => {
                        window.location.href = 'main.html';
                    }, 2000);
                } else {
                    // 11. Si el servidor respondió con 'success: false', se muestra el mensaje de error que vino en la respuesta.
                    messageDiv.textContent = result.message || 'Error en el registro. Inténtalo de nuevo.';
                    messageDiv.style.color = 'red';
                }
            })
            // 12. Si ocurre un error de red (ej: el servidor está caído o no se puede conectar), se ejecuta este bloque.
            .catch(error => {
                console.error('Error en la petición de registro:', error);
                messageDiv.textContent = 'Error de conexión con el servidor.';
                messageDiv.style.color = 'red';
            });
        });
    }
});