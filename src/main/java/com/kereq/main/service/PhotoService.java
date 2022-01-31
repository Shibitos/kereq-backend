package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.main.entity.PhotoData;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.PhotoRepository;
import com.kereq.main.util.ImageCropOptions;
import com.kereq.main.util.ImageResizeOptions;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PhotoService {

    private static final int CHAR_DIV_COUNT = 4;
    private static final int DIRECTORY_MAX_LEVEL = 4; //TODO: params, validation
    private static final String IMG_STORAGE_PATH = "D:/tmp/img/";

    private static final int THUMBNAIL_SIZE = 200;
    private static final String THUMBNAIL_SUFFIX = "tb";

    private static final int THUMBNAIL_MINI_SIZE = 40;
    private static final String THUMBNAIL_MINI_SUFFIX = "tbm";

    private static final ImageResizeOptions PHOTO_MAX_SIZE = new ImageResizeOptions(2000, 1800);
    private static final ImageResizeOptions PHOTO_PROFILE_MAX_SIZE = new ImageResizeOptions(400, 400); //TODO: think move

    @Autowired
    private PhotoRepository photoRepository;

    public PhotoData addPhoto(Long userId, MultipartFile imageFile) {
        UUID photoUUID = saveImageAndThumbnails(imageFile, null, PHOTO_MAX_SIZE);

        PhotoData photo = new PhotoData();
        photo.setType(PhotoData.PhotoType.PHOTO);
        photo.setUuid(photoUUID);
        photo.setUserId(userId);
        return photoRepository.save(photo);
    }

    public PhotoData addProfilePhoto(Long userId, MultipartFile imageFile, ImageCropOptions imageCropOptions) {
        UUID photoUUID = saveImageAndThumbnails(imageFile, imageCropOptions, PHOTO_PROFILE_MAX_SIZE);

        PhotoData photo = new PhotoData();
        photo.setType(PhotoData.PhotoType.PROFILE);
        photo.setUuid(photoUUID);
        photo.setUserId(userId);
        return photoRepository.save(photo);
    }

    private UUID saveImageAndThumbnails(MultipartFile imageFile,
                                        ImageCropOptions imageCropOptions,
                                        ImageResizeOptions imageResizeOptions) { //TODO: compression? what if one fails? limit user uploaded photos?
        if (imageFile.isEmpty()) {
            throw new ApplicationException(CommonError.OTHER_ERROR); //TODO: think
        }
        UUID photoUUID = UUID.randomUUID();
        String photoId = photoUUID.toString().replace("-", "");
        String directory = prepareDirectory(photoId);
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
            if (imageCropOptions != null) {
                bufferedImage = Scalr.crop(bufferedImage,
                        imageCropOptions.getPosX(),
                        imageCropOptions.getPosY(),
                        imageCropOptions.getSize(),
                        imageCropOptions.getSize());
            }
            if (imageResizeOptions != null
                    && (imageResizeOptions.getMaxWidth() > 0 || imageResizeOptions.getMaxHeight() > 0)) {
                double resizeScale = Math.min((double) imageResizeOptions.getMaxWidth() / bufferedImage.getWidth(),
                        (double) imageResizeOptions.getMaxHeight() / bufferedImage.getHeight());
                if (resizeScale < 1) {
                    bufferedImage = Scalr.resize(bufferedImage,
                            Math.min((int) (bufferedImage.getWidth() * resizeScale), imageResizeOptions.getMaxWidth()),
                            Math.min((int) (bufferedImage.getHeight() * resizeScale), imageResizeOptions.getMaxHeight()));
                }
            }
            Path mainPath = Paths.get(directory, photoId + ".jpg");
            saveCompressed(mainPath, bufferedImage);
            Path thumbnailPath = Paths.get(directory, photoId + "_" + THUMBNAIL_SUFFIX + ".jpg");
            saveThumbnail(thumbnailPath, bufferedImage, THUMBNAIL_SIZE);
            Path thumbnailMiniPath = Paths.get(directory, photoId + "_" + THUMBNAIL_MINI_SUFFIX + ".jpg");
            saveThumbnail(thumbnailMiniPath, bufferedImage, THUMBNAIL_MINI_SIZE);
        } catch (IOException e) {
            //TODO: cleanup
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
        return photoUUID;
    }

    private void saveCompressed(Path path, BufferedImage image) throws IOException {
        File newImageFile = path.toFile();
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        if (!ImageIO.write(convertedImage, "jpg", newImageFile)) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
    }

    private void saveThumbnail(Path path, BufferedImage image, int size) throws IOException {
        File newImageFile = path.toFile();
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        convertedImage = Scalr.resize(convertedImage, size);
        if (!ImageIO.write(convertedImage, "jpg", newImageFile)) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
    }

    private String prepareDirectory(String id) { //TODO: Files.createDirectories?
        File dir = new File(getDirectory(id));
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new ApplicationException(CommonError.OTHER_ERROR);
            }
        }
        return dir.getAbsolutePath();
    }

    private String getDirectory(String id) {
        StringBuilder dir = new StringBuilder(IMG_STORAGE_PATH);
        for (int pos = 0; pos < CHAR_DIV_COUNT * DIRECTORY_MAX_LEVEL; pos += CHAR_DIV_COUNT) {
            dir.append(id, pos, pos + CHAR_DIV_COUNT);
            dir.append("/");
        }
        return dir.toString();
    }
}
