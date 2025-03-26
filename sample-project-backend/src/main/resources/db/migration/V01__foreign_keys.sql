ALTER TABLE main.course
    ADD CONSTRAINT "Degree_Foreign_Key" FOREIGN KEY (degree_id)
        REFERENCES main.degree (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE NO ACTION
        NOT VALID;

ALTER TABLE main.course
    ADD CONSTRAINT "Professor_Foreign_Key" FOREIGN KEY (professor_id)
        REFERENCES main.staff (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE NO ACTION
    NOT VALID;

ALTER TABLE main.student
    ADD CONSTRAINT degree_foreign_key FOREIGN KEY (degree_id)
        REFERENCES main.degree (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID;

ALTER TABLE main.enrollment
    ADD CONSTRAINT course_foreign_key FOREIGN KEY (course)
        REFERENCES main.course (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,

    ADD CONSTRAINT student_foreign_key FOREIGN KEY (student)
        REFERENCES main.student (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID;
