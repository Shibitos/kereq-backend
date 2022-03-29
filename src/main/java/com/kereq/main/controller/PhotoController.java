package com.kereq.main.controller;

import com.kereq.main.constant.PhotoSize;
import com.kereq.main.service.ImageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class PhotoController {

    private final ImageService imageService;

    public PhotoController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/og/{photoId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable("photoId") String photoId) {
        return imageService.getImage(photoId.replace("-", ""), PhotoSize.ORIGINAL);
    }

    @GetMapping(value = "/tb/{photoId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImageThumbnail(@PathVariable("photoId") String photoId) {
        return imageService.getImage(photoId.replace("-", ""), PhotoSize.THUMBNAIL);
    }

    @GetMapping(value = "/tbm/{photoId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImageThumbnailMini(@PathVariable("photoId") String photoId) {
        return imageService.getImage(photoId.replace("-", ""), PhotoSize.THUMBNAIL_MINI);
    }
}
