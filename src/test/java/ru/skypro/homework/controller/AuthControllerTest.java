package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.CustomUserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
@DisplayName("Тестирование AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private Register createValidRegister() {
        Register register = new Register();
        register.setUsername("test@example.com");
        register.setPassword("password123");
        register.setFirstName("John");
        register.setLastName("Doe");
        register.setPhone("+79991234567");
        register.setRole(Role.USER);
        return register;
    }

    private Login createValidLogin() {
        Login login = new Login();
        login.setUsername("test@example.com");
        login.setPassword("password123");
        return login;
    }

    @Nested
    @DisplayName("Тесты эндпоинта /login")
    class LoginTests {

        @Test
        @DisplayName("Успешная авторизация - должен вернуть 200 OK")
        void login_WithValidCredentials_ShouldReturnOk() throws Exception {
            Login login = createValidLogin();

            when(authService.login("test@example.com", "password123")).thenReturn(true);

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isOk());

            verify(authService).login("test@example.com", "password123");
        }

        @Test
        @DisplayName("Неуспешная авторизация - должен вернуть 401 Unauthorized")
        void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
            Login login = createValidLogin();

            when(authService.login("test@example.com", "password123")).thenReturn(false);

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isUnauthorized());

            verify(authService).login("test@example.com", "password123");
        }

        @Test
        @DisplayName("Авторизация с несуществующим пользователем - должен вернуть 401 Unauthorized")
        void login_WithNonExistentUser_ShouldReturnUnauthorized() throws Exception {
            Login login = new Login();
            login.setUsername("nonexistent@example.com");
            login.setPassword("password123");

            when(authService.login("nonexistent@example.com", "password123")).thenReturn(false);

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isUnauthorized());

            verify(authService).login("nonexistent@example.com", "password123");
        }

        @Test
        @DisplayName("Авторизация с некорректным JSON - должен вернуть 400 Bad Request")
        void login_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ invalid json }"))
                    .andExpect(status().isBadRequest());

            verify(authService, never()).login(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Тесты эндпоинта /register")
    class RegisterTests {

        @Test
        @DisplayName("Успешная регистрация - должен вернуть 201 Created")
        void register_WithValidData_ShouldReturnCreated() throws Exception {
            Register register = createValidRegister();

            when(authService.register(any(Register.class))).thenReturn(true);

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(register)))
                    .andExpect(status().isCreated());

            verify(authService).register(any(Register.class));
        }

        @Test
        @DisplayName("Регистрация существующего пользователя - должен вернуть 409 Conflict")
        void register_WithExistingUser_ShouldReturnConflict() throws Exception {
            Register register = createValidRegister();

            when(authService.register(any(Register.class))).thenReturn(false);

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(register)))
                    .andExpect(status().isConflict());

            verify(authService).register(any(Register.class));
        }

        @Test
        @DisplayName("Регистрация с ролью ADMIN - должен вернуть 201 Created")
        void register_WithAdminRole_ShouldReturnCreated() throws Exception {
            Register register = createValidRegister();
            register.setRole(Role.ADMIN);

            when(authService.register(any(Register.class))).thenReturn(true);

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(register)))
                    .andExpect(status().isCreated());

            verify(authService).register(any(Register.class));
        }

        @Test
        @DisplayName("Регистрация с некорректным JSON - должен вернуть 400 Bad Request")
        void register_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ invalid json }"))
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any(Register.class));
        }
    }

    @Nested
    @DisplayName("Тесты CORS")
    class CorsTests {

        @Test
        @DisplayName("CORS запрос с разрешенного origin - должен быть разрешен")
        void corsRequest_FromAllowedOrigin_ShouldBeAllowed() throws Exception {
            Login login = createValidLogin();

            when(authService.login("test@example.com", "password123")).thenReturn(true);

            mockMvc.perform(post("/login")
                            .header("Origin", "http://localhost:3000")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(login)))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
        }
    }
}