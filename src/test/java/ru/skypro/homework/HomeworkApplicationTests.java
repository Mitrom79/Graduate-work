package ru.skypro.homework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Тестирование контекста приложения")
class HomeworkApplicationTests {

    @Test
    @DisplayName("Контекст Spring должен успешно загружаться")
    void contextLoads() {

        assertTrue(true, "Контекст Spring должен загружаться без ошибок");
    }
}