package com.kereq.common.util;

import java.io.IOException;
import java.nio.file.Path;

public interface FileUtil {

    void createDirectories(Path path) throws IOException;

    boolean deleteIfExists(Path path) throws IOException;

    byte[] readAllBytes(Path path) throws IOException;
}
