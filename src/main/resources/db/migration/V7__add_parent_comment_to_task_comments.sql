ALTER TABLE task_comments_tb
    ADD COLUMN parent_comment_id BIGINT;

ALTER TABLE task_comments_tb
    ADD CONSTRAINT fk_comment_parent
        FOREIGN KEY (parent_comment_id)
            REFERENCES task_comments_tb (id);