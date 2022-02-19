package com.kereq.main.service;

import com.kereq.common.error.RepositoryError;
import com.kereq.main.entity.PhotoData;
import com.kereq.main.entity.UserData;
import com.kereq.main.error.PhotoError;
import com.kereq.main.exception.ApplicationException;
import com.kereq.main.repository.PhotoRepository;
import com.kereq.main.repository.UserRepository;
import com.kereq.main.util.ImageCropOptions;
import com.kereq.main.util.ImageResizeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
public class PhotoService {

    private static final ImageResizeOptions PHOTO_MAX_SIZE = new ImageResizeOptions(2000, 1800);
    private static final ImageResizeOptions PHOTO_PROFILE_MAX_SIZE = new ImageResizeOptions(400, 400); //TODO: think move

    @Autowired
    private ImageService imageService;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    public PhotoData addPhoto(long userId, MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            throw new ApplicationException(PhotoError.NO_FILE);
        }
        UUID photoUUID = UUID.randomUUID();
        String photoId = photoUUID.toString().replace("-", "");
        imageService.saveImageAndThumbnails(photoId, imageFile, null, PHOTO_MAX_SIZE);

        PhotoData photo = new PhotoData();
        photo.setType(PhotoData.PhotoType.PHOTO);
        photo.setUuid(photoUUID);
        photo.setUserId(userId);
        return photoRepository.save(photo);
    }

    @Transactional
    public PhotoData addProfilePhoto(long userId, MultipartFile imageFile, ImageCropOptions imageCropOptions) {
        UserData user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND));
        UUID photoUUID = UUID.randomUUID();
        String photoId = photoUUID.toString().replace("-", "");
        imageService.saveImageAndThumbnails(photoId, imageFile, imageCropOptions, PHOTO_PROFILE_MAX_SIZE);

        PhotoData existing = photoRepository.findByUserIdAndType(user.getId(), PhotoData.PhotoType.PROFILE);
        if (existing != null) {
            existing.setType(PhotoData.PhotoType.PHOTO);
        }
        PhotoData photo = new PhotoData();
        photo.setType(PhotoData.PhotoType.PROFILE);
        photo.setUuid(photoUUID);
        photo.setUserId(user.getId());
        photo = photoRepository.save(photo);
        user.setProfilePhoto(photo);
        return photo;
    }

    public Page<PhotoData> getUserPhotos(long userId, Pageable page) {
        return photoRepository.findByUserId(userId, page);
    }
}
