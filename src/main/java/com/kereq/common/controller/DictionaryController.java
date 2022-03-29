package com.kereq.common.controller;

import com.kereq.common.dto.DictionaryItemDTO;
import com.kereq.common.entity.DictionaryItemData;
import com.kereq.common.service.DictionaryService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    private final ModelMapper modelMapper;

    public DictionaryController(DictionaryService dictionaryService, ModelMapper modelMapper) {
        this.dictionaryService = dictionaryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{code}")
    public List<DictionaryItemDTO> getDictionaryValues(@PathVariable("code") String code) {
        List<DictionaryItemData> items = dictionaryService.getAllDictionaryItems(code.toUpperCase());
        return items.stream().map(i -> modelMapper.map(i, DictionaryItemDTO.class)).collect(Collectors.toList());
    }
}
