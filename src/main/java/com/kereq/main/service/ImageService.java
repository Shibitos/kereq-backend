package com.kereq.main.service;

import com.kereq.common.constant.ParamKey;
import com.kereq.common.error.CommonError;
import com.kereq.common.error.FileSystemError;
import com.kereq.common.service.EnvironmentService;
import com.kereq.common.util.FileUtil;
import com.kereq.main.constant.PhotoSize;
import com.kereq.main.error.PhotoError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.util.ImageCropOptions;
import com.kereq.main.util.ImageResizeOptions;
import com.kereq.main.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ImageService {
    
    private final EnvironmentService environmentService;
    
    private final ImageUtil imageUtil;

    private final FileUtil fileUtil;
    
    @Autowired
    public ImageService(EnvironmentService environmentService, ImageUtil imageUtil, FileUtil fileUtil) {
        this.environmentService = environmentService;
        this.imageUtil = imageUtil;
        this.fileUtil = fileUtil;
    }
    
    public byte[] getImage(String photoId, String photoSize) { //TODO: privileges? security? now link to image must be unauthorized, maybe put logged userId into photoId when returning from api and check it here?
        String directory = getImageDirectory(photoId);
        try {
            return fileUtil.readAllBytes(Paths.get(directory, getImageFilename(photoId, photoSize)));
        } catch (IOException e) {
            throw new ApplicationException(FileSystemError.RESOURCE_NOT_FOUND);
        }
    }

    public void saveImageAndThumbnails(String photoId, //TODO: validation?
                     MultipartFile imageFile,
                     ImageCropOptions imageCropOptions,
                     ImageResizeOptions imageResizeOptions) {
        String directory = getImageDirectory(photoId);
        try {
            BufferedImage bufferedImage = imageUtil.read(imageFile.getInputStream());
            validateImage(bufferedImage);
            bufferedImage = cropResizeImage(bufferedImage, imageCropOptions, imageResizeOptions);

            fileUtil.createDirectories(Paths.get(directory));

            Path mainPath = Paths.get(directory, getImageFilename(photoId, PhotoSize.ORIGINAL));
            saveImage(mainPath, bufferedImage, 0);

            Path thumbnailPath = Paths.get(directory, getImageFilename(photoId, PhotoSize.THUMBNAIL));
            saveImage(thumbnailPath, bufferedImage, environmentService.getParamInteger(ParamKey.THUMBNAIL_SIZE));

            Path thumbnailMiniPath = Paths.get(directory, getImageFilename(photoId, PhotoSize.THUMBNAIL_MINI));
            saveImage(thumbnailMiniPath, bufferedImage, environmentService.getParamInteger(ParamKey.THUMBNAIL_MINI_SIZE));
        } catch (IOException e) {
            log.error("Failed to save image {}", photoId, e);
            removePhotoImages(photoId);
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
    }

    public void removePhotoImages(String photoId) {
        String directory = getImageDirectory(photoId);
        try {
            fileUtil.deleteIfExists(Paths.get(directory, getImageFilename(photoId, PhotoSize.ORIGINAL)));
            fileUtil.deleteIfExists(Paths.get(directory, getImageFilename(photoId, PhotoSize.THUMBNAIL)));
            fileUtil.deleteIfExists(Paths.get(directory, getImageFilename(photoId, PhotoSize.THUMBNAIL_MINI)));
        } catch (IOException e) {
            log.error("Failed to clean up image files {}", photoId, e);
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
    }

    public BufferedImage cropResizeImage(BufferedImage image, ImageCropOptions imageCropOptions,
                                          ImageResizeOptions imageResizeOptions) {
        if (imageCropOptions != null) {
            image = imageUtil.crop(image,
                    imageCropOptions.getPosX(),
                    imageCropOptions.getPosY(),
                    imageCropOptions.getSize(),
                    imageCropOptions.getSize());
        }
        if (imageResizeOptions != null
                && (imageResizeOptions.getMaxWidth() > 0 && imageResizeOptions.getMaxHeight() > 0)) {
            double resizeScale = Math.min((double) imageResizeOptions.getMaxWidth() / image.getWidth(),
                    (double) imageResizeOptions.getMaxHeight() / image.getHeight());
            if (resizeScale < 1) {
                image = imageUtil.resize(image,
                        Math.min((int) (image.getWidth() * resizeScale), imageResizeOptions.getMaxWidth()),
                        Math.min((int) (image.getHeight() * resizeScale), imageResizeOptions.getMaxHeight()));
            }
        }
        return image;
    }

    public String getImageDirectory(String photoId) {
        StringBuilder dir = new StringBuilder(environmentService.getParam(ParamKey.IMG_STORAGE_PATH));
        int maxPos = environmentService.getParamInteger(ParamKey.IMG_DIR_DIV_CHAR_COUNT) * environmentService.getParamInteger(ParamKey.IMG_DIR_DIV_MAX_LEVEL);
        int step = environmentService.getParamInteger(ParamKey.IMG_DIR_DIV_CHAR_COUNT);
        for (int pos = 0; pos < maxPos; pos += step) {
            dir.append(photoId, pos, pos + environmentService.getParamInteger(ParamKey.IMG_DIR_DIV_CHAR_COUNT));
            dir.append(File.separator);
        }
        return dir.toString();
    }

    public String getImageFilename(String photoId, String photoSize) {
        StringBuilder fileName = new StringBuilder(photoId);
        if (PhotoSize.THUMBNAIL.equals(photoSize)) {
            fileName.append(environmentService.getParam(ParamKey.THUMBNAIL_SUFFIX));
        } else if (PhotoSize.THUMBNAIL_MINI.equals(photoSize)) {
            fileName.append(environmentService.getParam(ParamKey.THUMBNAIL_MINI_SUFFIX));
        }
        fileName.append(".").append(environmentService.getParam(ParamKey.IMG_EXTENSION));
        return fileName.toString();
    }

    private void validateImage(BufferedImage image) { //TODO: max size validation?
        if (image.getWidth() < environmentService.getParamInteger(ParamKey.IMG_MIN_SIZE)
                || image.getHeight() < environmentService.getParamInteger(ParamKey.IMG_MIN_SIZE)) {
            throw new ApplicationException(PhotoError.IMAGE_TOO_SMALL, environmentService.getParamInteger(ParamKey.IMG_MIN_SIZE));
        }
    }

    private void saveImage(Path path, BufferedImage image, int size) throws IOException {
        File newImageFile = path.toFile();
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        if (size > 0) {
            convertedImage = imageUtil.resize(convertedImage, size);
        }
        if (!imageUtil.write(convertedImage, environmentService.getParam(ParamKey.IMG_EXTENSION), newImageFile)) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
    }
}
