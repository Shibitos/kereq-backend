package com.kereq.unit;

import com.kereq.common.error.ApplicationError;
import com.kereq.common.error.CommonError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ApplicationErrorUnitTest {

    @Test
    void testApplicationError() {
        ApplicationError error = CommonError.TEST_ERROR;
        assertThat(error.buildMessage()).isEqualTo("Test {0} err{1}o{1}r");
        assertThat(error.buildMessage("abc")).isEqualTo("Test abc err{1}o{1}r");
        assertThat(error.buildMessage("abc", 2)).isEqualTo("Test abc err2o2r");
    }
}
