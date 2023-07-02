package com.babakjan.moneybag.repository;

import com.babakjan.moneybag.entity.Account;
import com.babakjan.moneybag.entity.Role;
import com.babakjan.moneybag.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getTotalBalanceZeroAccountsTest() {
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

        // when
        Double totalBalance = accountRepository.getTotalBalance(user.getId());

        // then
        assertThat(totalBalance).isEqualTo(0.0);
    }

    @Test
    void getTotalBalanceOneAccountTest() {
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
                .balance(1000.99)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(savings);
        accountRepository.save(savings);

        // when
        Double totalBalance = accountRepository.getTotalBalance(user.getId());

        // then
        assertThat(totalBalance).isEqualTo(1000.99);
    }

    @Test
    void getTotalBalanceTwoIncludedAccountsOneExcludedTest() {
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
                .balance(1000.99)
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
                .balance(100.1)
                .includeInStatistic(false)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(excluded);
        accountRepository.save(excluded);

        // when
        Double totalBalance = accountRepository.getTotalBalance(user.getId());

        // then
        assertThat(totalBalance).isEqualTo(1050.99);
    }

    @Test
    void getTotalBalanceNotExistingUserTest() {
        // when
        Double totalBalance = accountRepository.getTotalBalance(123L);

        // then
        assertThat(totalBalance).isEqualTo(0.0);
    }
}