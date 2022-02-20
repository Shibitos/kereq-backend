package com.kereq.main.util;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ImageUtil {

    BufferedImage read(InputStream input) throws IOException;

    boolean write(RenderedImage im, String formatName, File output) throws IOException;

    BufferedImage resize(BufferedImage src, int size);

    BufferedImage resize(BufferedImage src, int width, int height);

    BufferedImage crop(BufferedImage src, int x, int y, int width, int height);
}
