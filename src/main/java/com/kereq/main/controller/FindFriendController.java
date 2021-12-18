package com.kereq.main.controller;

import com.kereq.main.dto.FindFriendDTO;
import com.kereq.main.entity.FindFriendData;
import com.kereq.main.entity.UserData;
import com.kereq.main.service.FindFriendService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/find-friends")
public class FindFriendController {

    @Autowired
    private FindFriendService findFriendService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public FindFriendDTO getMyAd(@AuthenticationPrincipal UserData user) {
        FindFriendData findFriendData = findFriendService.getFindFriendAdByUserId(user.getId());
        return modelMapper.map(findFriendData, FindFriendDTO.class);
    }

    @PostMapping
    public ResponseEntity<Object> addAd(@Valid @RequestBody FindFriendDTO findFriendDTO,
                                        @AuthenticationPrincipal UserData user) {
        FindFriendData findFriendData = modelMapper.map(findFriendDTO, FindFriendData.class);
        findFriendData.setUser(user);
        findFriendService.createFindFriendAd(findFriendData);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Object> modifyAd(@Valid @RequestBody FindFriendDTO findFriendDTO,
                                           @AuthenticationPrincipal UserData user) {
        FindFriendData findFriendData = modelMapper.map(findFriendDTO, FindFriendData.class);
        findFriendService.modifyFindFriendAd(user.getId(), findFriendData);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Object> removeAd(@AuthenticationPrincipal UserData user) {
        findFriendService.removeFindFriendAd(user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/browse")
    public Page<FindFriendDTO> getAdsForUser(@PageableDefault(sort = { "auditMD" }) Pageable page,
                                             @AuthenticationPrincipal UserData user) {
        return findFriendService.getFindFriendAdsForUser(user, page).map(f -> modelMapper.map(f, FindFriendDTO.class));
    }
}
