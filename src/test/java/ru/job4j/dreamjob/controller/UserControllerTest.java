package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.FileService;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenGetRegisterPage() {
        var view = userController.getRegistationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenLogoutThenRedirectLogin() {
        var view = userController.logout(new MockHttpSession());
        assertThat(view).isEqualTo("redirect:/users/login");
    }

    @Test
    public void whenGetLoginPageThenReceivedPage() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenRegisterUserThenSuccessfully() {
        var user = new User(1, "a@gmail.com", "artur", "art");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenRegisterUserThenError() {
        when(userService.save(any(User.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(model, new User());
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    public void whenLoginUserThenRedirectVacancies() {
        var user = new User(1, "a@gmail.com", "artur", "art");

        var model = new ConcurrentModel();
        var emailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var passwordArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(emailArgumentCaptor.capture(),
                passwordArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var view = userController.loginUser(user, model, new MockHttpServletRequest());
        var actualEmail = emailArgumentCaptor.getValue();
        var actualPassword = passwordArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualEmail).isEqualTo("a@gmail.com");
        assertThat(actualPassword).isEqualTo("art");
    }

    @Test
    public void whenLoginUserThenError() {
        when(userService.findByEmailAndPassword(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(new User(), model, new MockHttpServletRequest());
        var actualMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualMessage).isEqualTo("Почта или пароль введены неверно");
    }
}