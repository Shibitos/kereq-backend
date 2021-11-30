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

    private static final int ADS_PER_PAGE = 6; //TODO: move?

    @Autowired
    private FindFriendService findFriendService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/ad")
    public FindFriendDTO getMyAd(@AuthenticationPrincipal UserData user) {
        FindFriendData findFriendData = findFriendService.getFindFriendAdByUserId(user.getId());
        return modelMapper.map(findFriendData, FindFriendDTO.class);
    }

    @PostMapping("/ad")
    public ResponseEntity<Object> addAd(@Valid @RequestBody FindFriendDTO findFriendDTO,
                                        @AuthenticationPrincipal UserData user) {
        FindFriendData findFriendData = modelMapper.map(findFriendDTO, FindFriendData.class);
        findFriendData.setUser(user);
        findFriendService.createFindFriendAd(findFriendData);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ad")
    public ResponseEntity<Object> modifyAd(@Valid @RequestBody FindFriendDTO findFriendDTO,
                                           @AuthenticationPrincipal UserData user) {
        FindFriendData findFriendData = modelMapper.map(findFriendDTO, FindFriendData.class);
        findFriendData.setUser(user);
        findFriendService.modifyFindFriendAd(findFriendData);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ad")
    public ResponseEntity<Object> removeAd(@AuthenticationPrincipal UserData user) {
        findFriendService.removeFindFriendAd(user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/browse")
    public Page<FindFriendDTO> getAdsForUser(@PageableDefault(sort = { "auditMD" }, value = ADS_PER_PAGE) Pageable page,
                                             @AuthenticationPrincipal UserData user) {
        return findFriendService.getFindFriendAdsForUser(user, page).map(this::convertToDTO);
    }

    private FindFriendDTO convertToDTO(FindFriendData findFriendData) {
        return modelMapper.map(findFriendData, FindFriendDTO.class);
    }
}
