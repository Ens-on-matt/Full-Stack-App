-- SCHEMA: main

-- DROP SCHEMA IF EXISTS main ;

CREATE SCHEMA IF NOT EXISTS main
    AUTHORIZATION postgres;

-- ACTIVATE EXTENSION
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Table: main.course

-- DROP TABLE IF EXISTS main.course;

CREATE TABLE IF NOT EXISTS main.course
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY
    (START 1 INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1),
    professor_id integer NOT NULL,
    name text COLLATE pg_catalog."default" NOT NULL,
    degree_id integer NOT NULL,
    CONSTRAINT "Course_pkey" PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS main.course
    OWNER to postgres;


-- Table: main.degree

-- DROP TABLE IF EXISTS main.degree;

CREATE TABLE IF NOT EXISTS main.degree
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY
    (START 1 INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1),
    name text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "Degree_pkey" PRIMARY KEY (id),
    CONSTRAINT "Course Name Unique" UNIQUE (name)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS main.degree
    OWNER to postgres;

GRANT ALL ON TABLE main.degree TO postgres;

-- Table: main.staff

-- DROP TABLE IF EXISTS main.staff;

CREATE TABLE IF NOT EXISTS main.staff
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY
    (START 1 INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1),
    name text COLLATE pg_catalog."default" NOT NULL,
    email text COLLATE pg_catalog."default" NOT NULL,
    phone_number text COLLATE pg_catalog."default" NOT NULL,
    salary integer NOT NULL,
    job text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT staff_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS main.staff
    OWNER to postgres;

-- Table: main.student

-- DROP TABLE IF EXISTS main.student;

CREATE TABLE IF NOT EXISTS main.student
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY
    (START 1 INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1),
    name text COLLATE pg_catalog."default" NOT NULL,
    email text COLLATE pg_catalog."default",
    phone_number text COLLATE pg_catalog."default" NOT NULL,
    degree_id integer NOT NULL,
    CONSTRAINT student_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS main.student
    OWNER to postgres;

-- Table: main.enrollment

-- DROP TABLE IF EXISTS main.enrollment;

CREATE TABLE IF NOT EXISTS main.enrollment
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    student integer NOT NULL,
    course integer NOT NULL,
    status text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT course_enrollments_pkey PRIMARY KEY (id)
    )

TABLESPACE pg_default;

ALTER TABLE IF EXISTS main.enrollment
    OWNER to postgres;