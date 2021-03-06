CREATE TABLE PHOTOS (
    PHT_ID BIGINT PRIMARY KEY,
	PHT_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PHT_AUDIT_CU BIGINT,
	PHT_AUDIT_MD TIMESTAMP,
	PHT_AUDIT_MU BIGINT,
	PHT_AUDIT_RD TIMESTAMP,
	PHT_VERSION BIGINT NOT NULL DEFAULT 0,
	PHT_USER_ID BIGINT REFERENCES USERS(USER_ID) NOT NULL,
    PHT_TYPE VARCHAR(1) NOT NULL,
    PHT_UUID UUID NOT NULL
);

CREATE SEQUENCE SEQ_PHT_ID START 50 INCREMENT 50;
CREATE UNIQUE INDEX IDX_UQ_PHT_UUID ON PHOTOS(PHT_UUID);
