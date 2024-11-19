package com.example.demo.DTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class SigninDTO {

    @NotBlank(message = "Email can;t be blank")
    @Email(message = "Invalid email format")
    private String email;
    @Length(min = 5,max = 20,message = "Invalid password length")
    private String password;

}
