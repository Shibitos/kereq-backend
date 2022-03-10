package com.kereq.unit.main;

import com.kereq.common.constant.ParamKey;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.service.EnvironmentService;
import com.kereq.helper.AssertHelper;
import com.kereq.main.entity.PhotoData;
import com.kereq.main.entity.UserData;
import com.kereq.main.error.PhotoError;
import com.kereq.main.repository.PhotoRepository;
import com.kereq.main.repository.UserRepository;
import com.kereq.main.service.ImageService;
import com.kereq.main.service.PhotoService;
import com.kereq.main.util.ImageCropOptions;
import com.kereq.main.util.ImageResizeOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class PhotoServiceUnitTest {

    private final int photoMaxWidth = 1800;

    private final int photoMaxHeight = 1600;

    private final int profilePhotoMaxWidth = 600;

    private final int profilePhotoMaxHeight = 500;

    private final ImageService imageService = Mockito.mock(ImageService.class);

    private final PhotoRepository photoRepository = Mockito.mock(PhotoRepository.class);

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private final EnvironmentService environmentService = Mockito.mock(EnvironmentService.class);

    private PhotoService photoService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(environmentService.getParamInteger(ParamKey.PHOTO_MAX_WIDTH)).thenReturn(photoMaxWidth);
        when(environmentService.getParamInteger(ParamKey.PHOTO_MAX_HEIGHT)).thenReturn(photoMaxHeight);
        when(environmentService.getParamInteger(ParamKey.PHOTO_PROFILE_MAX_WIDTH)).thenReturn(profilePhotoMaxWidth);
        when(environmentService.getParamInteger(ParamKey.PHOTO_PROFILE_MAX_HEIGHT)).thenReturn(profilePhotoMaxHeight);
        when(photoRepository.save(Mockito.any(PhotoData.class))).thenAnswer(i -> i.getArguments()[0]);
        photoService = new PhotoService(imageService, photoRepository, userRepository, environmentService);
    }

    @Test
    void testAddPhoto() {
        MultipartFile emptyFile = new MockMultipartFile("test", (byte[]) null);
        MultipartFile notEmptyFile = new MockMultipartFile("test", "test".getBytes());

        AssertHelper.assertException(PhotoError.NO_FILE, () -> photoService.addPhoto(1L, null));
        AssertHelper.assertException(PhotoError.NO_FILE, () -> photoService.addPhoto(1L, emptyFile));

        PhotoData photo = photoService.addPhoto(1L, notEmptyFile);
        assertThat(photo.getType()).isEqualTo(PhotoData.PhotoType.PHOTO);
        assertThat(photo.getUserId()).isEqualTo(1L);
        assertThat(photo.getUuid()).isNotNull();
        ArgumentCaptor<ImageCropOptions> imageCropArgCaptor = ArgumentCaptor.forClass(ImageCropOptions.class);
        ArgumentCaptor<ImageResizeOptions> imageResizeArgCaptor = ArgumentCaptor.forClass(ImageResizeOptions.class);
        Mockito.verify(imageService, times(1))
                .saveImageAndThumbnails(
                        Mockito.anyString(),
                        Mockito.any(MultipartFile.class),
                        imageCropArgCaptor.capture(),
                        imageResizeArgCaptor.capture());
        assertThat(imageCropArgCaptor.getValue()).isNull();
        assertThat(imageResizeArgCaptor.getValue().getMaxWidth()).isEqualTo(photoMaxWidth);
        assertThat(imageResizeArgCaptor.getValue().getMaxHeight()).isEqualTo(photoMaxHeight);
    }

    @Test
    void testAddProfilePhoto() {
        MultipartFile emptyFile = new MockMultipartFile("test", (byte[]) null);
        MultipartFile notEmptyFile = new MockMultipartFile("test", "test".getBytes());
        ImageCropOptions imageCropOptions = new ImageCropOptions(300, 200, 180);
        UserData user = new UserData();
        PhotoData oldPhoto = new PhotoData();
        oldPhoto.setType(PhotoData.PhotoType.PROFILE);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        when(photoRepository.findByUserIdAndType(2L, PhotoData.PhotoType.PROFILE)).thenReturn(null);
        when(photoRepository.findByUserIdAndType(3L, PhotoData.PhotoType.PROFILE)).thenReturn(oldPhoto);

        AssertHelper.assertException(PhotoError.NO_FILE,
                () -> photoService.addProfilePhoto(2L, null, imageCropOptions));
        AssertHelper.assertException(PhotoError.NO_FILE,
                () -> photoService.addProfilePhoto(2L, emptyFile, imageCropOptions));

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> photoService.addProfilePhoto(1L, notEmptyFile, imageCropOptions));

        PhotoData photo = photoService.addProfilePhoto(2L, notEmptyFile, imageCropOptions);
        assertThat(user.getProfilePhoto()).isNotNull();
        assertThat(photo.getType()).isEqualTo(PhotoData.PhotoType.PROFILE);
        assertThat(photo.getUserId()).isEqualTo(2L);
        assertThat(photo.getUuid()).isNotNull();
        ArgumentCaptor<ImageCropOptions> imageCropArgCaptor = ArgumentCaptor.forClass(ImageCropOptions.class);
        ArgumentCaptor<ImageResizeOptions> imageResizeArgCaptor = ArgumentCaptor.forClass(ImageResizeOptions.class);
        Mockito.verify(imageService, times(1))
                .saveImageAndThumbnails(
                        Mockito.anyString(),
                        Mockito.any(MultipartFile.class),
                        imageCropArgCaptor.capture(),
                        imageResizeArgCaptor.capture());
        assertThat(imageCropArgCaptor.getValue()).isEqualTo(imageCropOptions);
        assertThat(imageResizeArgCaptor.getValue().getMaxWidth()).isEqualTo(profilePhotoMaxWidth);
        assertThat(imageResizeArgCaptor.getValue().getMaxHeight()).isEqualTo(profilePhotoMaxHeight);

        photo = photoService.addProfilePhoto(3L, notEmptyFile, imageCropOptions);
        assertThat(oldPhoto.getType()).isEqualTo(PhotoData.PhotoType.PHOTO);
        assertThat(user.getProfilePhoto()).isEqualTo(photo);
        assertThat(photo.getType()).isEqualTo(PhotoData.PhotoType.PROFILE);
        assertThat(photo.getUserId()).isEqualTo(3L);
        assertThat(photo.getUuid()).isNotNull();
    }
}
