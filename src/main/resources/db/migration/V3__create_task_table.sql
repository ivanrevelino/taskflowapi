CREATE TABLE tasks_tb
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    project_id  BIGINT,
    assignee_id BIGINT,
    created_at  TIMESTAMP,

    CONSTRAINT fk_task_project
        FOREIGN KEY (project_id) REFERENCES projects_tb (id),

    CONSTRAINT fk_task_assignee
        FOREIGN KEY (assignee_id) REFERENCES users_tb (id)
);

