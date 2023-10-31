package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerFullContextTest {
    private final UserController userController;

    private void checkUserDto(UserDto userDto, UserDto userDtoFromController) {
        assertEquals(userDto.getId(), userDtoFromController.getId());
        assertEquals(userDto.getName(), userDtoFromController.getName());
        assertEquals(userDto.getEmail(), userDtoFromController.getEmail());
    }

    @Nested
    class Add {
        @Test
        public void shouldAdd() {
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .name("Test user")
                    .email("t-r@ya.ru")
                    .build();
            userController.createUser(userDto);

            List<UserDto> usersFromController = new ArrayList<>(userController.getAll());

            assertEquals(usersFromController.size(), 1);

            UserDto userFromController = usersFromController.get(0);

            checkUserDto(userDto, userFromController);
        }

        @Test
        public void shouldThrowExceptionIfExistedEmail() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("t-r@ya.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("t-r@ya.ru")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> userController.createUser(userDto2));
            assertEquals(userController.getAll().size(), 1);

            UserDto userFromController = userController.getAll().get(0);

            checkUserDto(userDto1, userFromController);
        }
    }

    @Nested
    class GetAll {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("t-r1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("t-r2@ya.ru")
                    .build();
            userController.createUser(userDto2);

            List<UserDto> usersFromController = userController.getAll();

            assertEquals(usersFromController.size(), 2);

            UserDto userFromController1 = usersFromController.get(0);
            UserDto userFromController2 = usersFromController.get(1);

            checkUserDto(userDto1, userFromController1);
            checkUserDto(userDto2, userFromController2);
        }

        @Test
        public void shouldGetIfEmpty() {
            List<UserDto> usersFromController = userController.getAll();

            assertTrue(usersFromController.isEmpty());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGet() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("t-r1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto usersFromController = userController.getById(1L);

            checkUserDto(userDto1, usersFromController);
        }

        @Test
        public void shouldThrowExceptionIfUserIdNotFound() {
            NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.getById(10L));
            assertEquals("Пользователя с таким id не существует.", exception.getMessage());
            assertTrue(userController.getAll().isEmpty());
        }
    }

    @Nested
    class Update {
        @Test
        public void shouldUpdate() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("t-r1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Update test user 1")
                    .email("t-r2@ya.ru")
                    .build();
            userController.updateUser(userDto1.getId(), userDto2);

            List<UserDto> usersFromController = userController.getAll();

            assertEquals(usersFromController.size(), 1);

            UserDto userFromController = usersFromController.get(0);

            assertEquals(userFromController.getId(), userDto1.getId());
            assertEquals(userFromController.getName(), userDto2.getName());
            assertEquals(userFromController.getEmail(), userDto2.getEmail());
        }

        @Test
        public void shouldThrowExceptionIfExistedEmail() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("t-r1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("t-r2@ya.ru")
                    .build();
            userController.createUser(userDto2);

            UserDto userDto3 = UserDto.builder()
                    .id(3L)
                    .name("Patch test user 1")
                    .email("t-r2@ya.ru")
                    .build();

            assertThrows(DataIntegrityViolationException.class, () -> userController.updateUser(userDto1.getId(), userDto3));

            List<UserDto> usersFromController = userController.getAll();

            assertEquals(usersFromController.size(), 2);

            UserDto userFromController1 = usersFromController.get(0);
            UserDto userFromController2 = usersFromController.get(1);

            checkUserDto(userDto1, userFromController1);
            checkUserDto(userDto2, userFromController2);
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDelete() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("t-r1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            List<UserDto> usersFromController = userController.getAll();

            assertEquals(usersFromController.size(), 1);

            UserDto userFromController = usersFromController.get(0);

            checkUserDto(userDto1, userFromController);

            userController.deleteUser(userDto1.getId());

            assertTrue(userController.getAll().isEmpty());
        }

        @Test
        public void shouldDeleteIfUserIdNotFound() {
            assertThrows(EmptyResultDataAccessException.class, () -> userController.deleteUser(10L));

            assertTrue(userController.getAll().isEmpty());
        }
    }
}