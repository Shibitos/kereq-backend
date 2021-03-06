CREATE TABLE FIND_FRIEND_ADS (
    FFA_ID BIGINT PRIMARY KEY,
	FFA_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	FFA_AUDIT_CU BIGINT,
	FFA_AUDIT_MD TIMESTAMP,
	FFA_AUDIT_MU BIGINT,
	FFA_AUDIT_RD TIMESTAMP,
	FFA_VERSION BIGINT NOT NULL DEFAULT 0,
	FFA_USER_ID BIGINT REFERENCES USERS(USER_ID) NOT NULL,
	FFA_MIN_AGE INTEGER CHECK(FFA_MIN_AGE > 0),
	FFA_MAX_AGE INTEGER CHECK(FFA_MAX_AGE >= FFA_MIN_AGE),
	FFA_GENDER VARCHAR(1),
	FFA_DESCRIPTION VARCHAR(400)
);

CREATE SEQUENCE SEQ_FFA_ID START 50 INCREMENT 50;
