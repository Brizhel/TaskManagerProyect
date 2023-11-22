// URL de la API
const apiUrl = 'http://localhost/user/profile';

// Función para realizar el inicio de sesión
function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Realizar una solicitud POST al endpoint de inicio de sesión
    fetch('http://localhost/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            username: username,
            password: password,
        }),
    })
    .then(response => response.json())
    .then(data => {
        // Almacenar el token en el almacenamiento local
        localStorage.setItem('token', data.token);
        document.getElementById('result').innerText = 'Login successful!';
    })
    .catch(error => {
        console.error('Error during login:', error);
        document.getElementById('result').innerText = 'Login failed!';
    });
}

// Función para realizar una solicitud protegida a la API
function fetchUserProfile() {
    const token = localStorage.getItem('token');
	console.log('Token antes de enviar al servidor:', token);

    if (!token) {
        document.getElementById('result').innerText = 'Token not found. Please login.';
        return;
    }

    // Realizar una solicitud GET al endpoint protegido
    fetch(apiUrl, {
        headers: {
            'Authorization': 'Bearer ' + token,
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Unauthorized');
        }
        return response.json();
    })
    .then(data => {
        document.getElementById('result').innerText = 'User Profile: ' + JSON.stringify(data);
    })
    .catch(error => {
        console.error('Error during API request:', error);
        document.getElementById('result').innerText = 'Failed to fetch user profile. Please check your token.';
    });
}
function logout() {
    // Eliminar el token del almacenamiento local
    localStorage.removeItem('token');

    // Redirigir al usuario a la página de inicio de sesión
    window.location.href = '/login';
}