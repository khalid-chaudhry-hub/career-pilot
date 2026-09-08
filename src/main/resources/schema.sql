CREATE TABLE IF NOT EXISTS jobs (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    company TEXT NOT NULL,
    location TEXT NOT NULL,
    work_mode TEXT NOT NULL,
    contract_type TEXT NOT NULL,
    description TEXT NOT NULL,
    source TEXT NOT NULL,
    source_url TEXT NOT NULL,
    discovered_at TEXT NOT NULL
);
