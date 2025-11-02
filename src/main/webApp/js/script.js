// --- ¡NUEVO BLOQUE! Panel de Aprobación de Admin (Lógica de Formulario) ---
// (Este código debe estar dentro de tu 'DOMContentLoaded' general)

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
            adminUserListBody.innerHTML = `<tr><td colspan="4" style="text-align: center;">No hay usuarios pendientes de aprobación.</td></tr>`;
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
                               class="confirm-check">
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
        fetch('/mywebapp/admin/usuarios-pendientes')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar los usuarios: ' + response.statusText);
                }
                return response.json();
            })
            .then(users => {
                populateTable(users);
            })
            .catch(error => {
                console.error(error);
                adminUserListBody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: red;">Error al cargar datos.</td></tr>`;
            });
    }

    /**
     * NUEVA FUNCIÓN: Se activa cuando se envía el formulario
     */
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

        // 3. Enviar el ARRAY de IDs al servidor
        // (Nota: la URL cambió a 'confirmar-roles-lote' para reflejar la acción)
        fetch('/mywebapp/admin/confirmar-roles-lote', {
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
                alert('Error al aprobar: ' (data.message || 'Error desconocido'));
            }
        })
        .catch(error => {
            console.error('Error de red:', error);
            alert('Error de conexión al enviar la confirmación.');
        });
    }

    // --- PUNTO DE ENTRADA ---
    
    // 2. Escuchar el evento 'submit' del formulario
    adminConfirmForm.addEventListener('submit', handleMassApproval);

    // 1. Cargar los usuarios al iniciar la página
    fetchPendingUsers();
}