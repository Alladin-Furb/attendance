const API_BASE_URL = window.CONFIRMACAO_API_URL || 'http://localhost:3001';
let mapInstance;
let mapMarker;
let currentUser = null;

// Define a data atual no carregamento para agilizar a confirmação.
document.addEventListener('DOMContentLoaded', () => {
    const dateInput = document.getElementById('confirmationDate');
    dateInput.value = getTodayDate();
    dateInput.readOnly = true;

    initializeBoardingMap();
    document.getElementById('loginForm').addEventListener('submit', login);
    document.getElementById('cancelPresenceButton').addEventListener('click', cancelTodayPresence);
    document.getElementById('useCurrentLocation').addEventListener('click', useCurrentLocation);
    document.getElementById('searchAddressBtn').addEventListener('click', searchAddress);
    document.getElementById('addressSearch').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') searchAddress();
    });
    loadSession();
});

function getTodayDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}

function getAuthToken() {
    return window.localStorage.getItem('attendanceAuthToken');
}

function getStoredAuthUser() {
    try {
        return JSON.parse(window.localStorage.getItem('attendanceAuthUser') || 'null');
    } catch {
        return null;
    }
}

function getAuthHeaders() {
    const token = getAuthToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
}

function setFormEnabled(enabled) {
    document.querySelectorAll('#presenceForm input, #presenceForm select, #presenceForm button').forEach((field) => {
        field.disabled = !enabled;
    });
    document.getElementById('studentName').readOnly = true;
    document.getElementById('confirmationDate').readOnly = true;
}

function getUserDisplayName(user) {
    return user?.name || user?.sub || user?.username || '';
}

function applyLoggedUser(user) {
    currentUser = user;
    const displayName = getUserDisplayName(user);

    document.getElementById('studentName').value = displayName;
    document.getElementById('loginSection').hidden = Boolean(displayName);
    setFormEnabled(Boolean(displayName));
}

async function loadSession() {
    setFormEnabled(false);

    if (!getAuthToken()) {
        applyLoggedUser(null);
        showFeedback('Entre para confirmar sua presenca.', true);
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
            headers: getAuthHeaders()
        });

        if (!response.ok) {
            throw new Error('Sessao expirada.');
        }

        const data = await response.json();
        applyLoggedUser({
            ...data.user,
            ...getStoredAuthUser()
        });
        showFeedback('Sessao ativa.');
    } catch (error) {
        window.localStorage.removeItem('attendanceAuthToken');
        window.localStorage.removeItem('attendanceAuthUser');
        applyLoggedUser(null);
        showFeedback(error.message || 'Entre novamente.', true);
    }
}

async function login(event) {
    event.preventDefault();

    const email = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Falha ao entrar.');
        }

        window.localStorage.setItem('attendanceAuthToken', data.token);
        window.localStorage.setItem('attendanceAuthUser', JSON.stringify(data.user));
        applyLoggedUser(data.user);
        showFeedback('Login realizado. Nome preenchido automaticamente.');
    } catch (error) {
        showFeedback(error.message || 'Erro ao entrar.', true);
    }
}

function initializeBoardingMap() {
    const defaultCenter = [-26.9194, -49.0661];

    mapInstance = L.map('boardingMap').setView(defaultCenter, 13);

    const streetLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap contributors'
    });

    const satelliteLayer = L.tileLayer(
        'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
        {
            maxZoom: 19,
            attribution: 'Tiles &copy; Esri'
        }
    );

    streetLayer.addTo(mapInstance);

    L.control.layers(
        {
            Rua: streetLayer,
            Satelite: satelliteLayer
        },
        {},
        { collapsed: true }
    ).addTo(mapInstance);

    mapInstance.on('click', (event) => {
        setCoordinates(event.latlng.lat, event.latlng.lng);
    });
}

async function searchAddress() {
    const query = document.getElementById('addressSearch').value.trim();

    if (!query) {
        showFeedback('Digite um endereço para buscar.', true);
        return;
    }

    const resultsContainer = document.getElementById('searchResults');
    resultsContainer.innerHTML = '<div class="search-result-item"><em>Buscando...</em></div>';
    resultsContainer.classList.add('visible');

    try {
        const response = await fetch(
            `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(query)}&format=json&limit=5`
        );
        const results = await response.json();

        if (!results || results.length === 0) {
            resultsContainer.innerHTML = '<div class="search-result-item"><em>Nenhum resultado encontrado</em></div>';
            return;
        }

        resultsContainer.innerHTML = results
            .map(
                (result) =>
                    `<div class="search-result-item" onclick="selectSearchResult('${result.lat}', '${result.lon}', '${result.display_name}')">
                    <div class="search-result-text">${result.display_name}</div>
                    <div class="search-result-coords">Lat: ${parseFloat(result.lat).toFixed(4)}, Lng: ${parseFloat(result.lon).toFixed(4)}</div>
                </div>`
            )
            .join('');
    } catch (error) {
        console.error('Erro ao buscar endereço:', error);
        resultsContainer.innerHTML = '<div class="search-result-item"><em>Erro ao buscar endereço</em></div>';
    }
}

function selectSearchResult(lat, lon, displayName) {
    setCoordinates(parseFloat(lat), parseFloat(lon));
    document.getElementById('boardingLocation').value = displayName;
    mapInstance.setView([parseFloat(lat), parseFloat(lon)], 17);

    document.getElementById('searchResults').classList.remove('visible');
    document.getElementById('addressSearch').value = '';
    showFeedback('Ótima! Localização definida.');
}

function setCoordinates(latitude, longitude) {
    const lat = Number(latitude.toFixed(6));
    const lng = Number(longitude.toFixed(6));

    document.getElementById('latitude').value = String(lat);
    document.getElementById('longitude').value = String(lng);
    document.getElementById('coordinatesPreview').textContent = `Lat: ${lat} | Lng: ${lng}`;

    if (!mapMarker) {
        mapMarker = L.marker([lat, lng]).addTo(mapInstance);
    } else {
        mapMarker.setLatLng([lat, lng]);
    }
}

function useCurrentLocation() {
    if (!navigator.geolocation) {
        showFeedback('Seu navegador nao suporta geolocalizacao.', true);
        return;
    }

    navigator.geolocation.getCurrentPosition(
        (position) => {
            const { latitude, longitude } = position.coords;
            setCoordinates(latitude, longitude);
            mapInstance.setView([latitude, longitude], 17);
            showFeedback('Localizacao atual definida no mapa.');
        },
        () => {
            showFeedback('Nao foi possivel obter sua localizacao atual.', true);
        },
        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        }
    );
}

async function confirmPresence(event) {
    event.preventDefault();

    const studentName = document.getElementById('studentName').value.trim();
    const confirmationDate = getTodayDate();
    const tripType = document.getElementById('tripType').value;
    const boardingLocation = document.getElementById('boardingLocation').value.trim();
    const latitude = document.getElementById('latitude').value;
    const longitude = document.getElementById('longitude').value;
    const boardingCoordinates = `${latitude},${longitude}`;

    if (!getAuthToken()) {
        showFeedback('Entre para confirmar sua presenca.', true);
        return;
    }

    if (!studentName || !confirmationDate || !tripType || !boardingLocation || !latitude || !longitude) {
        showFeedback('Preencha os campos e selecione a localizacao no mapa.', true);
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/presencas/confirmacao`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify({
                nomeAluno: studentName,
                dataConfirmacao: confirmationDate,
                tipoDeslocamento: tripType,
                localEmbarque: boardingCoordinates,
                latitude: Number(latitude),
                longitude: Number(longitude)
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Falha ao salvar confirmação.');
        }

        showFeedback('Presença confirmada e salva com sucesso.');
        document.getElementById('cancelPresenceButton').hidden = false;
        resetFormKeepingDate();
    } catch (error) {
        showFeedback(error.message || 'Erro ao confirmar presença.', true);
    }
}

async function cancelTodayPresence() {
    if (!getAuthToken()) {
        showFeedback('Entre para cancelar sua presenca.', true);
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/presencas/confirmacao/hoje`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify({
                nomeAluno: getUserDisplayName(currentUser)
            })
        });
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Falha ao cancelar presenca.');
        }

        document.getElementById('cancelPresenceButton').hidden = true;
        showFeedback(data.message || 'Presenca cancelada com sucesso.');
    } catch (error) {
        showFeedback(error.message || 'Erro ao cancelar presenca.', true);
    }
}

function resetFormKeepingDate() {
    const form = document.getElementById('presenceForm');
    const currentDate = getTodayDate();

    form.reset();
    document.getElementById('confirmationDate').value = currentDate;
    document.getElementById('studentName').value = getUserDisplayName(currentUser);
    document.getElementById('coordinatesPreview').textContent = 'Nenhuma coordenada selecionada';

    if (mapMarker) {
        mapInstance.removeLayer(mapMarker);
        mapMarker = null;
    }
}

function showFeedback(message, isError = false) {
    const feedback = document.getElementById('feedbackMessage');
    feedback.textContent = message;
    feedback.classList.toggle('error', isError);
    feedback.classList.toggle('success', !isError);
}
