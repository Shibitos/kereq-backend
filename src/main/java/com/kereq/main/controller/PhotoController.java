package com.kereq.main.controller;

import com.kereq.main.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @GetMapping(value = "/og/{photoId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable("photoId") String photoId) {
        return photoService.getPhoto(photoId, PhotoService.PhotoSize.ORIGINAL);
    }

    @GetMapping(value = "/tb/{photoId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImageThumbnail(@PathVariable("photoId") String photoId) {
        return photoService.getPhoto(photoId, PhotoService.PhotoSize.THUMBNAIL);
    }

    @GetMapping(value = "/tbm/{photoId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImageThumbnailMini(@PathVariable("photoId") String photoId) {
        return photoService.getPhoto(photoId, PhotoService.PhotoSize.THUMBNAIL_MINI);
    }
}
