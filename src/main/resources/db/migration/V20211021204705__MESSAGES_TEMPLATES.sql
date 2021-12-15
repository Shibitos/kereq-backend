CREATE TABLE MESSAGE_TEMPLATES (
    MSG_TMP_ID BIGINT PRIMARY KEY,
    MSG_TMP_CODE VARCHAR(50) UNIQUE NOT NULL,
    MSG_TMP_SUBJECT VARCHAR(100) NOT NULL,
    MSG_TMP_BODY VARCHAR NOT NULL,
    MSG_TMP_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MSG_TMP_AUDIT_MD TIMESTAMP,
    MSG_TMP_AUDIT_RD TIMESTAMP
);

CREATE SEQUENCE SEQ_MSG_TMP_ID START 50 INCREMENT 50;

CREATE TABLE MESSAGES (
    MSG_ID BIGINT PRIMARY KEY,
    MSG_MSG_TMP_ID BIGINT REFERENCES MESSAGE_TEMPLATES(MSG_TMP_ID),
    MSG_SUBJECT VARCHAR(77) NOT NULL,
    MSG_BODY VARCHAR NOT NULL,
    MSG_FROM VARCHAR(50) NOT NULL,
    MSG_TO VARCHAR(50) NOT NULL,
    MSG_STATUS VARCHAR(1) NOT NULL,
    MSG_RETRY_COUNT INTEGER NOT NULL DEFAULT 0,
    MSG_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MSG_AUDIT_MD TIMESTAMP,
    MSG_AUDIT_RD TIMESTAMP
);

CREATE SEQUENCE SEQ_MSG_ID START 50 INCREMENT 50;

INSERT INTO MESSAGE_TEMPLATES (MSG_TMP_ID, MSG_TMP_CODE, MSG_TMP_SUBJECT, MSG_TMP_BODY)
    VALUES (NEXTVAL('SEQ_MSG_TMP_ID'), 'COMPLETE_REGISTRATION', 'Complete registration',
    'To complete registration, click following link: {{CONFIRM_URL}}');
--TODO: sizes of columns