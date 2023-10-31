DROP TABLE IF EXISTS bookings, items, requests, users, comments;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY,
    description  VARCHAR(1024) NOT NULL,
    requestor_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created      DATE          NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1024) NOT NULL,
    available   BOOLEAN       NOT NULL,
    owner_id    BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    request_id  BIGINT REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    booker_id  BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text         VARCHAR(4096) NOT NULL,
    item_id      BIGINT REFERENCES items (id) ON DELETE CASCADE,
    author_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created_date TIMESTAMP WITHOUT TIME ZONE
);