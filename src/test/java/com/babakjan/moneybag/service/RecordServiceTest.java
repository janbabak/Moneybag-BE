package com.babakjan.moneybag.service;

import com.babakjan.moneybag.dto.account.AccountDtoReduced;
import com.babakjan.moneybag.dto.category.CategoryDto;
import com.babakjan.moneybag.dto.record.CreateRecordRequest;
import com.babakjan.moneybag.dto.record.RecordDto;
import com.babakjan.moneybag.dto.record.UpdateRecordRequest;
import com.babakjan.moneybag.entity.Record;
import com.babakjan.moneybag.entity.*;
import com.babakjan.moneybag.error.exception.AccountNotFoundException;
import com.babakjan.moneybag.error.exception.CategoryNotFoundException;
import com.babakjan.moneybag.error.exception.RecordNotFoundException;
import com.babakjan.moneybag.error.exception.UserNotFoundException;
import com.babakjan.moneybag.repository.RecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {
    @InjectMocks
    private RecordService recordService;
    @Mock
    private RecordRepository recordRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private AuthenticationService authenticationService;

    @Test
    void getAll() {
        // when
        recordService.getAll();

        // then
        verify(recordRepository).findAll();
    }

    @Test
    void getAllFilter() throws UserNotFoundException {
        // given
        Specification<Record> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("label"),  "%restaurant%");
        Pageable pageable = PageRequest.of(0, 2);
        Long userId = 3L;

        // when
        recordService.getAllFilter(specification, pageable, userId);

        // then
        verify(recordRepository).findAll(specification, pageable);
    }

    @Test
    void getById() throws UserNotFoundException, RecordNotFoundException {
        // given
        Long recordId = 9239L;

        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Category category = Category.builder()
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();

        Record record = Record.builder()
                .id(recordId)
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(record);
        current.addRecord(record);

        given(recordRepository.findById(recordId))
                .willReturn(Optional.of(record));

        // when
        recordService.getById(recordId);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        verify(recordRepository).findById(idCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(recordId);
    }

    @Test
    void save() throws UserNotFoundException, CategoryNotFoundException, AccountNotFoundException {
        // given
        Long userId = 77L;
        User user = User.builder()
                .id(userId)
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Long currentAccountId = 22L;
        Account current = Account.builder()
                .id(currentAccountId)
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Long categoryId = 44L;
        Category category = Category.builder()
                .id(categoryId)
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();

        Record record = Record.builder()
                .amount(9000.0)
                .label("salary")
                .note("")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(record);
        current.addRecord(record);

        CreateRecordRequest createRequest = new CreateRecordRequest(
                9000.0,
                "salary",
                "",
                (new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime(),
                currentAccountId,
                categoryId
        );

        given(recordRepository.save(any()))
                .willReturn(record);
        given(accountService.getById(currentAccountId))
                .willReturn(current);
        given(categoryService.getById(categoryId))
                .willReturn(category);

        // when
        recordService.save(createRequest);

        // then
        ArgumentCaptor<Record> recordCapture = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository).save(recordCapture.capture());
        assertThat(recordCapture.getValue()).isEqualTo(record);
    }

    @Test
    void update()
            throws UserNotFoundException, RecordNotFoundException, CategoryNotFoundException, AccountNotFoundException {
        // given
        Long recordId = 9239L;

        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Category category = Category.builder()
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();

        Record record = Record.builder()
                .id(recordId)
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(record);
        current.addRecord(record);

        UpdateRecordRequest updateRequest = new UpdateRecordRequest(
                recordId,
                900.0,
                "Salary",
                "",
                (new GregorianCalendar(2022, Calendar.JANUARY, 1)).getTime(),
                current.getId(),
                category.getId()
        );

        Record updatedRecord = Record.builder()
                .id(recordId)
                .amount(900.0)
                .label("Salary")
                .date((new GregorianCalendar(2022, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();

        given(recordRepository.findById(recordId))
                .willReturn(Optional.of(record));
        given(recordRepository.save(updatedRecord))
                .willReturn(updatedRecord);

        // when
        Record result = recordService.update(recordId, updateRequest);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Record> updatedRecordCapture = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository).findById(idCapture.capture());
        verify(recordRepository).save(updatedRecordCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(recordId);
        assertThat(updatedRecordCapture.getValue()).isEqualTo(updatedRecord);
        assertThat(result).isEqualTo(updatedRecord);
    }

    @Test
    void deleteById() throws UserNotFoundException, RecordNotFoundException {
        // given
        Long recordId = 9239L;

        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Category category = Category.builder()
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();

        Record record = Record.builder()
                .id(recordId)
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(record);
        current.addRecord(record);

        given(recordRepository.findById(recordId))
                .willReturn(Optional.of(record));

        // when
        recordService.deleteById(recordId);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        verify(recordRepository).deleteById(idCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(recordId);
    }

    @Test
    void recordsToDto() {
        // given
        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Long currentAccountId = 99L;
        Account currentAccount = Account.builder()
                .id(currentAccountId)
                .name("Current")
                .currency("USD")
                .color("#fff")
                .icon("mdi-pig")
                .balance(1000.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(currentAccount);

        Long categoryId = 98989L;
        Category category = Category.builder()
                .id(categoryId)
                .name("Miscellaneous")
                .icon("icon")
                .color("#fefefe")
                .records(new ArrayList<>())
                .build();

        List<Record> records = List.of(
                Record.builder()
                        .id(1L)
                        .amount(9000.0)
                        .label("salary")
                        .date((new GregorianCalendar(2024, Calendar.JANUARY, 1)).getTime())
                        .account(currentAccount)
                        .category(category)
                        .note("")
                        .build(),
                Record.builder()
                        .id(2L)
                        .amount(-5.90)
                        .label("bike")
                        .date((new GregorianCalendar(2023, Calendar.MAY, 1)).getTime())
                        .account(currentAccount)
                        .category(category)
                        .note("")
                        .build()
        );

        AccountDtoReduced currentAccountDto = new AccountDtoReduced(
                currentAccountId,
                "Current",
                "USD",
                "#fff",
                "mdi-pig"
        );

        CategoryDto categoryDto = new CategoryDto(
                categoryId,
                "Miscellaneous",
                "icon",
                "#fefefe"
        );

        List<RecordDto> recordDtos = List.of(
                new RecordDto(
                        1L,
                        9000.0,
                        "salary",
                        "",
                        (new GregorianCalendar(2024, Calendar.JANUARY, 1)).getTime(),
                        currentAccountDto,
                       categoryDto
                ),
                new RecordDto(
                        2L,
                        -5.90,
                        "bike",
                        "",
                        (new GregorianCalendar(2023, Calendar.MAY, 1)).getTime(),
                        currentAccountDto,
                        categoryDto
                )
        );

        // when
        List<RecordDto> resultRecordDtos = RecordService.recordsToDto(records);

        // then
        assertThat(recordDtos).isEqualTo(resultRecordDtos);
    }
}