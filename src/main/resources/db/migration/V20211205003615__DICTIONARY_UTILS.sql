CREATE OR REPLACE PROCEDURE CREATE_DICTIONARY(p_code IN VARCHAR)
LANGUAGE SQL
AS $$
    INSERT INTO DICTIONARIES(DICT_ID, DICT_CODE) VALUES (NEXTVAL('SEQ_DICT_ID'), p_code);
$$;

CREATE OR REPLACE PROCEDURE ADD_DICTIONARY_ITEM(p_dict_code IN VARCHAR, p_code IN VARCHAR, p_value IN VARCHAR)
LANGUAGE PLPGSQL
AS $$
DECLARE
    v_dict_id DICTIONARIES.DICT_ID%type;
BEGIN
    SELECT DICT_ID INTO v_dict_id FROM DICTIONARIES WHERE DICT_CODE = p_dict_code;
    INSERT INTO DICTIONARY_ITEMS(DICT_ITEM_ID, DICT_ITEM_DICT_ID, DICT_ITEM_CODE, DICT_ITEM_VALUE)
        VALUES (NEXTVAL('SEQ_DICT_ITEM_ID'), v_dict_id, p_code, p_value);
END
$$;
