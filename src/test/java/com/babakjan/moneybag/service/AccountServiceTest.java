package com.babakjan.moneybag.service;

import com.babakjan.moneybag.dto.account.AccountDto;
import com.babakjan.moneybag.dto.account.CreateAccountRequest;
import com.babakjan.moneybag.dto.account.UpdateAccountRequest;
import com.babakjan.moneybag.entity.Account;
import com.babakjan.moneybag.entity.Category;
import com.babakjan.moneybag.entity.Record;
import com.babakjan.moneybag.entity.User;
import com.babakjan.moneybag.error.exception.AccountNotFoundException;
import com.babakjan.moneybag.error.exception.UserNotFoundException;
import com.babakjan.moneybag.repository.AccountRepository;
import com.babakjan.moneybag.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @InjectMocks
    private AccountService accountService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AuthenticationService authenticationService;

    @Test
    void getAll() {
        // when
        accountService.getAll();

        // then
        verify(accountRepository).findAll();
    }

    @Test
    void getById() throws UserNotFoundException {
        // given
        Long userId = 435980L;
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build();

        Account frankAccount = Account.builder()
                .id(1L)
                .name("Frank account")
                .currency("CHF")
                .balance(5434.98)
                .color("#333388")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(frankAccount);

        Account poundAccount = Account.builder()
                .id(2L)
                .name("Pound account")
                .currency("GBP")
                .balance(4325.0)
                .color("#fff")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(poundAccount);

        Category category = Category.builder()
                .id(8L)
                .name("Miscellaneous")
                .icon("icon")
                .color("#fefefe")
                .records(new ArrayList<>())
                .build();

        Record salary = Record.builder()
                .id(1L)
                .amount(9000.0)
                .label("salary")
                .date(new Date())
                .account(frankAccount)
                .category(category)
                .note("")
                .build();
        frankAccount.addRecord(salary);

        Record bike = Record.builder()
                .id(2L)
                .amount(-500.90)
                .label("bike")
                .date(new Date())
                .account(frankAccount)
                .category(category)
                .note("")
                .build();
        frankAccount.addRecord(bike);

        AccountDto frankAccountDto = AccountDto.builder()
                .id(1L)
                .name("Frank account")
                .currency("CHF")
                .balance(5434.98)
                .color("#333388")
                .icon("mdi-money")
                .recordIds(List.of(1L, 2L))
                .includeInStatistic(true)
                .userId(userId)
                .incomes(9000.0)
                .expenses(-500.90)
                .build();
        AccountDto poundAccountDto = AccountDto.builder()
                .id(2L)
                .name("Pound account")
                .currency("GBP")
                .balance(4325.0)
                .color("#fff")
                .icon("mdi-money")
                .recordIds(new ArrayList<>())
                .includeInStatistic(true)
                .userId(userId)
                .incomes(0.0)
                .expenses(0.0)
                .build();

        List<AccountDto> accountDtos = List.of(frankAccountDto, poundAccountDto);

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        // when
        List<AccountDto> result = accountService.getByAllByUserIdWithThisMontIncomesAndExpenses(userId);

        // then
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(userId);
        assertThat(result).isEqualTo(accountDtos);
    }

    @Test
    void getByAllByUserIdWithThisMontIncomesAndExpenses() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build();

        Long accountId = 21L;
        Account account = Account.builder()
                .id(accountId)
                .name("Savings")
                .records(new ArrayList<>())
                .currency("USD")
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(account);
    }

    @Test
    void save() throws UserNotFoundException {
        // given
        Long userId = 332L;
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .currency("CHF")
                .accounts(new ArrayList<>())
                .build();

        Account account = Account.builder()
                .name("Savings")
                .currency("CHF")
                .balance(134.0)
                .color("#333388")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(account);
        CreateAccountRequest createRequest = new CreateAccountRequest(
                "Savings",
                "CHF",
                134.0,
                "#333388",
                "mdi-money",
                true,
                userId
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));
        given(accountRepository.save(account))
                .willReturn(account);

        // when
        Account result = accountService.save(createRequest);

        // then
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue()).isEqualTo(account);
        assertThat(result).isEqualTo(account);
    }

    @Test
    void update() throws UserNotFoundException, AccountNotFoundException {
        // given
        Long userId = 332L;
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .currency("CHF")
                .accounts(new ArrayList<>())
                .build();

        Long accountId = 21L;
        Account account = Account.builder()
                .name("Savings")
                .currency("CHF")
                .balance(134.0)
                .color("#333388")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(account);
        UpdateAccountRequest updateRequest = new UpdateAccountRequest(
                accountId,
                "savings",
                134.9,
                "#333388",
                "mdi-money",
                false,
                userId
        );
        Account updatedAccount = Account.builder()
                .name("savings")
                .currency("CHF")
                .balance(134.9)
                .color("#333388")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(false)
                .user(user)
                .build();

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));
        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));
        given(accountRepository.save(updatedAccount))
                .willReturn(updatedAccount);

        // when
        Account result = accountService.update(accountId, updateRequest);

        // then
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).findById(idCaptor.capture());
        verify(accountRepository).save(accountCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(accountId);
        assertThat(accountCaptor.getValue()).isEqualTo(account);
        assertThat(result).isEqualTo(updatedAccount);
    }

    @Test
    void deleteById() throws UserNotFoundException, AccountNotFoundException {
        // given
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build();

        Long accountId = 21L;
        Account account = Account.builder()
                .id(accountId)
                .name("Savings")
                .records(new ArrayList<>())
                .currency("USD")
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(account);

        given(accountRepository.findById(accountId))
                .willReturn(Optional.of(account));

        // when
        accountService.deleteById(accountId);

        // then
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(accountRepository).deleteById(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(accountId);
    }

    @Test
    void accountsToDtos() {
        // given
        Long userId = 332L;
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .currency("CHF")
                .accounts(new ArrayList<>())
                .build();

        Account frankAccount = Account.builder()
                .id(1L)
                .name("Frank account")
                .currency("CHF")
                .balance(5434.98)
                .color("#333388")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(frankAccount);

        Account poundAccount = Account.builder()
                .id(2L)
                .name("Pound account")
                .currency("GBP")
                .balance(4325.0)
                .color("#fff")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(poundAccount);

        Category category = Category.builder()
                .id(8L)
                .name("Miscellaneous")
                .icon("icon")
                .color("#fefefe")
                .records(new ArrayList<>())
                .build();

        Record salary = Record.builder()
                .id(1L)
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2024, Calendar.JANUARY, 1)).getTime())
                .account(frankAccount)
                .category(category)
                .note("")
                .build();
        frankAccount.addRecord(salary);

        Record bike = Record.builder()
                .id(2L)
                .amount(-500.90)
                .label("bike")
                .date((new GregorianCalendar(2023, Calendar.MAY, 1)).getTime())
                .account(frankAccount)
                .category(category)
                .note("")
                .build();
        frankAccount.addRecord(bike);

        AccountDto frankAccountDto = AccountDto.builder()
                .id(1L)
                .name("Frank account")
                .currency("CHF")
                .balance(5434.98)
                .color("#333388")
                .icon("mdi-money")
                .recordIds(List.of(1L, 2L))
                .includeInStatistic(true)
                .userId(userId)
                .build();
        AccountDto poundAccountDto = AccountDto.builder()
                .id(2L)
                .name("Pound account")
                .currency("GBP")
                .balance(4325.0)
                .color("#fff")
                .icon("mdi-money")
                .recordIds(new ArrayList<>())
                .includeInStatistic(true)
                .userId(userId)
                .build();

        List<Account> accounts = List.of(frankAccount, poundAccount);
        List<AccountDto> accountDtos = List.of(frankAccountDto, poundAccountDto);

        // when
        List<AccountDto> result = AccountService.accountsToDtos(accounts);

        // then
        assertThat(result).isEqualTo(accountDtos);
    }
}