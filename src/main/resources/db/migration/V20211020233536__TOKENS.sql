CREATE TABLE USER_TOKENS (
	TKN_ID BIGINT PRIMARY KEY,
    TKN_USER_ID BIGINT REFERENCES USERS(USER_ID),
	TKN_EXPIRE_DATE TIMESTAMP NOT NULL,
	TKN_LAST_SEND_DATE TIMESTAMP,
	TKN_VALUE VARCHAR(36) NOT NULL,
	TKN_TYPE VARCHAR(1) NOT NULL,
	UNIQUE(TKN_USER_ID, TKN_TYPE)
);

CREATE SEQUENCE SEQ_TKN_ID START 50 INCREMENT 50;
