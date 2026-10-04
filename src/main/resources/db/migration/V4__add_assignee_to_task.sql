ALTER TABLE tasks_tb
    ADD COLUMN assignee_id BIGINT;

ALTER TABLE tasks_tb
    ADD CONSTRAINT fk_task_assignee
        FOREIGN KEY (assignee_id)
            REFERENCES users_tb (id);