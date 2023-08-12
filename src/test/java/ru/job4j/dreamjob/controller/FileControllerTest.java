package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;
    private FileController fileController;
    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenFileGetByIdThenGetStatus200() throws IOException {
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.getFileById(idArgumentCaptor.capture())).thenReturn(Optional.of(fileDto));

        var view = fileController.getById(10).getStatusCodeValue();
        var actualId = idArgumentCaptor.getValue();
        assertThat(view).isEqualTo(200);
        assertThat(actualId).isEqualTo(10);
    }

    @Test
    public void whenFileGetByIdThenGetStatus404() {
        var idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.getFileById(idArgumentCaptor.capture())).thenReturn(Optional.empty());

        var view = fileController.getById(10).getStatusCodeValue();
        var actualId = idArgumentCaptor.getValue();
        assertThat(view).isEqualTo(404);
        assertThat(actualId).isEqualTo(10);
    }
}