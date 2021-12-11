package com.kereq.helper;

import com.kereq.common.error.ApplicationError;
import com.kereq.main.exception.ApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AssertHelper {

    public static void assertException(ApplicationError error, Executable func) {
        assertException(error, func, null);
    }

    public static void assertException(ApplicationError error, Executable func, String param) {
        ApplicationException e;
        e = Assertions.assertThrows(ApplicationException.class, func, param);
        assertThat(e.getErrorCode()).isEqualTo(error.name());
    }
}
