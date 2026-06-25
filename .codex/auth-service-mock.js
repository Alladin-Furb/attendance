const http = require('http');
const crypto = require('crypto');

const PORT = Number(process.env.PORT || 8081);
const SECRET = process.env.JWT_SECRET || 'dev-secret-change-me-dev-secret-change-me';

function base64UrlJson(value) {
    return Buffer.from(JSON.stringify(value)).toString('base64url');
}

function createToken(email) {
    const now = Math.floor(Date.now() / 1000);
    const header = { alg: 'HS256', typ: 'JWT' };
    const payload = {
        sub: '1',
        role: 'ROLE_ALUNO',
        profileId: 1,
        email,
        name: email,
        iat: now,
        exp: now + 3600
    };
    const unsigned = `${base64UrlJson(header)}.${base64UrlJson(payload)}`;
    const signature = crypto.createHmac('sha256', SECRET).update(unsigned).digest('base64url');

    return `${unsigned}.${signature}`;
}

function readJson(req) {
    return new Promise((resolve, reject) => {
        let body = '';
        req.on('data', (chunk) => {
            body += chunk;
        });
        req.on('end', () => {
            try {
                resolve(body ? JSON.parse(body) : {});
            } catch (error) {
                reject(error);
            }
        });
        req.on('error', reject);
    });
}

function sendJson(res, status, payload) {
    const body = JSON.stringify(payload);
    res.writeHead(status, {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization',
        'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(body)
    });
    res.end(body);
}

http.createServer(async (req, res) => {
    if (req.method === 'OPTIONS') {
        return sendJson(res, 204, {});
    }

    if (req.method === 'GET' && req.url === '/actuator/health') {
        return sendJson(res, 200, { status: 'UP' });
    }

    if (req.method === 'POST' && req.url === '/api/auth/login') {
        try {
            const { email, password } = await readJson(req);
            if (!email || !password) {
                return sendJson(res, 401, { message: 'Invalid credentials' });
            }

            return sendJson(res, 200, { token: createToken(email) });
        } catch (error) {
            return sendJson(res, 400, { message: error.message });
        }
    }

    return sendJson(res, 404, { message: 'Not found' });
}).listen(PORT, '127.0.0.1', () => {
    console.log(`auth-service mock on http://127.0.0.1:${PORT}`);
});
