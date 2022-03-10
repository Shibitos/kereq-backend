package com.kereq.unit.main;

import com.kereq.common.constant.ParamKey;
import com.kereq.common.error.CommonError;
import com.kereq.common.error.FileSystemError;
import com.kereq.common.service.EnvironmentService;
import com.kereq.common.util.FileUtil;
import com.kereq.helper.AssertHelper;
import com.kereq.main.constant.PhotoSize;
import com.kereq.main.error.PhotoError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.service.ImageService;
import com.kereq.main.util.ImageCropOptions;
import com.kereq.main.util.ImageResizeOptions;
import com.kereq.main.util.ImageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ImageServiceUnitTest {

    private static final String EXAMPLE_PHOTO_ID = "d847f6b8681240739b16f4fc214a4744";
    
    private static final String IMAGE_EXTENSION = "ext";

    private static final int CHAR_DIV_COUNT = 3;

    private static final int DIRECTORY_MAX_LEVEL = 6;

    private static final String IMG_STORAGE_PATH = "@home@path".replace("@", File.separator);

    private static final int IMG_MIN_SIZE = 40;

    private static final int THUMBNAIL_SIZE = 200;

    private static final String THUMBNAIL_SUFFIX = "_tb";

    private static final int THUMBNAIL_MINI_SIZE = 40;

    private static final String THUMBNAIL_MINI_SUFFIX = "_tbm";

    private final EnvironmentService environmentService = Mockito.mock(EnvironmentService.class);

    private final ImageUtil imageUtil = Mockito.mock(ImageUtil.class);

    private final FileUtil fileUtil = Mockito.mock(FileUtil.class);

    private ImageService imageService;

    @BeforeEach
    public void setup() {
        when(environmentService.getParam(ParamKey.IMG_EXTENSION)).thenReturn(IMAGE_EXTENSION);
        when(environmentService.getParamInteger(ParamKey.IMG_DIR_DIV_CHAR_COUNT)).thenReturn(CHAR_DIV_COUNT);
        when(environmentService.getParamInteger(ParamKey.IMG_DIR_DIV_MAX_LEVEL)).thenReturn(DIRECTORY_MAX_LEVEL);
        when(environmentService.getParam(ParamKey.IMG_STORAGE_PATH)).thenReturn(IMG_STORAGE_PATH);
        when(environmentService.getParamInteger(ParamKey.IMG_MIN_SIZE)).thenReturn(IMG_MIN_SIZE);
        when(environmentService.getParamInteger(ParamKey.THUMBNAIL_SIZE)).thenReturn(THUMBNAIL_SIZE);
        when(environmentService.getParam(ParamKey.THUMBNAIL_SUFFIX)).thenReturn(THUMBNAIL_SUFFIX);
        when(environmentService.getParamInteger(ParamKey. THUMBNAIL_MINI_SIZE)).thenReturn(THUMBNAIL_MINI_SIZE);
        when(environmentService.getParam(ParamKey.THUMBNAIL_MINI_SUFFIX)).thenReturn(THUMBNAIL_MINI_SUFFIX);
        imageService = new ImageService(environmentService, imageUtil, fileUtil);
    }

    @Test
    void testGetImage() throws IOException {
        byte[] testBytes = "test".getBytes();
        String directory = imageService.getImageDirectory(EXAMPLE_PHOTO_ID);
        when(fileUtil.readAllBytes(
                Paths.get(directory, imageService.getImageFilename(EXAMPLE_PHOTO_ID, PhotoSize.ORIGINAL)))
        ).thenReturn(testBytes);

        byte[] bytes = imageService.getImage(EXAMPLE_PHOTO_ID, PhotoSize.ORIGINAL);
        assertThat(bytes).isEqualTo(testBytes);

        when(fileUtil.readAllBytes(
                Paths.get(directory, imageService.getImageFilename(EXAMPLE_PHOTO_ID, PhotoSize.ORIGINAL)))
        ).thenThrow(new ApplicationException(FileSystemError.RESOURCE_NOT_FOUND));
        AssertHelper.assertException(FileSystemError.RESOURCE_NOT_FOUND,
                () -> imageService.getImage(EXAMPLE_PHOTO_ID, PhotoSize.ORIGINAL));
    }

    @Test
    void testSaveImageAndThumbnail() throws IOException {
        MultipartFile emptyFile = new MockMultipartFile("test", (byte[]) null);

        when(imageUtil.read(Mockito.any())).thenReturn(new BufferedImage(IMG_MIN_SIZE - 1, IMG_MIN_SIZE + 1, TYPE_INT_RGB));
        AssertHelper.assertException(PhotoError.IMAGE_TOO_SMALL,
                () -> imageService.saveImageAndThumbnails(EXAMPLE_PHOTO_ID, emptyFile, null, null));

        when(imageUtil.read(Mockito.any())).thenReturn(new BufferedImage(IMG_MIN_SIZE + 1, IMG_MIN_SIZE - 1, TYPE_INT_RGB));
        AssertHelper.assertException(PhotoError.IMAGE_TOO_SMALL,
                () -> imageService.saveImageAndThumbnails(EXAMPLE_PHOTO_ID, emptyFile, null, null));

        clearInvocations(imageUtil);
        clearInvocations(fileUtil);
        BufferedImage emptyImage = new BufferedImage(50, 50, TYPE_INT_RGB);
        ImageCropOptions imageCropOptions = new ImageCropOptions(0, 0, 0);
        ImageResizeOptions imageResizeOptions = new ImageResizeOptions(25, 25);
        String directory = imageService.getImageDirectory(EXAMPLE_PHOTO_ID);
        when(imageUtil.read(Mockito.any())).thenReturn(emptyImage);
        when(imageUtil.crop(
                Mockito.any(BufferedImage.class),
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(emptyImage);
        when(imageUtil.resize(
                Mockito.any(BufferedImage.class),
                Mockito.anyInt())).thenReturn(emptyImage);
        when(imageUtil.resize(
                Mockito.any(BufferedImage.class),
                Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(emptyImage);
        when(imageUtil.write(Mockito.any(BufferedImage.class), Mockito.any(), Mockito.any())).thenReturn(true);

        imageService.saveImageAndThumbnails(EXAMPLE_PHOTO_ID, emptyFile, imageCropOptions, imageResizeOptions);
        Mockito.verify(imageUtil, times(1)).read(Mockito.any());
        Mockito.verify(imageUtil, times(1)).crop(emptyImage,
                imageCropOptions.getPosX(),
                imageCropOptions.getPosY(),
                imageCropOptions.getSize(),
                imageCropOptions.getSize());
        Mockito.verify(imageUtil, times(1)).resize(emptyImage, 25, 25);
        Mockito.verify(fileUtil, times(1)).createDirectories(Paths.get(directory));
        Mockito.verify(imageUtil, times(3)).write(Mockito.any(), Mockito.anyString(), Mockito.any());
        Mockito.verify(imageUtil).resize(Mockito.any(BufferedImage.class), Mockito.eq(THUMBNAIL_SIZE));
        Mockito.verify(imageUtil).resize(Mockito.any(BufferedImage.class), Mockito.eq(THUMBNAIL_MINI_SIZE));

        clearInvocations(imageUtil);
        imageService.saveImageAndThumbnails(EXAMPLE_PHOTO_ID, emptyFile, null, null);
        Mockito.verify(imageUtil, never()).crop(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(imageUtil, times(2)).resize(Mockito.any(), Mockito.anyInt());

        clearInvocations(fileUtil);
        when(imageUtil.write(Mockito.any(BufferedImage.class), Mockito.any(), Mockito.any())).thenThrow(new IOException());
        AssertHelper.assertException(CommonError.OTHER_ERROR,
                () -> imageService.saveImageAndThumbnails(EXAMPLE_PHOTO_ID, emptyFile, null, null));
        Mockito.verify(fileUtil, times(3)).deleteIfExists(Mockito.any());
    }

    @Test
    void testRemovePhotoImages() throws IOException {
        String directory = imageService.getImageDirectory(EXAMPLE_PHOTO_ID);
        imageService.removePhotoImages(EXAMPLE_PHOTO_ID);
        Mockito.verify(fileUtil)
                .deleteIfExists(Paths.get(directory, imageService.getImageFilename(EXAMPLE_PHOTO_ID, PhotoSize.ORIGINAL)));
        Mockito.verify(fileUtil)
                .deleteIfExists(Paths.get(directory, imageService.getImageFilename(EXAMPLE_PHOTO_ID, PhotoSize.THUMBNAIL)));
        Mockito.verify(fileUtil)
                .deleteIfExists(Paths.get(directory, imageService.getImageFilename(EXAMPLE_PHOTO_ID, PhotoSize.THUMBNAIL_MINI)));

        when(fileUtil.deleteIfExists(Mockito.any())).thenThrow(new IOException());
        AssertHelper.assertException(CommonError.OTHER_ERROR, () -> imageService.removePhotoImages(EXAMPLE_PHOTO_ID));
    }

    @Test
    void testCropResizeImage() {
        BufferedImage image = new BufferedImage(50, 40, TYPE_INT_RGB);
        ImageCropOptions imageCropOptions = new ImageCropOptions(1, 2, 3);
        ImageResizeOptions imageResizeOptions = new ImageResizeOptions(0, 0);

        imageService.cropResizeImage(image, null, null);
        imageService.cropResizeImage(image, imageCropOptions, null);
        Mockito.verify(imageUtil, times(1)).crop(image, 2, 3, 1, 1);

        imageService.cropResizeImage(image, null, imageResizeOptions);
        Mockito.verify(imageUtil, never())
                .resize(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        imageResizeOptions = new ImageResizeOptions(20, 500);
        imageService.cropResizeImage(image, null, imageResizeOptions);
        Mockito.verify(imageUtil).resize(image, 20, 16);

        imageResizeOptions = new ImageResizeOptions(500, 20);
        imageService.cropResizeImage(image, null, imageResizeOptions);
        Mockito.verify(imageUtil).resize(image, 25, 20);

        clearInvocations(imageUtil);
        imageResizeOptions = new ImageResizeOptions(500, 500);
        imageService.cropResizeImage(image, null, imageResizeOptions);
        Mockito.verify(imageUtil, never())
                .resize(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void testGetImageDirectory() {
        String finalDir = IMG_STORAGE_PATH + "d84@7f6@b86@812@407@39b@".replace("@", File.separator);
        String directory = imageService.getImageDirectory(EXAMPLE_PHOTO_ID);
        assertThat(directory).isEqualTo(finalDir);

        when(environmentService.getParamInteger(ParamKey.IMG_DIR_DIV_CHAR_COUNT)).thenReturn(2);
        when(environmentService.getParamInteger(ParamKey.IMG_DIR_DIV_MAX_LEVEL)).thenReturn(2);

        finalDir = IMG_STORAGE_PATH + "d8@47@".replace("@", File.separator);
        directory = imageService.getImageDirectory(EXAMPLE_PHOTO_ID);
        assertThat(directory).isEqualTo(finalDir);
    }

    @Test
    void testGetImageFilename() {
        String photoId = "test";
        String original = photoId + "." + IMAGE_EXTENSION;
        String thumbnail = photoId + "_tb." + IMAGE_EXTENSION;
        String thumbnailMini = photoId + "_tbm." + IMAGE_EXTENSION;
        assertThat(imageService.getImageFilename(photoId, PhotoSize.ORIGINAL)).isEqualTo(original);
        assertThat(imageService.getImageFilename(photoId, PhotoSize.THUMBNAIL)).isEqualTo(thumbnail);
        assertThat(imageService.getImageFilename(photoId, PhotoSize.THUMBNAIL_MINI)).isEqualTo(thumbnailMini);
    }
}
