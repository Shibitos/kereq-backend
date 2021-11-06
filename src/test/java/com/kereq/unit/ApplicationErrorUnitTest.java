package com.kereq.unit;

import com.kereq.main.error.ApplicationError;
import com.kereq.main.error.CommonError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ApplicationErrorUnitTest {

    @Test
    void testApplicationException() {
        ApplicationError error = CommonError.TEST_ERROR;
        assertThat(error.buildMessage()).isEqualTo("Test {0} erro{1}r");
        assertThat(error.buildMessage("abc")).isEqualTo("Test abc erro{1}r");
        assertThat(error.buildMessage("abc", 2)).isEqualTo("Test abc erro2r");
    }
}
