document.addEventListener('DOMContentLoaded', function() {

    // ==================== LOGIN FORM ====================
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const errorMessageDiv = document.getElementById('errorMessage');

            fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, password: password })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Si el usuario es admin, redirigir al panel de administración
                    if (data.isAdmin) {
                        window.location.href = 'user-admin.html';
                        return;
                    }
                    // Usuario normal: mostrar mensaje o redirigir al inicio
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

    // ==================== RECOVER PASSWORD FORM ====================
    const recoverForm = document.getElementById('recoverForm');
    if (recoverForm) {
        recoverForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const email = document.getElementById('recoverEmail').value;
            const messageContainer = document.getElementById('message-container');
            
            // Muestra un mensaje de "Procesando..."
            messageContainer.innerHTML = `<p class="info-message">Procesando su solicitud...</p>`;

            // Nota: el controlador REST está bajo el prefijo /api
            fetch('/api/forgot-password', {
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
    
    // ==================== REGISTER FORM ====================
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
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
            fetch('/api/register', { // URL actualizada para coincidir con el controlador Spring Boot
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
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
                        window.location.href = 'index.html';
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

    // ==================== ADMIN ROLE CONFIRMATION ====================
    const adminConfirmForm = document.getElementById('adminConfirmForm');
    
    // 1. Si encontramos el formulario del panel de admin...
    if (adminConfirmForm) {
        const adminUserListBody = document.getElementById('user-list-body');

        /**
         * Rellena la tabla (modificado para el formulario)
         */
        function populateTable(users) {
            adminUserListBody.innerHTML = ''; // Limpiar "Cargando..."

            if (users.length === 0) {
                adminUserListBody.innerHTML = `<tr><td colspan="4" class="text-center">No hay usuarios pendientes de aprobación.</td></tr>`;
                return;
            }

            users.forEach(user => {
                const rowHTML = `
                    <tr id="user-row-${user.id}">
                        <td>${user.nombre} ${user.apellido}</td>
                        <td>${user.email}</td>
                        <td>${user.rolSolicitado}</td>
                        <td>
                            <input type="checkbox" 
                                   name="userIds" 
                                   value="${user.id}" 
                                   class="confirm-check form-check-input">
                        </td>
                    </tr>
                `;
                adminUserListBody.innerHTML += rowHTML;
            });
        }

        /**
         * Busca los usuarios pendientes de tu API (GET)
         * (Esta función es igual que antes)
         */
        function fetchPendingUsers() {
            // Llamamos al endpoint nuevo que devuelve solo usuarios pendientes
            fetch('/api/admin/usuarios-pendientes')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error al cargar los usuarios: ' + response.statusText);
                    }
                    return response.json();
                })
                .then(users => {
                        // El endpoint ya devuelve solo usuarios pendientes (isActive == false)
                        populateTable(users);
                })
                .catch(error => {
                    console.error(error);
                    adminUserListBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Error al cargar datos.</td></tr>`;
                });
        }

        function populateTable(users) {
            adminUserListBody.innerHTML = ''; // Limpiar "Cargando..."

            if (users.length === 0) {
                adminUserListBody.innerHTML = `<tr><td colspan="4" class="text-center">No hay usuarios pendientes de aprobación.</td></tr>`;
                return;
            }

            users.forEach(user => {
                const roles = user.roles.map(rol => rol.nombre).join(', ');
                const rowHTML = `
                    <tr id="user-row-${user.id}">
                        <td>${user.nombre} ${user.apellido}</td>
                        <td>${user.email}</td>
                        <td>${roles}</td>
                        <td>
                            <input type="checkbox" 
                                   name="userIds" 
                                   value="${user.id}" 
                                   class="confirm-check form-check-input">
                        </td>
                    </tr>
                `;
                adminUserListBody.innerHTML += rowHTML;
            });
        }

        // --- PUNTO DE ENTRADA ---
        
        // 2. Escuchar el evento 'submit' del formulario
        adminConfirmForm.addEventListener('submit', handleMassApproval);

        // 1. Cargar los usuarios al iniciar la página
        fetchPendingUsers();

        function handleMassApproval(event) {
            event.preventDefault(); // Evita que la página se recargue

            // 1. Encontrar todos los checkboxes marcados
            const checkedBoxes = adminConfirmForm.querySelectorAll('input[name="userIds"]:checked');
            
            // 2. Extraer solo los IDs (del atributo 'value')
            const idsToApprove = Array.from(checkedBoxes).map(cb => cb.value);

            if (idsToApprove.length === 0) {
                alert('No ha seleccionado ningún usuario para aprobar.');
                return;
            }

            console.log('Enviando IDs para aprobar:', idsToApprove);

            // 3. Enviar el ARRAY de IDs al servidor (ruta del controlador: /api/admin/confirmar-roles-lote)
            fetch('/api/admin/confirmar-roles-lote', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(idsToApprove) // Enviamos un array de IDs, ej: [1, 5, 12]
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('¡Usuarios aprobados con éxito!');
                    // 4. Recargar la lista para mostrar solo los pendientes
                    fetchPendingUsers(); 
                } else {
                    alert('Error al aprobar: ' + (data.message || 'Error desconocido'));
                }
            })
            .catch(error => {
                console.error('Error de red:', error);
                alert('Error de conexión al enviar la confirmación.');
            });
        }
    }
    
    // ==================== RESET PASSWORD FORM ====================
    const resetForm = document.getElementById('reset-form');
    if (resetForm) {
        const params = new URLSearchParams(window.location.search);
        const token = params.get('token');
        const error = params.get('error');

        if (token) {
            document.getElementById('token-input').value = token;
        } else {
            // Si no hay token, no se puede proceder
            const formLogin = document.querySelector('.form-login');
            if(formLogin){
                formLogin.innerHTML = '<h2>Enlace inválido</h2><p>El enlace para restablecer la contraseña no es válido o ha expirado. Por favor, solicita uno nuevo.</p>';
            }
        }

        if (error) {
            const errorMessage = document.getElementById('error-message');
            if(errorMessage){
                errorMessage.textContent = decodeURIComponent(error.replace(/\+/g, ' '));
            }
        }

        resetForm.addEventListener('submit', function(e) {
            e.preventDefault(); // Prevent default form submission

            const newPass = document.getElementById('newPassword').value;
            const confirmPass = document.getElementById('confirmPassword').value;
            const errorMsg = document.getElementById('error-message');

            if (newPass !== confirmPass) {
                errorMsg.textContent = 'Las contraseñas no coinciden';
                errorMsg.style.display = 'block';
                return;
            }

            // Clear previous errors
            errorMsg.textContent = '';
            errorMsg.style.display = 'none';

            const formData = new FormData(e.target);
            const data = Object.fromEntries(formData.entries());

            // Enviar al endpoint REST correcto con token como query param y cuerpo JSON
            const tokenParam = token ? '?token=' + encodeURIComponent(token) : '';
            fetch('/api/reset-password' + tokenParam, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ newPassword: newPass, confirmPassword: confirmPass })
            })
            .then(async response => {
                if (response.ok) {
                    // Redirigir al login o página principal con indicador de éxito
                    window.location.href = 'index.html?reset=success';
                } else {
                    // Leer el texto devuelto por el servidor (mensaje de error)
                    const text = await response.text();
                    errorMsg.textContent = text || 'Error al restablecer la contraseña.';
                    errorMsg.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                errorMsg.textContent = 'Hubo un error al conectar con el servidor.';
                errorMsg.style.display = 'block';
            });
        });
    }
});