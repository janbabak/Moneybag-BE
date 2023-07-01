package com.babakjan.moneybag.repository;

import com.babakjan.moneybag.entity.*;
import com.babakjan.moneybag.entity.Record;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class RecordRepositoryTest {

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        recordRepository.deleteAll();
        accountRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getTotalIncomesWhenUserHasNoAccounts() {
        // given
        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();
        userRepository.save(user);

        Calendar from = new GregorianCalendar(2020, Calendar.JANUARY, 1);

        // when
        Double totalIncomes = recordRepository.getTotalIncomes(user.getId(), from.getTime(), new Date());

        // then
        assertThat(totalIncomes).isEqualTo(0.0);
    }

    @Test
    void getTotalIncomesTwoAccountsIncludedFromStatisticsOneExcluded() {
        // given
        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();
        userRepository.save(user);

        Account savings = Account.builder()
                .name("Savings")
                .currency("USD")
                .balance(1000.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(savings);
        accountRepository.save(savings);

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(50.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);
        accountRepository.save(current);

        Account excluded = Account.builder()
                .name("Excluded")
                .currency("USD")
                .balance(100.0)
                .includeInStatistic(false)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(excluded);
        accountRepository.save(excluded);

        Category category = Category.builder()
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();
        categoryRepository.save(category);

        Record salary = Record.builder()
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(salary);
        current.addRecord(salary);
        recordRepository.save(salary);

        Record dividend = Record.builder()
                .amount(8.5)
                .label("dividend")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(dividend);
        current.addRecord(dividend);
        recordRepository.save(dividend);

        Record mango = Record.builder()
                .amount(-1.5)
                .label("mango")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(mango);
        current.addRecord(mango);
        recordRepository.save(mango);

        Record car = Record.builder()
                .amount(-5000.5)
                .label("car")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(savings)
                .category(category)
                .build();
        category.addRecord(car);
        savings.addRecord(car);
        recordRepository.save(car);

        Record gift = Record.builder()
                .amount(100.0)
                .label("gift")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(excluded)
                .category(category)
                .build();
        category.addRecord(gift);
        excluded.addRecord(gift);
        recordRepository.save(gift);

        Calendar from = new GregorianCalendar(2020, Calendar.JANUARY, 1);
        Calendar to = new GregorianCalendar(2023, Calendar.JANUARY, 1);

        // when
        Double totalIncomes = recordRepository.getTotalIncomes(user.getId(), from.getTime(), to.getTime());

        // then
        assertThat(totalIncomes).isEqualTo(9008.5);
    }

    @Test
    void getTotalExpensesWhenUserHasNoAccounts() {
        // given
        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        userRepository.save(user);

        Calendar from = new GregorianCalendar(2020, Calendar.JANUARY, 1);

        // then
        assertThat(recordRepository.getTotalExpenses(user.getId(), from.getTime(), new Date())).isEqualTo(0.0);
    }

    @Test
    void getTotalExpensesTwoAccountsIncludedFromStatisticsOneExcluded() {
        // given
        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();
        userRepository.save(user);

        Account savings = Account.builder()
                .name("Savings")
                .currency("USD")
                .balance(1000.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(savings);
        accountRepository.save(savings);

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(50.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);
        accountRepository.save(current);

        Account excluded = Account.builder()
                .name("Excluded")
                .currency("USD")
                .balance(100.0)
                .includeInStatistic(false)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(excluded);
        accountRepository.save(excluded);

        Category category = Category.builder()
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();
        categoryRepository.save(category);

        Record salary = Record.builder()
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(salary);
        current.addRecord(salary);
        recordRepository.save(salary);

        Record dividend = Record.builder()
                .amount(8.5)
                .label("dividend")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(dividend);
        current.addRecord(dividend);
        recordRepository.save(dividend);

        Record mango = Record.builder()
                .amount(-1.5)
                .label("mango")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(mango);
        current.addRecord(mango);
        recordRepository.save(mango);

        Record car = Record.builder()
                .amount(-5000.5)
                .label("car")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(savings)
                .category(category)
                .build();
        category.addRecord(car);
        savings.addRecord(car);
        recordRepository.save(car);

        Record gift = Record.builder()
                .amount(-100.0)
                .label("gift")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(excluded)
                .category(category)
                .build();
        category.addRecord(gift);
        excluded.addRecord(gift);
        recordRepository.save(gift);

        Calendar from = new GregorianCalendar(2020, Calendar.JANUARY, 1);
        Calendar to = new GregorianCalendar(2023, Calendar.JANUARY, 1);

        // when
        Double totalExpenses = recordRepository.getTotalExpenses(user.getId(), from.getTime(), to.getTime());

        // then
        assertThat(totalExpenses).isEqualTo(-5002.0);
    }
}