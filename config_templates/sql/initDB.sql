DROP TABLE IF EXISTS user_group;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS user_seq;
DROP TYPE IF EXISTS user_flag;
DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS groups;
DROP TYPE IF EXISTS group_type;
DROP TABLE IF EXISTS projects;
DROP SEQUENCE IF EXISTS general_seq;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE TYPE group_type AS ENUM ('REGISTERING', 'CURRENT', 'FINISHED');

CREATE SEQUENCE user_seq START 100000;
CREATE SEQUENCE general_seq START 100;

CREATE TABLE cities (
  id        INTEGER PRIMARY KEY DEFAULT nextval('general_seq'),
  name      TEXT UNIQUE NOT NULL,
  full_name TEXT
);

CREATE TABLE projects (
  id          INTEGER PRIMARY KEY DEFAULT nextval('general_seq'),
  name        TEXT UNIQUE NOT NULL,
  description TEXT
);

CREATE TABLE groups (
  id         INTEGER PRIMARY KEY DEFAULT nextval('general_seq'),
  name       TEXT UNIQUE       NOT NULL,
  group_type group_type        NOT NULL,
  project_id INTEGER           NOT NULL,
  FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT      NOT NULL,
  email     TEXT      NOT NULL,
  flag      user_flag NOT NULL,
  city_name TEXT      NOT NULL,
  FOREIGN KEY (city_name) REFERENCES cities (name) ON DELETE CASCADE
);

CREATE UNIQUE INDEX email_idx ON users (email);

CREATE TABLE user_group (
  user_id  INTEGER NOT NULL,
  group_name TEXT NOT NULL,
  CONSTRAINT user_group_idx UNIQUE (user_id, group_name),
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  FOREIGN KEY (group_name) REFERENCES groups (name) ON DELETE CASCADE
);
