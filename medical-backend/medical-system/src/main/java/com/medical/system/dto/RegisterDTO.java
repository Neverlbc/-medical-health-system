package com.medical.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Register request DTO
 */
@Data
public class RegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "username must not be blank")
    @Size(min = 4, max = 20, message = "username length must be 4-20")
    private String username;

    @NotBlank(message = "password must not be blank")
    @Size(min = 6, max = 20, message = "password length must be 6-20")
    private String password;

    @NotBlank(message = "phone must not be blank")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "invalid phone format")
    private String phone;

    private String email;

    @NotBlank(message = "role must not be blank")
    private String role;
}
