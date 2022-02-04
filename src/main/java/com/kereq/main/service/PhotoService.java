package com.kereq.main.service;

import com.kereq.common.error.CommonError;
import com.kereq.common.error.FileSystemError;
import com.kereq.main.entity.PhotoData;
import com.kereq.main.error.PhotoError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.PhotoRepository;
import com.kereq.main.util.ImageCropOptions;
import com.kereq.main.util.ImageResizeOptions;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PhotoService {

    private static final String IMAGE_EXTENSION = "jpg";
    private static final int CHAR_DIV_COUNT = 4;
    private static final int DIRECTORY_MAX_LEVEL = 4; //TODO: params, validation
    private static final String IMG_STORAGE_PATH = "D:/tmp/img/";
    private static final int PHOTO_MIN_SIZE = 400;

    private static final int THUMBNAIL_SIZE = 200;
    private static final String THUMBNAIL_SUFFIX = "_tb";

    private static final int THUMBNAIL_MINI_SIZE = 40;
    private static final String THUMBNAIL_MINI_SUFFIX = "_tbm";

    private static final ImageResizeOptions PHOTO_MAX_SIZE = new ImageResizeOptions(2000, 1800);
    private static final ImageResizeOptions PHOTO_PROFILE_MAX_SIZE = new ImageResizeOptions(400, 400); //TODO: think move

    public interface PhotoSize {
        String ORIGINAL = "O";
        String THUMBNAIL = "TB";
        String THUMBNAIL_MINI = "TBM";
    }

    @Autowired
    private PhotoRepository photoRepository;

    public PhotoData addPhoto(long userId, MultipartFile imageFile) {
        UUID photoUUID = saveImageAndThumbnails(imageFile, null, PHOTO_MAX_SIZE);

        PhotoData photo = new PhotoData();
        photo.setType(PhotoData.PhotoType.PHOTO);
        photo.setUuid(photoUUID);
        photo.setUserId(userId);
        return photoRepository.save(photo);
    }

    @Transactional
    public PhotoData addProfilePhoto(long userId, MultipartFile imageFile, ImageCropOptions imageCropOptions) {
        UUID photoUUID = saveImageAndThumbnails(imageFile, imageCropOptions, PHOTO_PROFILE_MAX_SIZE);

        PhotoData existing = photoRepository.findByUserIdAndType(userId, PhotoData.PhotoType.PROFILE);
        if (existing != null) {
            existing.setType(PhotoData.PhotoType.PHOTO);
            photoRepository.save(existing);
        }
        PhotoData photo = new PhotoData();
        photo.setType(PhotoData.PhotoType.PROFILE);
        photo.setUuid(photoUUID);
        photo.setUserId(userId);
        return photoRepository.save(photo);
    }

    public Page<PhotoData> getUserPhotos(long userId, Pageable page) {
        return photoRepository.findByUserId(userId, page);
    }

    public byte[] getPhotoImage(String photoId, String photoSize) { //TODO: privileges? security? now link to image must be unauthorized, maybe put logged userId into photoId when returning from api and check it here?
        String directory = getDirectory(photoId);
        StringBuilder fileName = new StringBuilder(photoId);
        if (PhotoSize.THUMBNAIL.equals(photoSize)) {
            fileName.append(THUMBNAIL_SUFFIX);
        } else if (PhotoSize.THUMBNAIL_MINI.equals(photoSize)) {
            fileName.append(THUMBNAIL_MINI_SUFFIX);
        }
        fileName.append("." + IMAGE_EXTENSION);
        try {
            return Files.readAllBytes(Paths.get(directory, fileName.toString()));
        } catch (IOException e) {
            throw new ApplicationException(FileSystemError.RESOURCE_NOT_FOUND);
        }
    }

    private UUID saveImageAndThumbnails(MultipartFile imageFile,
                                        ImageCropOptions imageCropOptions,
                                        ImageResizeOptions imageResizeOptions) { //TODO: compression? limit user uploaded photos?
        if (imageFile.isEmpty()) {
            throw new ApplicationException(PhotoError.NO_FILE);
        }
        UUID photoUUID = UUID.randomUUID();
        String photoId = photoUUID.toString().replace("-", "");
        String directory = getDirectory(photoId);
        try {
            Files.createDirectories(Paths.get(directory));
            BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());
            validateImage(bufferedImage);
            bufferedImage = prepareImage(bufferedImage, imageCropOptions, imageResizeOptions);

            Path mainPath = Paths.get(directory, photoId + "." + IMAGE_EXTENSION);
            saveCompressed(mainPath, bufferedImage);

            Path thumbnailPath = Paths.get(directory, photoId + THUMBNAIL_SUFFIX + "." + IMAGE_EXTENSION);
            saveThumbnail(thumbnailPath, bufferedImage, THUMBNAIL_SIZE);

            Path thumbnailMiniPath = Paths.get(directory, photoId + THUMBNAIL_MINI_SUFFIX + "." + IMAGE_EXTENSION);
            saveThumbnail(thumbnailMiniPath, bufferedImage, THUMBNAIL_MINI_SIZE);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(Paths.get(directory, photoId + "." + IMAGE_EXTENSION));
                Files.deleteIfExists(Paths.get(directory, photoId + THUMBNAIL_SUFFIX + "." + IMAGE_EXTENSION));
                Files.deleteIfExists(Paths.get(directory, photoId + THUMBNAIL_MINI_SUFFIX + "." + IMAGE_EXTENSION));
            } catch (IOException ex) {
                throw new ApplicationException(CommonError.OTHER_ERROR);
            }
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
        return photoUUID;
    }

    private void validateImage(BufferedImage image) { //TODO: max size validation?
        if (image.getWidth() < PHOTO_MIN_SIZE || image.getHeight() < PHOTO_MIN_SIZE) {
            throw new ApplicationException(PhotoError.IMAGE_TOO_SMALL, PHOTO_MIN_SIZE);
        }
//        if (image.getWidth() > PHOTO_MAX_SIZE || image.getHeight() > PHOTO_MAX_SIZE) {
//            throw new ApplicationException(PhotoError.IMAGE_TOO_SMALL, PHOTO_MIN_SIZE);
//        }
    }

    private BufferedImage prepareImage(BufferedImage image, ImageCropOptions imageCropOptions,
                              ImageResizeOptions imageResizeOptions) {
        if (imageCropOptions != null) {
            image = Scalr.crop(image,
                    imageCropOptions.getPosX(),
                    imageCropOptions.getPosY(),
                    imageCropOptions.getSize(),
                    imageCropOptions.getSize());
        }
        if (imageResizeOptions != null
                && (imageResizeOptions.getMaxWidth() > 0 || imageResizeOptions.getMaxHeight() > 0)) {
            double resizeScale = Math.min((double) imageResizeOptions.getMaxWidth() / image.getWidth(),
                    (double) imageResizeOptions.getMaxHeight() / image.getHeight());
            if (resizeScale < 1) {
                image = Scalr.resize(image,
                        Math.min((int) (image.getWidth() * resizeScale), imageResizeOptions.getMaxWidth()),
                        Math.min((int) (image.getHeight() * resizeScale), imageResizeOptions.getMaxHeight()));
            }
        }
        return image;
    }

    private void saveCompressed(Path path, BufferedImage image) throws IOException {
        File newImageFile = path.toFile();
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        if (!ImageIO.write(convertedImage, IMAGE_EXTENSION, newImageFile)) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
    }

    private void saveThumbnail(Path path, BufferedImage image, int size) throws IOException {
        File newImageFile = path.toFile();
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
        convertedImage = Scalr.resize(convertedImage, size);
        if (!ImageIO.write(convertedImage, IMAGE_EXTENSION, newImageFile)) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
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
