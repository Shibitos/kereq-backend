package com.kereq.main.controller;

import com.kereq.main.dto.FindFriendDTO;
import com.kereq.main.entity.FindFriendData;
import com.kereq.main.entity.UserData;
import com.kereq.main.service.FindFriendService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/find-friend")
public class FindFriendController {

    @Autowired
    private FindFriendService findFriendService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/my")
    public FindFriendDTO getMyAd(@AuthenticationPrincipal UserData user) {
        FindFriendData findFriendData = findFriendService.getFindFriendAdByUserId(user.getId());

        return modelMapper.map(findFriendData, FindFriendDTO.class);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addAd(FindFriendDTO findFriendDTO) {
        FindFriendData findFriendData = modelMapper.map(findFriendDTO, FindFriendData.class);
        findFriendService.createFindFriendAd(findFriendData);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modify")
    public ResponseEntity<Object> modifyAd(FindFriendDTO findFriendDTO, @AuthenticationPrincipal UserData user) {
        FindFriendData findFriendData = modelMapper.map(findFriendDTO, FindFriendData.class);
        findFriendData.setUser(user);
        findFriendService.createFindFriendAd(findFriendData);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/browse")
    public Page<FindFriendDTO> getAdsForUser(Pageable page, @AuthenticationPrincipal UserData user) {
        return findFriendService.getFindFriendAdsForUser(user, page).map(this::convertToDTO);
    }

    private FindFriendDTO convertToDTO(FindFriendData findFriendData) {
        return modelMapper.map(findFriendData, FindFriendDTO.class);
    }
}
