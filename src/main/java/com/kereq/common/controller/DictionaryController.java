package com.kereq.common.controller;

import com.kereq.common.entity.DictionaryItemData;
import com.kereq.common.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/{code}")
    public List<String> getDictionaryValues(@PathVariable("code") String code) {
        List<DictionaryItemData> items = dictionaryService.getAllDictionaryValues(code);
        return items.stream().map(DictionaryItemData::getValue).collect(Collectors.toList());
    }
}
