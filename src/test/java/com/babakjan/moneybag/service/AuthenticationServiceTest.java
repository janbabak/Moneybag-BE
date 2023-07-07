package com.babakjan.moneybag.service;

import com.babakjan.moneybag.authentication.AuthenticationFacadeInterface;
import com.babakjan.moneybag.dto.user.AuthenticationRequest;
import com.babakjan.moneybag.dto.user.AuthenticationResponse;
import com.babakjan.moneybag.dto.user.RegisterRequest;
import com.babakjan.moneybag.dto.user.UserDto;
import com.babakjan.moneybag.entity.Role;
import com.babakjan.moneybag.entity.User;
import com.babakjan.moneybag.error.exception.UserAlreadyExistsException;
import com.babakjan.moneybag.error.exception.UserNotFoundException;
import com.babakjan.moneybag.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private AuthenticationFacadeInterface authenticationFacadeInterface;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void register() throws UserAlreadyExistsException {
        // given
        Long userId = 454L;
        String email = "johndoe123@gmai.com";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJleHAiOjE2ODU4NzQ3MzksImlhdCI6MTY4NTc4ODMzOSwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IkFETUlOIn1dfQ.G4axFV8xRkzcpLulzPXp-bRuRc3lQIs2vp_jkb6LxLw";
        User user = User.builder()
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .currency("EUR")
                .build();
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .password("tajnyheslO")
                .currency("EUR")
                .build();
        UserDto userDto = UserDto.builder()
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .currency("EUR")
                .accountIds(new ArrayList<>())
                .build();
        AuthenticationResponse response = new AuthenticationResponse(jwt, userDto);
        given(userRepository.findByEmail(email))
                .willReturn(Optional.empty());
        given(userRepository.save(user))
                .willReturn(user);
        given(jwtService.generateToken(user))
                .willReturn(jwt);

        // when
        AuthenticationResponse result = authenticationService.register(registerRequest);

        // then
        assertThat(result).isEqualTo(response);
    }

    @Test
    void authenticate() {
        // given
        String email = "john.doe@yahoo.com";
        Long userId = 43335L;
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJleHAiOjE2ODU4NzQ3MzksImlhdCI6MTY4NTc4ODMzOSwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IkFETUlOIn1dfQ.G4axFV8xRkzcpLulzPXp-bRuRc3lQIs2vp_jkb6LxLw";
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, "SECRET");
        User user = User.builder()
                .id(userId)
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .currency("EUR")
                .build();
        UserDto userDto = UserDto.builder()
                .id(userId)
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .currency("EUR")
                .accountIds(new ArrayList<>())
                .build();
        AuthenticationResponse response = new AuthenticationResponse(jwt, userDto);
        given(userRepository.findByEmail(email))
                .willReturn(Optional.of(user));
        given(jwtService.generateToken(user))
                .willReturn(jwt);

        // when
        AuthenticationResponse result = authenticationService.authenticate(authenticationRequest);

        // then
        assertThat(result).isEqualTo(response);
    }

    @Test
    void checkIfRequestingSelf() throws UserNotFoundException {
        // given
        Long userId = 443L;
        String email = "john.doe@yahoo.com";
        User user = User.builder()
                .id(userId)
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .currency("EUR")
                .build();

        Authentication authentication = new Authentication() {
            @Override
            public String getName() { return email; }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() { return null; }

            @Override
            public Object getCredentials() { return null; }

            @Override
            public Object getDetails() { return null; }

            @Override
            public Object getPrincipal() { return null; }

            @Override
            public boolean isAuthenticated() { return false; }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}
        };

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));
        given(authenticationFacadeInterface.getAuthentication())
                .willReturn(authentication);

        // when
        User result = authenticationService.checkIfRequestingSelf(userId);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void checkIfRequestingSelfWillThrow() throws UserNotFoundException {
        // given
        Long userId = 443L;
        String email = "john.doe@yahoo.com";
        User user = User.builder()
                .id(4L)
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .currency("EUR")
                .build();

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> authenticationService.checkIfRequestingSelf(userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access denied.");
    }

    @Test
    void isNotAdminOrSelfRequest() throws UserNotFoundException {
        // given
        Long userId = 443L;
        String email = "john.doe@yahoo.com";
        User user = User.builder()
                .id(userId)
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .currency("EUR")
                .build();

        Authentication authentication = new Authentication() {
            @Override
            public String getName() { return email; }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() { return null; }

            @Override
            public Object getCredentials() { return null; }

            @Override
            public Object getDetails() { return null; }

            @Override
            public Object getPrincipal() { return null; }

            @Override
            public boolean isAuthenticated() { return false; }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}
        };

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));
        given(authenticationFacadeInterface.getAuthentication())
                .willReturn(authentication);
        given(authenticationFacadeInterface.isAdmin())
                .willReturn(false);

        // when
        boolean result = authenticationService.isNotAdminOrSelfRequest(userId);

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    void ifNotAdminOrSelfRequestThrowAccessDenied() {
        // given
        Long userId = 443L;
        String email = "john.doe@yahoo.com";
        User user = User.builder()
                .id(4L)
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .currency("EUR")
                .build();

        Authentication authentication = new Authentication() {
            @Override
            public String getName() {
                return "differentemail@email.cz";
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }
        };

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));
        given(authenticationFacadeInterface.getAuthentication())
                .willReturn(authentication);
        given(authenticationFacadeInterface.isAdmin())
                .willReturn(false);

        // then
        assertThatThrownBy(() -> authenticationService.ifNotAdminOrSelfRequestThrowAccessDenied(userId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Access denied.");

    }

    @Test
    void isAdmin() {
        // given
        boolean isAdmin = true;
        given(authenticationFacadeInterface.isAdmin())
                .willReturn(isAdmin);

        // when
        boolean result = authenticationService.isAdmin();

        // then
        assertThat(result).isEqualTo(isAdmin);
    }

    @Test
    void ifNotAdminThrowAccessDenied() {
        // given
        boolean isAdmin = false;
        given(authenticationFacadeInterface.isAdmin())
                .willReturn(isAdmin);

        // then
        assertThatThrownBy(() -> authenticationService.ifNotAdminThrowAccessDenied())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Admin ROLE required.");
    }
}