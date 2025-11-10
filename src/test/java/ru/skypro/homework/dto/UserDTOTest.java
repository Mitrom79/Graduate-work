package ru.skypro.homework.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserDTOTest {

    @Test
    void userDTO_ShouldHaveCorrectStructure() {

        UserDTO userDTO = new UserDTO(
                1,
                "test@mail.ru",
                "John",
                "Doe",
                "+79991234567",
                Role.USER,
                "/images/1.jpg"
        );


        assertThat(userDTO.getId()).isEqualTo(1);
        assertThat(userDTO.getEmail()).isEqualTo("test@mail.ru");
        assertThat(userDTO.getFirstName()).isEqualTo("John");
        assertThat(userDTO.getLastName()).isEqualTo("Doe");
        assertThat(userDTO.getPhone()).isEqualTo("+79991234567");
        assertThat(userDTO.getRole()).isEqualTo(Role.USER);
        assertThat(userDTO.getImage()).isEqualTo("/images/1.jpg");
    }

    @Test
    void userDTO_NoArgsConstructor_ShouldWork() {

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setEmail("test@mail.ru");


        assertThat(userDTO.getId()).isEqualTo(1);
        assertThat(userDTO.getEmail()).isEqualTo("test@mail.ru");
    }
}