document.addEventListener('DOMContentLoaded', function() {
    const usersTbody = document.getElementById('usersTbody');
    const newUserBtn = document.getElementById('newUserBtn');
    const userModalEl = document.getElementById('userModal');
    const userModal = new bootstrap.Modal(userModalEl);
    const userForm = document.getElementById('userForm');
    const saveUserBtn = document.getElementById('saveUserBtn');

    function formatRoles(roles) {
        if (!roles) return '';
        return roles.map(r => r.nombre || r).join(', ');
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
            btn.addEventListener('click', () => deleteUser(btn.dataset.id));
        });
        document.querySelectorAll('.btn-toggle').forEach(btn => {
            btn.addEventListener('click', () => toggleActive(btn.dataset.id));
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
                document.getElementById('rolesInput').value = (user.roles || []).map(r => r.nombre || r).join(',');
                document.getElementById('isActiveCheck').checked = !!(user.active || user.isActive);
                document.getElementById('password').value = '';
                document.getElementById('userModalTitle').textContent = 'Editar usuario';
                userModal.show();
            })
            .catch(err => { console.error(err); alert('Error al cargar usuario: ' + err.message); });
    }

    function deleteUser(id) {
        if (!confirm('¿Eliminar usuario #' + id + '?')) return;
        fetch(`/api/usuarios/${id}`, { method: 'DELETE' })
            .then(r => {
                if (r.ok) {
                    fetchUsers();
                } else {
                    alert('Error al eliminar');
                }
            })
            .catch(err => { console.error(err); alert('Error de red'); });
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
                if (r.ok) fetchUsers(); else r.text().then(t => { alert('No se pudo actualizar: ' + t); });
            })
            .catch(err => { console.error(err); alert('Error al actualizar estado: ' + err.message); });
    }

    saveUserBtn.addEventListener('click', () => {
        const id = document.getElementById('userId').value;
        const payload = {
            nombre: document.getElementById('nombre').value,
            apellido: document.getElementById('apellido').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            roles: document.getElementById('rolesInput').value.split(',').map(s => s.trim()).filter(Boolean),
            isActive: document.getElementById('isActiveCheck').checked
        };
        if (id) {
            fetch(`/api/usuarios/${id}`, {
                method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
            }).then(r => { if (r.ok) { userModal.hide(); fetchUsers(); } else alert('Error al actualizar'); });
        } else {
            // Crear nuevo usuario (endpoint de register)
            fetch('/api/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) })
                .then(r => r.json())
                .then(res => {
                    if (res.success) { userModal.hide(); fetchUsers(); } else alert(res.message || 'Error al crear');
                }).catch(err => { console.error(err); alert('Error de red'); });
        }
    });

    newUserBtn.addEventListener('click', () => {
        document.getElementById('userForm').reset();
        document.getElementById('userId').value = '';
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
    fetchUsers();
});
