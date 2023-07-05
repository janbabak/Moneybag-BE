package com.babakjan.moneybag.service;

import com.babakjan.moneybag.dto.user.UpdateUserRequest;
import com.babakjan.moneybag.dto.user.UserDto;
import com.babakjan.moneybag.entity.*;
import com.babakjan.moneybag.error.exception.UserNotFoundException;
import com.babakjan.moneybag.repository.AccountRepository;
import com.babakjan.moneybag.repository.RecordRepository;
import com.babakjan.moneybag.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.withPrecision;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RecordRepository recordRepository;
    @Mock
    private AuthenticationService authenticationService;

    @Test
    void getAll() {
        // when
        userService.getAll();

        // then
        verify(userRepository).findAll();
    }

    @Test
    void getById() throws UserNotFoundException {
        // given
        Long id = 9809809L;
        User user = new User(
                id,
                "John",
                "Doe",
                "john.doe@gmail.com",
                "secret",
                Role.USER,
                new ArrayList<>(),
                "USD"
        );
        given(userRepository.findById(id))
                .willReturn(Optional.of(user));

        // when
        userService.getById(id);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findById(idCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(id);
    }

    @Test
    void getByIdThrowExceptionUserNotExist() {
        // given
        Long id = 9809809L;

        // then
        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User of id: " + id + " not found.");
    }

    @Test
    void deleteById() throws UserNotFoundException {
        // given
        Long id = 9809809L;
        User user = new User(
                id,
                "John",
                "Doe",
                "john.doe@gmail.com",
                "secret",
                Role.USER,
                new ArrayList<>(),
                "USD"
        );
        given(userRepository.findById(id))
                .willReturn(Optional.of(user));

        // when
        userService.deleteById(id);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).deleteById(idCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(id);
    }

    @Test
    void update() throws UserNotFoundException {
        Long id = 9809809L;
        User user = new User(
                id,
                "John",
                "Doe",
                "john.doe@gmail.com",
                "secret",
                Role.USER,
                new ArrayList<>(),
                "USD"
        );
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                id,
                "Jonny",
                null,
                null,
                Role.ADMIN,
                null
        );
        User updatedUser = new User(
                id,
                "Jonny",
                "Doe",
                "john.doe@gmail.com",
                "secret",
                Role.ADMIN,
                new ArrayList<>(),
                "USD"
        );
        given(userRepository.findById(id))
                .willReturn(Optional.of(user));
        given(userRepository.save(updatedUser))
                .willReturn(updatedUser);

        // when
        User result = userService.update(id, updateRequest);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<User> updatedUserCapture = ArgumentCaptor.forClass(User.class);
        verify(userRepository).findById(idCapture.capture());
        verify(userRepository).save(updatedUserCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(id);
        assertThat(updatedUserCapture.getValue()).isEqualTo(updatedUser);
        assertThat(result).isEqualTo(updatedUser);
    }

    @Test
    void getTotalAnalytic() throws UserNotFoundException {
        // given
        Long userId = 1L;
        Date from = new Date();
        Date to = new Date();
        String currency = "CZK";
        Double income = 1253.9;
        Double expenses = -253.9;
        Double totalBalance = 900.02;
        Account account = Account.builder()
                .currency(currency)
                .build();
        given(recordRepository.getTotalIncomes(userId, from, to))
                .willReturn(income);
        given(recordRepository.getTotalExpenses(userId, from, to))
                .willReturn(expenses);
        given(accountRepository.findAll())
                .willReturn(List.of(account));
        given(accountRepository.getTotalBalance(userId))
                .willReturn(totalBalance);

        // when
        TotalAnalytic totalAnalytic = userService.getTotalAnalytic(userId, from, to);
        // then
        assertThat(totalAnalytic.getIncomes()).isEqualTo(income);
        assertThat(totalAnalytic.getExpenses()).isEqualTo(expenses);
        assertThat(totalAnalytic.getBalance()).isEqualTo(totalBalance);
        assertThat(totalAnalytic.getCashFlow()).isEqualTo(1000.0, withPrecision(0.000001));
    }

    @Test
    void getTotalBalance() throws UserNotFoundException {
        // given
        Long userId = 489L;
        Date dateLt = new Date();
        given(accountRepository.getTotalBalance(userId))
                .willReturn(150.5);
        given(recordRepository.getTotalIncomes(eq(userId), eq(dateLt), any()))
                .willReturn(10.0);
        given(recordRepository.getTotalExpenses(eq(userId), eq(dateLt), any()))
                .willReturn(-12.5);

        // when
        Double totalBalance = userService.getTotalBalance(userId, dateLt);

        // then
        assertThat(totalBalance).isEqualTo(153.0);
    }

    @Test
    void getBalanceEvolution() throws UserNotFoundException {
        // given
        Long userId = 1L;
        Calendar from = new GregorianCalendar(2022, Calendar.JANUARY, 3);
        Calendar to = new GregorianCalendar(2022, Calendar.AUGUST, 29);
        List<TimeSeriesEntry> balanceEvolution = new ArrayList<>();
        balanceEvolution.add(
                new TimeSeriesEntry(150.0, (new GregorianCalendar(2022, Calendar.MAY, 8).getTime()))
        );
        balanceEvolution.add(
                new TimeSeriesEntry(10.0, (new GregorianCalendar(2022, Calendar.APRIL, 1).getTime()))
        );
        balanceEvolution.add(
                new TimeSeriesEntry(500.0, (new GregorianCalendar(2022, Calendar.JULY, 1).getTime()))
        );
        balanceEvolution.add(
                new TimeSeriesEntry(200.0, (new GregorianCalendar(2022, Calendar.AUGUST, 1).getTime()))
        );
        given(recordRepository.getSpendingEvolution(userId, from.getTime(), to.getTime()))
                .willReturn(balanceEvolution);
        given(accountRepository.getTotalBalance(userId))
                .willReturn(1000.0);

        // when
        List<TimeSeriesEntry> resultEvolution = userService.getBalanceEvolution(userId, from.getTime(), to.getTime());

        // then
        List<TimeSeriesEntry> referenceEvolution = new ArrayList<>();
        referenceEvolution.add(
                new TimeSeriesEntry(140.0, from.getTime())
        );
        referenceEvolution.add(
                new TimeSeriesEntry(290.0, (new GregorianCalendar(2022, Calendar.MAY, 8).getTime()))
        );
        referenceEvolution.add(
                new TimeSeriesEntry(300.0, (new GregorianCalendar(2022, Calendar.APRIL, 1).getTime()))
        );
        referenceEvolution.add(
                new TimeSeriesEntry(800.0, (new GregorianCalendar(2022, Calendar.JULY, 1).getTime()))
        );
        referenceEvolution.add(
                new TimeSeriesEntry(1000.0, (new GregorianCalendar(2022, Calendar.AUGUST, 1).getTime()))
        );
        referenceEvolution.add(
                new TimeSeriesEntry(1000.0, (new GregorianCalendar(2022, Calendar.AUGUST, 28).getTime()))
        );
        assertThat(resultEvolution).isEqualTo(referenceEvolution);
    }

    @Test
    void usersToDtos() {
        // given
        List<User> users = List.of(
                new User(
                        1L,
                        "John",
                        "Doe",
                        "john.doe@gmail.com",
                        "secret",
                        Role.USER,
                        List.of(
                                Account.builder()
                                        .id(9L)
                                        .name("Current")
                                        .currency("EUR")
                                        .balance(999.44)
                                        .includeInStatistic(true)
                                        .build(),
                                Account.builder()
                                        .id(99L)
                                        .name("Savings")
                                        .currency("EUR")
                                        .balance(1000000.0)
                                        .includeInStatistic(true)
                                        .build()
                        ),
                        "EUR"
                ),
                new User(
                        1L,
                        "Harry",
                        "Potter",
                        "magic@gmail.com",
                        "avadakadabra",
                        Role.ADMIN,
                        new ArrayList<>(),
                        "USD"
                )
        );

        List<UserDto> userDtos = List.of(
                new UserDto(
                        1L,
                        "John",
                        "Doe",
                        "john.doe@gmail.com",
                        Role.USER,
                        "EUR",
                        List.of(9L, 99L)
                ),
                new UserDto(
                        1L,
                        "Harry",
                        "Potter",
                        "magic@gmail.com",
                        Role.ADMIN,
                        "USD",
                        new ArrayList<>()
                )
        );

        // when
        List<UserDto> resultUserDtos = UserService.usersToDtos(users);

        // then
        assertThat(userDtos).isEqualTo(resultUserDtos);
    }
}