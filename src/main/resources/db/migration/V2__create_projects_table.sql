CREATE TABLE projects_tb
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id    BIGINT       NOT NULL,
    created_at  TIMESTAMP,

    CONSTRAINT fk_project_owner
        FOREIGN KEY (owner_id) REFERENCES users_tb (id)
);