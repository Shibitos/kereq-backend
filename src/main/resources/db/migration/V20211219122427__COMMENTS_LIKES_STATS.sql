CREATE TABLE COMMENTS_LIKES (
    COLK_ID BIGINT PRIMARY KEY,
	COLK_USER_ID BIGINT REFERENCES USERS(USER_ID) NOT NULL,
	COLK_COMM_ID BIGINT REFERENCES COMMENTS(COMM_ID) NOT NULL,
	COLK_TYPE INT NOT NULL
);

CREATE SEQUENCE SEQ_COLK_ID START 50 INCREMENT 50;

CREATE TABLE COMMENTS_STATISTICS (
    COSTAT_ID BIGINT PRIMARY KEY,
	COSTAT_COMM_ID BIGINT REFERENCES COMMENTS(COMM_ID) NOT NULL,
	COSTAT_LIKES_COUNT INT NOT NULL DEFAULT 0,
	COSTAT_DISLIKES_COUNT INT NOT NULL DEFAULT 0
);

CREATE SEQUENCE SEQ_COSTAT_ID START 50 INCREMENT 50;
