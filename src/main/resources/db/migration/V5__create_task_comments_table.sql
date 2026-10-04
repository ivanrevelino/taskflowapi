CREATE TABLE task_comments_tb
(
    id         BIGSERIAL PRIMARY KEY,
    content    TEXT   NOT NULL,
    task_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMP,

    CONSTRAINT fk_comment_task
        FOREIGN KEY (task_id) REFERENCES tasks_tb (id),

    CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id) REFERENCES users_tb (id)
);