CREATE TABLE USER_TOKENS (
	TKN_ID BIGSERIAL PRIMARY KEY,
    TKN_USER_ID BIGSERIAL REFERENCES USERS(USER_ID),
	TKN_EXPIRE_DATE TIMESTAMP NOT NULL,
	TKN_VALUE VARCHAR(36) NOT NULL,
	TKN_TYPE VARCHAR(1) NOT NULL,
	UNIQUE(TKN_USER_ID, TKN_TYPE)
);
