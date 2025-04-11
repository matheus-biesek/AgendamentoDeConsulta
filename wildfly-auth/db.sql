/*
CREATE TYPE role AS ENUM ('ADMIN', 'PATIENT', 'DOCTOR', 'NURSE', 'TECHNICIAN');
CREATE TYPE sex AS ENUM ('MALE', 'FEMALE');
*/
/*
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    role role NOT NULL,
    sex sex NOT NULL
);
*/
/*
ALTER TYPE role RENAME TO userrole;
*/
/*
ALTER TYPE sex RENAME TO sexrole;
*/
/*
DROP TABLE IF EXISTS users CASCADE;
*/
/*
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    role userrole NOT NULL,
    sex sexrole NOT NULL
);
*/