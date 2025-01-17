CREATE KEYSPACE IF NOT EXISTS your_keyspace
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE your_keyspace;

CREATE TABLE IF NOT EXISTS demo (
    id text PRIMARY KEY,
    name text,
    description text
);