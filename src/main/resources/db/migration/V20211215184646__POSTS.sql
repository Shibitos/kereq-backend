CREATE TABLE POSTS (
    POST_ID BIGINT PRIMARY KEY,
	POST_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	POST_AUDIT_MD TIMESTAMP,
	POST_AUDIT_RD TIMESTAMP,
	POST_USER_ID BIGINT REFERENCES USERS(USER_ID) NOT NULL,
	POST_CONTENT VARCHAR(1000) NOT NULL
);

CREATE SEQUENCE SEQ_POST_ID START 50 INCREMENT 50;
