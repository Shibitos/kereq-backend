CREATE TABLE POSTS_LIKES (
    POLK_ID BIGINT PRIMARY KEY,
	POLK_USER_ID BIGINT REFERENCES USERS(USER_ID) NOT NULL,
	POLK_POST_ID BIGINT REFERENCES POSTS(POST_ID) NOT NULL,
	POLK_VERSION BIGINT NOT NULL DEFAULT 0,
	POLK_TYPE INT NOT NULL
);

CREATE SEQUENCE SEQ_POLK_ID START 50 INCREMENT 50;

CREATE TABLE POSTS_STATISTICS (
    POSTAT_ID BIGINT PRIMARY KEY,
	POSTAT_POST_ID BIGINT REFERENCES POSTS(POST_ID) NOT NULL UNIQUE,
	POSTAT_VERSION BIGINT NOT NULL DEFAULT 0,
	POSTAT_LIKES_COUNT INT NOT NULL DEFAULT 0,
	POSTAT_DISLIKES_COUNT INT NOT NULL DEFAULT 0,
	POSTAT_COMMENTS_COUNT INT NOT NULL DEFAULT 0
);

CREATE SEQUENCE SEQ_POSTAT_ID START 50 INCREMENT 50;
