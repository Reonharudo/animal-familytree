CREATE TABLE IF NOT EXISTS owner
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS horse
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255)            NOT NULL,
    description      VARCHAR(4095),
    date_of_birth    DATE                    NOT NULL,
    sex              ENUM ('MALE', 'FEMALE') NOT NULL,
    owner_id         BIGINT,
    female_parent_id BIGINT,
    male_parent_id   BIGINT
);

ALTER TABLE horse
    ADD FOREIGN KEY (female_parent_id)
        REFERENCES horse (id) ON DELETE SET NULL;

ALTER TABLE horse
    ADD FOREIGN KEY (male_parent_id)
        REFERENCES horse (id) ON DELETE SET NULL;