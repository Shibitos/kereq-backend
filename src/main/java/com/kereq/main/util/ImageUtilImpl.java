package com.kereq.main.util;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageUtilImpl implements ImageUtil {

    @Override
    public BufferedImage read(InputStream input) throws IOException {
        return ImageIO.read(input);
    }

    @Override
    public boolean write(RenderedImage im, String formatName, File output) throws IOException {
        return ImageIO.write(im, formatName, output);
    }

    @Override
    public BufferedImage resize(BufferedImage src, int size) {
        return resize(src, size, size);
    }

    @Override
    public BufferedImage resize(BufferedImage src, int width, int height) {
        return Scalr.resize(src, width, height);
    }

    @Override
    public BufferedImage crop(BufferedImage src, int x, int y, int width, int height) {
        return Scalr.crop(src, x, y, width, height);
    }
}
