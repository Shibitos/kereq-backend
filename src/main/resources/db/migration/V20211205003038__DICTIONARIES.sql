CREATE TABLE DICTIONARIES (
	DICT_ID BIGINT PRIMARY KEY,
	DICT_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	DICT_AUDIT_MD TIMESTAMP,
	DICT_AUDIT_RD TIMESTAMP,
	DICT_CODE VARCHAR(25) UNIQUE NOT NULL
);

CREATE SEQUENCE SEQ_DICT_ID START 100 INCREMENT 50;

CREATE TABLE DICTIONARY_ITEMS (
    DICT_ITEM_ID BIGINT PRIMARY KEY,
	DICT_ITEM_AUDIT_CD TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	DICT_ITEM_AUDIT_MD TIMESTAMP,
	DICT_ITEM_AUDIT_RD TIMESTAMP,
	DICT_ITEM_DICT_ID BIGINT REFERENCES DICTIONARIES(DICT_ID) NOT NULL,
	DICT_ITEM_CODE VARCHAR(25) UNIQUE NOT NULL,
	DICT_ITEM_VALUE VARCHAR(50) UNIQUE NOT NULL
);

CREATE SEQUENCE SEQ_DICT_ITEM_ID START 100 INCREMENT 50;
