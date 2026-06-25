const { Pool } = require('pg');

const connectionString = process.env.DATABASE_URL || process.env.SUPABASE_DATABASE_URL;
const useSsl = String(process.env.DB_SSL || '').toLowerCase() === 'true' || Boolean(connectionString);

const databaseConfig = connectionString
    ? {
        connectionString,
        ssl: useSsl ? { rejectUnauthorized: false } : false
    }
    : {
        user: process.env.DB_USER || 'postgres',
        host: process.env.DB_HOST || 'localhost',
        database: process.env.DB_NAME || 'people_transportation',
        password: process.env.DB_PASSWORD || 'postgres',
        port: Number(process.env.DB_PORT || 5432),
        ssl: useSsl ? { rejectUnauthorized: false } : false
    };

const pool = new Pool(databaseConfig);

pool.on('connect', () => {
    console.log('Conectado ao PostgreSQL');
});

pool.on('error', (err) => {
    console.error('Erro inesperado na conexao com PostgreSQL:', err);
});

async function testConnection() {
    const result = await pool.query('SELECT NOW() AS now');
    return result.rows[0];
}

module.exports = {
    pool,
    testConnection,
    databaseConfig: {
        host: databaseConfig.host || new URL(connectionString || 'postgresql://localhost').hostname,
        port: databaseConfig.port || Number(new URL(connectionString || 'postgresql://localhost:5432').port || 5432),
        database: databaseConfig.database || new URL(connectionString || 'postgresql://localhost/people_transportation').pathname.replace('/', ''),
        ssl: Boolean(databaseConfig.ssl)
    },
};
