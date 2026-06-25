const crypto = require('crypto');

const AUTH_SERVICE_URL = process.env.AUTH_SERVICE_URL || 'http://localhost:8081';

function base64UrlEncode(value) {
    return Buffer.from(JSON.stringify(value)).toString('base64url');
}

function base64UrlDecode(value) {
    return JSON.parse(Buffer.from(value, 'base64url').toString('utf8'));
}

function getSecret() {
    return process.env.AUTH_SERVICE_JWT_SECRET || process.env.JWT_SECRET || null;
}

function signToken(payload) {
    const secret = getSecret();

    if (!secret) {
        throw new Error('JWT_SECRET ou AUTH_SERVICE_JWT_SECRET deve estar configurado para login local');
    }

    const encodedHeader = base64UrlEncode({ alg: 'HS256', typ: 'JWT' });
    const encodedPayload = base64UrlEncode(payload);
    const signature = crypto
        .createHmac('sha256', secret)
        .update(`${encodedHeader}.${encodedPayload}`)
        .digest('base64url');

    return `${encodedHeader}.${encodedPayload}.${signature}`;
}

function safeEqual(left, right) {
    const leftBuffer = Buffer.from(left);
    const rightBuffer = Buffer.from(right);

    return leftBuffer.length === rightBuffer.length && crypto.timingSafeEqual(leftBuffer, rightBuffer);
}

function postJson(url, payload, headers = {}, timeoutMs = Number(process.env.AUTH_SERVICE_TIMEOUT_MS || 10000)) {
    return new Promise((resolve, reject) => {
        const parsedUrl = new URL(url);
        const client = parsedUrl.protocol === 'https:' ? require('https') : require('http');
        const body = JSON.stringify(payload);

        const request = client.request(
            parsedUrl,
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    'Content-Length': Buffer.byteLength(body),
                    ...headers
                },
                timeout: timeoutMs
            },
            (response) => {
                let responseBody = '';

                response.setEncoding('utf8');
                response.on('data', (chunk) => {
                    responseBody += chunk;
                });
                response.on('end', () => {
                    let data = responseBody;
                    try {
                        data = responseBody ? JSON.parse(responseBody) : null;
                    } catch {
                        data = responseBody;
                    }

                    if (response.statusCode < 200 || response.statusCode >= 300) {
                        const error = new Error(data?.message || data?.error || `auth-service respondeu com status ${response.statusCode}`);
                        error.statusCode = response.statusCode;
                        error.response = data;
                        reject(error);
                        return;
                    }

                    resolve(data);
                });
            }
        );

        request.on('timeout', () => {
            request.destroy(new Error('Tempo limite excedido ao chamar auth-service'));
        });
        request.on('error', reject);
        request.write(body);
        request.end();
    });
}

function parseToken(token) {
    if (!token || typeof token !== 'string') {
        throw new Error('Token ausente');
    }

    const parts = token.split('.');
    if (parts.length !== 3) {
        throw new Error('Token invalido');
    }

    const [encodedHeader, encodedPayload, signature] = parts;
    const header = base64UrlDecode(encodedHeader);
    const payload = base64UrlDecode(encodedPayload);
    const secret = getSecret();

    if (secret) {
        const expectedSignature = crypto
            .createHmac('sha256', secret)
            .update(`${encodedHeader}.${encodedPayload}`)
            .digest('base64url');

        if (!safeEqual(signature, expectedSignature)) {
            throw new Error('Assinatura do token invalida');
        }
    } else if (header.alg !== 'HS256') {
        throw new Error('Algoritmo do token nao suportado');
    }

    if (payload.exp && payload.exp < Math.floor(Date.now() / 1000)) {
        throw new Error('Token expirado');
    }

    return payload;
}

function verifyToken(token) {
    return normalizeUserFromClaims(parseToken(token));
}

async function authenticateCredentials(email, password) {
    if (process.env.LOCAL_AUTH_ENABLED === 'true') {
        if (!email || !password) {
            throw new Error('Email e senha sao obrigatorios');
        }

        const now = Math.floor(Date.now() / 1000);
        const user = {
            sub: email,
            email,
            name: email.split('@')[0] || email,
            role: 'ADMIN',
            roles: ['ADMIN'],
            profileId: Number(process.env.LOCAL_AUTH_PROFILE_ID || 1),
            iat: now,
            exp: now + 60 * 60 * 8
        };

        return {
            token: signToken(user),
            user: normalizeUserFromClaims(user, email)
        };
    }

    const response = await postJson(`${AUTH_SERVICE_URL}/api/auth/login`, { email, password });
    const token = response?.token;

    if (!token) {
        throw new Error('auth-service nao retornou token');
    }

    return {
        token,
        user: normalizeUserFromClaims(parseToken(token), email)
    };
}

function normalizeUserFromClaims(claims, fallbackEmail = null) {
    const role = claims.role || claims.authority || claims.roles;
    const roles = Array.isArray(role) ? role : role ? [role] : [];
    const email = claims.email || fallbackEmail || null;
    const name = claims.name || claims.nome || claims.displayName || email || claims.sub;

    return {
        id: claims.sub,
        sub: claims.sub,
        username: email || claims.sub,
        email,
        name,
        role: roles[0] || null,
        roles,
        profileId: claims.profileId || null
    };
}

function getBearerToken(req) {
    const authorization = req.headers.authorization || '';
    const [scheme, token] = authorization.split(' ');

    return scheme === 'Bearer' ? token : null;
}

function authenticateRequest(req, res, next) {
    try {
        req.user = verifyToken(getBearerToken(req));
        next();
    } catch (error) {
        res.status(401).json({ error: error.message });
    }
}

function requireRole(role) {
    return (req, res, next) => {
        if (!req.user || !Array.isArray(req.user.roles) || !req.user.roles.includes(role)) {
            return res.status(403).json({ error: 'Permissao insuficiente' });
        }

        return next();
    };
}

module.exports = {
    authenticateCredentials,
    authenticateRequest,
    requireRole,
    verifyToken
};
