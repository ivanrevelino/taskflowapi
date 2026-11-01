CREATE TABLE project_invites_tb
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT       NOT NULL,
    invited_id BIGINT       NOT NULL,
    inviter_id BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    status     VARCHAR(50)  NOT NULL,
    message    TEXT,

    CONSTRAINT fk_invite_project
        FOREIGN KEY (project_id) REFERENCES projects_tb (id),
    CONSTRAINT fk_invite_invited
        FOREIGN KEY (invited_id) REFERENCES users_tb (id),
    CONSTRAINT fk_invite_inviter
        FOREIGN KEY (inviter_id) REFERENCES users_tb (id)
);
