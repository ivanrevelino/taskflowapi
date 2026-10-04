CREATE TABLE refresh_tokens_tb
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255),
    expires_at TIMESTAMP,
    user_id    BIGINT,

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES users_tb (id)
);