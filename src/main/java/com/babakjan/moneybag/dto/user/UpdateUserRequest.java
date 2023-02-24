package com.babakjan.moneybag.dto.user;

import com.babakjan.moneybag.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    private Long id;

    @Size(max = 40)
    private String firstName;

    @Size(max = 40)
    private String lastName;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Size(max = 40)
    private String currency;
}
