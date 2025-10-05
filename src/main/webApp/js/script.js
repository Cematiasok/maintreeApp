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
});