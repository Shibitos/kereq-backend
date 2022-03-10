package com.kereq.unit.common;

import com.kereq.common.entity.DictionaryData;
import com.kereq.common.entity.DictionaryItemData;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.repository.DictionaryItemRepository;
import com.kereq.common.repository.DictionaryRepository;
import com.kereq.common.service.DictionaryService;
import com.kereq.helper.AssertHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class DictionaryServiceUnitTest {

    private final DictionaryRepository dictionaryRepository = Mockito.mock(DictionaryRepository.class);

    private final DictionaryItemRepository dictionaryItemRepository = Mockito.mock(DictionaryItemRepository.class);

    private DictionaryService dictionaryService;

    @BeforeEach
    public void setup() {
        dictionaryService = new DictionaryService(dictionaryRepository, dictionaryItemRepository);
    }

    @Test
    void testGetAllDictionaryItems() {
        when(dictionaryRepository.existsByCode("nonexisting")).thenReturn(false);
        when(dictionaryRepository.existsByCode("existing")).thenReturn(true);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND,
                () -> dictionaryService.getAllDictionaryItems("nonexisting"));
        dictionaryService.getAllDictionaryItems("existing");
    }

    @Test
    void testIsItemInDictionary() {
        when(dictionaryItemRepository.findByCode("nonexisting")).thenReturn(null);
        DictionaryData dictionary = new DictionaryData();
        dictionary.setCode("existing");
        DictionaryItemData item = new DictionaryItemData();
        item.setDictionary(dictionary);
        when(dictionaryItemRepository.findByCode("existing")).thenReturn(item);

        assertThat(dictionaryService.isItemInDictionary(null, "nonexisting")).isFalse();
        assertThat(dictionaryService.isItemInDictionary("nonexisting", "existing")).isFalse();
        assertThat(dictionaryService.isItemInDictionary("existing", "existing")).isTrue();
    }
}
