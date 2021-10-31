CREATE TABLE USERS (
	USER_ID BIGSERIAL PRIMARY KEY,
	USER_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	USER_AUDIT_MD TIMESTAMP,
	USER_AUDIT_RD TIMESTAMP,
	USER_LOGIN VARCHAR(25) UNIQUE (LOWER(USER_LOGIN)) NOT NULL,
	USER_PASSWORD VARCHAR(72) NOT NULL,
	USER_EMAIL VARCHAR(50) UNIQUE (LOWER(USER_EMAIL)) NOT NULL,
	USER_FIRST_NAME VARCHAR(25) NOT NULL,
	USER_LAST_NAME VARCHAR(25) NOT NULL,
	USER_ACTIVATED BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE ROLES (
    ROLE_ID BIGSERIAL PRIMARY KEY,
	ROLE_CODE VARCHAR(20) UNIQUE NOT NULL,
	ROLE_NAME VARCHAR(25) NOT NULL,
	ROLE_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	ROLE_AUDIT_MD TIMESTAMP,
	ROLE_AUDIT_RD TIMESTAMP
);

CREATE TABLE USERS_ROLES (
    UR_USER_ID BIGSERIAL REFERENCES USERS(USER_ID),
    UR_ROLE_ID BIGSERIAL REFERENCES ROLES(ROLE_ID),
    PRIMARY KEY (UR_USER_ID, UR_ROLE_ID)
);

INSERT INTO ROLES (ROLE_CODE, ROLE_NAME)
VALUES
    ('ROLE_USER', 'User'),
    ('ROLE_ADMIN', 'Administrator');
