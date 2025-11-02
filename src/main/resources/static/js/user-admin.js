document.addEventListener('DOMContentLoaded', function() {
    const usersTbody = document.getElementById('usersTbody');
    const newUserBtn = document.getElementById('newUserBtn');
    const userModalEl = document.getElementById('userModal');
    const userModal = new bootstrap.Modal(userModalEl);
    const deleteModalEl = document.getElementById('deleteConfirmModal');
    const deleteModal = deleteModalEl ? new bootstrap.Modal(deleteModalEl) : null;
    const userForm = document.getElementById('userForm');
    const saveUserBtn = document.getElementById('saveUserBtn');

    function formatRoles(roles) {
        if (!roles) return '';
        return roles.map(r => r.nombre || r).join(', ');
    }

    // Cargar opciones de roles y especialidades desde el backend
    function fetchRolesAndEspecialidades() {
        // Roles
        fetch('/api/roles')
            .then(r => r.json())
            .then(roles => {
                const rolesSelect = document.getElementById('rolesSelect');
                if (!rolesSelect) return;
                // Usar el atributo disabled para el placeholder
                rolesSelect.innerHTML = '<option value="" disabled selected>Seleccione un rol</option>';
                roles.forEach(role => {
                    const option = document.createElement('option');
                    option.value = role.nombre;
                    option.textContent = role.nombre;
                    rolesSelect.appendChild(option);
                });
            }).catch(err => console.error('No se pudieron cargar roles', err));

        // Especialidades
        fetch('/api/especialidades')
            .then(r => r.json())
            .then(especialidades => {
                const espSelect = document.getElementById('especialidadSelect');
                if (!espSelect) return;
                // Mantener la opción 'Sin especificar' y añadir las demás
                espSelect.innerHTML = '<option value="">Sin especificar</option>';
                especialidades.forEach(e => {
                    const opt = document.createElement('option');
                    opt.value = e;
                    opt.textContent = e;
                    espSelect.appendChild(opt);
                });
            }).catch(err => console.error('No se pudieron cargar especialidades', err));
    }

    function fetchUsers() {
        usersTbody.innerHTML = '<tr><td colspan="6" class="text-center">Cargando...</td></tr>';
        fetch('/api/usuarios')
            .then(r => r.json())
            .then(users => {
                if (!Array.isArray(users) || users.length === 0) {
                    usersTbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay usuarios.</td></tr>';
                    return;
                }
                usersTbody.innerHTML = '';
                users.forEach(u => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${u.id}</td>
                        <td>${u.nombre || ''} ${u.apellido || ''}</td>
                        <td>${u.email || ''}</td>
                        <td>${formatRoles(u.roles)}</td>
                        <td>${(u.active || u.isActive) ? 'Sí' : 'No'}</td>
                        <td>
                            <button class="btn btn-sm btn-primary btn-edit" data-id="${u.id}">Editar</button>
                            <button class="btn btn-sm btn-danger btn-delete" data-id="${u.id}">Eliminar</button>
                            <button class="btn btn-sm btn-secondary btn-toggle" data-id="${u.id}">${(u.active || u.isActive) ? 'Desactivar' : 'Activar'}</button>
                        </td>
                    `;
                    usersTbody.appendChild(tr);
                });
                attachRowEvents();
            })
            .catch(err => {
                console.error(err);
                usersTbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error al cargar usuarios.</td></tr>';
            });
    }

    function attachRowEvents() {
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.addEventListener('click', () => openEdit(btn.dataset.id));
        });
        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', () => showDeleteModal(btn.dataset.id));
        });
        document.querySelectorAll('.btn-toggle').forEach(btn => {
            btn.addEventListener('click', () => toggleActive(btn.dataset.id));
        });
    }

    // Mostrar modal de confirmación con opciones para eliminar o alternar estado
    function showDeleteModal(id) {
        if (!deleteModal) {
            // Fallback: confirmar con window.confirm
            if (!confirm('¿Eliminar usuario #' + id + '? Esta acción es definitiva.')) return;
            fetch(`/api/usuarios/${id}`, { method: 'DELETE' })
                .then(r => { if (r.ok) fetchUsers(); else alert('Error al eliminar'); })
                .catch(err => { console.error(err); alert('Error de red'); });
            return;
        }

        // Llenar mensaje con info del usuario si está disponible
        const messageEl = document.getElementById('deleteModalMessage');
        messageEl.textContent = '¿Realmente quiere eliminar este usuario?';

        // Guardar id en botones dataset
        const modalDeleteBtn = document.getElementById('modalDeleteBtn');
        const modalToggleBtn = document.getElementById('modalToggleBtn');
        modalDeleteBtn.dataset.userid = id;
        modalToggleBtn.dataset.userid = id;

        deleteModal.show();
    }

    // Handler para el botón Eliminar definitivamente
    if (document.getElementById('modalDeleteBtn')) {
        document.getElementById('modalDeleteBtn').addEventListener('click', function() {
            const id = this.dataset.userid;
            if (!id) return;
            fetch(`/api/usuarios/${id}`, { method: 'DELETE' })
                .then(r => {
                    if (r.ok) {
                        deleteModal.hide();
                        fetchUsers();
                    } else {
                        r.text().then(t => alert('Error al eliminar: ' + t));
                    }
                })
                .catch(err => { console.error(err); alert('Error de red'); });
        });
    }

    // Handler para Activar/Desactivar desde el modal
    if (document.getElementById('modalToggleBtn')) {
        document.getElementById('modalToggleBtn').addEventListener('click', function() {
            const id = this.dataset.userid;
            if (!id) return;
            toggleActive(id).then(() => {
                deleteModal.hide();
                fetchUsers();
            }).catch(err => {
                console.error(err);
                alert('Error al alternar estado: ' + err.message);
            });
        });
    }

    function openEdit(id) {
        // Cargar datos del usuario por id y abrir modal
        fetch(`/api/usuarios/${id}`)
            .then(r => {
                if (!r.ok) throw new Error('Usuario no encontrado');
                return r.json();
            })
            .then(user => {
                document.getElementById('userId').value = user.id;
                document.getElementById('nombre').value = user.nombre || '';
                document.getElementById('apellido').value = user.apellido || '';
                document.getElementById('email').value = user.email || '';
                
                // Roles: seleccionar el rol del usuario
                const rolesSelect = document.getElementById('rolesSelect');
                if (rolesSelect && user.roles && user.roles.length > 0) {
                    rolesSelect.value = user.roles[0].nombre || user.roles[0];
                } else {
                    rolesSelect.value = '';
                }
                
                // Especialidad
                const espSelect = document.getElementById('especialidadSelect');
                if (espSelect) {
                    espSelect.value = user.especialidad || '';
                }
                document.getElementById('isActiveCheck').checked = !!(user.active || user.isActive);
                document.getElementById('password').value = '';
                document.getElementById('userModalTitle').textContent = 'Editar usuario';
                userModal.show();
            })
            .catch(err => { console.error(err); alert('Error al cargar usuario: ' + err.message); });
    }


    // deleteUser se mantiene como fallback pero no se usa directamente ahora
    function deleteUser(id) {
        return fetch(`/api/usuarios/${id}`, { method: 'DELETE' })
            .then(r => {
                if (r.ok) return true;
                throw new Error('Error al eliminar');
            });
    }

    function toggleActive(id) {
        // Obtener usuario por id, invertir isActive y llamar PUT /api/usuarios/{id}
        fetch(`/api/usuarios/${id}`)
            .then(r => {
                if (!r.ok) throw new Error('Usuario no encontrado');
                return r.json();
            })
            .then(user => {
                const updated = {
                    nombre: user.nombre,
                    apellido: user.apellido,
                    email: user.email,
                    roles: user.roles,
                    isActive: !user.isActive
                };
                return fetch(`/api/usuarios/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updated)
                });
            })
            .then(r => {
                if (r.ok) return fetchUsers();
                return r.text().then(t => { throw new Error(t || 'No se pudo actualizar'); });
            })
            .catch(err => { console.error(err); throw err; });
    }

    saveUserBtn.addEventListener('click', () => {
        const id = document.getElementById('userId').value;
        const payload = {
            nombre: document.getElementById('nombre').value,
            apellido: document.getElementById('apellido').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            // rol como array con un solo objeto { nombre: 'ROLE' }
            roles: [{ nombre: document.getElementById('rolesSelect').value }],
            especialidad: document.getElementById('especialidadSelect').value || null,
            isActive: document.getElementById('isActiveCheck').checked
        };
        if (id) {
            fetch(`/api/usuarios/${id}`, {
                method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
            }).then(r => { if (r.ok) { userModal.hide(); fetchUsers(); } else alert('Error al actualizar'); });
        } else {
            // Crear nuevo usuario (endpoint de register) - enviar 'rol' como string (primer rol seleccionado)
            const firstRole = (payload.roles && payload.roles.length > 0) ? payload.roles[0].nombre : '';
            const registerPayload = {
                nombre: payload.nombre,
                apellido: payload.apellido,
                email: payload.email,
                password: payload.password,
                especialidad: payload.especialidad,
                rol: firstRole
            };
            fetch('/api/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(registerPayload) })
                .then(r => r.json())
                .then(res => {
                    if (res.success) { userModal.hide(); fetchUsers(); } else alert(res.message || 'Error al crear');
                }).catch(err => { console.error(err); alert('Error de red'); });
        }
    });

    newUserBtn.addEventListener('click', () => {
        document.getElementById('userForm').reset();
        document.getElementById('userId').value = '';
        // limpiar selects
        const rolesSelect = document.getElementById('rolesSelect');
        if (rolesSelect) Array.from(rolesSelect.options).forEach(o => o.selected = false);
        const espSelect = document.getElementById('especialidadSelect');
        if (espSelect) espSelect.value = '';
        document.getElementById('userModalTitle').textContent = 'Nuevo usuario';
        userModal.show();
    });

    // Búsqueda simple
    document.getElementById('searchBtn').addEventListener('click', () => {
        const q = document.getElementById('searchInput').value.toLowerCase();
        document.querySelectorAll('#usersTbody tr').forEach(tr => {
            const txt = tr.textContent.toLowerCase();
            tr.style.display = txt.includes(q) ? '' : 'none';
        });
    });

    // Carga inicial
    fetchRolesAndEspecialidades();
    fetchUsers();
});
