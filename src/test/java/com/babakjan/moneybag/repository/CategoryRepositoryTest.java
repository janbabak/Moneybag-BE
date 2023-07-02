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
class CategoryRepositoryTest {

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
    void findCategoriesAnalytic() {
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

        Category foodCategory = Category.builder()
                .name("Food")
                .records(new ArrayList<>())
                .build();
        categoryRepository.save(foodCategory);

        Category incomeCategory = Category.builder()
                .name("Income")
                .records(new ArrayList<>())
                .build();
        categoryRepository.save(incomeCategory);

        Record salary = Record.builder()
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(incomeCategory)
                .build();
        incomeCategory.addRecord(salary);
        current.addRecord(salary);
        recordRepository.save(salary);

        Record dividend = Record.builder()
                .amount(8.5)
                .label("dividend")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(incomeCategory)
                .build();
        incomeCategory.addRecord(dividend);
        current.addRecord(dividend);
        recordRepository.save(dividend);

        Record mango = Record.builder()
                .amount(-1.5)
                .label("mango")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(foodCategory)
                .build();
        foodCategory.addRecord(mango);
        current.addRecord(mango);
        recordRepository.save(mango);

        Record restaurant = Record.builder()
                .amount(-50.5)
                .label("restaurant")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(savings)
                .category(foodCategory)
                .build();
        foodCategory.addRecord(restaurant);
        savings.addRecord(restaurant);
        recordRepository.save(restaurant);

        Record gift = Record.builder()
                .amount(100.0)
                .label("gift")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(excluded)
                .category(incomeCategory)
                .build();
        incomeCategory.addRecord(gift);
        excluded.addRecord(gift);
        recordRepository.save(gift);

        Calendar from = new GregorianCalendar(2020, Calendar.JANUARY, 1);
        Calendar to = new GregorianCalendar(2023, Calendar.JANUARY, 1);

        // when
        List<CategoryAnalytic> categoriesAnalytic = categoryRepository.findCategoriesAnalytic(
                user.getId(),
                from.getTime(),
                to.getTime()
        );
        categoriesAnalytic.sort(Comparator.comparing(a -> a.getCategory().getName()));

        // then
        assertThat(categoriesAnalytic.get(0).getCategory()).isEqualTo(foodCategory);
        assertThat(categoriesAnalytic.get(0).getAmount()).isEqualTo(52.0);
        assertThat(categoriesAnalytic.get(0).getNumberOfRecords()).isEqualTo(2);

        assertThat(categoriesAnalytic.get(1).getCategory()).isEqualTo(incomeCategory);
        assertThat(categoriesAnalytic.get(1).getAmount()).isEqualTo(9008.5);
        assertThat(categoriesAnalytic.get(1).getNumberOfRecords()).isEqualTo(2);
    }
}