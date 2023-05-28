package ru.demichev.movies.dto.crt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Login cant be null")
    private String login;
    @NotBlank(message = "User NickName cant be null")
    private String nickName;
    @NotBlank(message = "Password cant be null")
    private String password;
    @NotBlank(message = "Email cant be null")
    private String email;
    private String role;
}
