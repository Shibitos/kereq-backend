package com.kereq.main.controller;

import com.kereq.common.service.EventPublisherService;
import com.kereq.main.dto.PhotoDTO;
import com.kereq.main.dto.ProfileImageDTO;
import com.kereq.main.dto.UserBiographyDTO;
import com.kereq.main.dto.UserDTO;
import com.kereq.main.entity.UserData;
import com.kereq.main.entity.UserDataInfo;
import com.kereq.main.event.ChangedProfilePictureEvent;
import com.kereq.main.service.PhotoService;
import com.kereq.main.service.UserService;
import com.kereq.main.util.ImageCropOptions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;

    private final PhotoService photoService;

    private final EventPublisherService eventPublisherService;

    private final ModelMapper modelMapper;

    public UserProfileController(UserService userService, PhotoService photoService, EventPublisherService eventPublisherService, ModelMapper modelMapper) {
        this.userService = userService;
        this.photoService = photoService;
        this.eventPublisherService = eventPublisherService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public UserDTO getLoggedUser(@AuthenticationPrincipal UserDataInfo user) {
        return getUser(user.getId());
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable("userId") long userId) {
        UserData requestedUser = userService.getUser(userId);
        return modelMapper.map(requestedUser, UserDTO.class);
    }

    @GetMapping("/{userId}/photos")
    public Page<PhotoDTO> browsePhotos(
            @PageableDefault(sort = {"auditCD"}, direction = Sort.Direction.ASC)
                    Pageable page,
            @AuthenticationPrincipal UserDataInfo user) {
        return photoService.getUserPhotos(user.getId(), page).map(p -> modelMapper.map(p, PhotoDTO.class));
    }

    @PatchMapping
    public UserDTO modifyLoggedUser(@Valid @RequestBody UserDTO userDTO,
                                    @AuthenticationPrincipal UserDataInfo user) {
        UserData modifiedUser = modelMapper.map(userDTO, UserData.class);
        modifiedUser = userService.modifyUser(user.getId(), modifiedUser);
        return modelMapper.map(modifiedUser, UserDTO.class);
    }

    @PostMapping("/biography")
    public UserDTO modifyLoggedUserBiography(@Valid @RequestBody UserBiographyDTO userBiographyDTO,
                                             @AuthenticationPrincipal UserDataInfo user) {
        UserData modifiedUser = userService.modifyUserBiography(user.getId(), userBiographyDTO.getBiography());
        return modelMapper.map(modifiedUser, UserDTO.class);
    }

    @PostMapping("/image")
    public ResponseEntity<Object> uploadProfileImage(@ModelAttribute ProfileImageDTO profileImageDTO, //TODO: validation?
                                                     @AuthenticationPrincipal UserDataInfo user) {
        photoService.addProfilePhoto(user.getId(), profileImageDTO.getFile(),
                new ImageCropOptions(profileImageDTO.getSize(), profileImageDTO.getPosX(), profileImageDTO.getPosY()));
        eventPublisherService.publishEvent(new ChangedProfilePictureEvent(user.getId()));
        return ResponseEntity.ok().build();
    }
}
