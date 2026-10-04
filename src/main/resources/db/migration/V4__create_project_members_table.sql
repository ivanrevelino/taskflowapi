CREATE TABLE project_members_tb
(
    id            BIGSERIAL PRIMARY KEY,
    project_id    BIGINT,
    user_id       BIGINT,
    invited_by_id BIGINT,
    joined_at     TIMESTAMP,
    role          VARCHAR(50),

    CONSTRAINT fk_member_project
        FOREIGN KEY (project_id) REFERENCES projects_tb (id),

    CONSTRAINT fk_member_user
        FOREIGN KEY (user_id) REFERENCES users_tb (id),

    CONSTRAINT fk_member_invited_by
        FOREIGN KEY (invited_by_id) REFERENCES users_tb (id)
);