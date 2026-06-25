const http = require('http');
const fs = require('fs');
const path = require('path');

const port = Number(process.env.FRONTEND_PORT || 3000);
const frontendDir = path.join(__dirname, 'frontend');

const contentTypes = {
    '.html': 'text/html; charset=utf-8',
    '.css': 'text/css; charset=utf-8',
    '.js': 'application/javascript; charset=utf-8'
};

const server = http.createServer((req, res) => {
    const requestedPath = decodeURIComponent(new URL(req.url, `http://localhost:${port}`).pathname);
    const normalizedPath = requestedPath === '/' ? '/index.html' : requestedPath;
    const filePath = path.normalize(path.join(frontendDir, normalizedPath));

    if (!filePath.startsWith(frontendDir)) {
        res.writeHead(403);
        res.end('Forbidden');
        return;
    }

    fs.readFile(filePath, (error, content) => {
        if (error) {
            res.writeHead(error.code === 'ENOENT' ? 404 : 500);
            res.end(error.code === 'ENOENT' ? 'Not found' : 'Server error');
            return;
        }

        res.writeHead(200, {
            'Content-Type': contentTypes[path.extname(filePath)] || 'application/octet-stream'
        });
        res.end(content);
    });
});

server.listen(port, () => {
    console.log(`Frontend: http://localhost:${port}/index.html`);
    console.log(`Monitoramento: http://localhost:${port}/monitoramento.html`);
});
