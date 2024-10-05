package com.microservice.manage_event;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.microservice.manage_event.service.implementation.ImagesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ImagesServiceImplTest {

    @InjectMocks
    private ImagesServiceImpl imagesService;

    @Mock
    private MultipartFile image;

    @Mock
    private Cloudinary cloudinary; // Mock the Cloudinary instance

    @Mock
    private Uploader uploader; // Mock the Uploader instance

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // When cloudinary's uploader method is called, return the mocked Uploader instance
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void testUploadImage_NullFile() throws IOException {
        // Arrange
        when(image.getOriginalFilename()).thenReturn("test_image.jpg");
        when(image.getBytes()).thenThrow(new IOException("File cannot be read"));

        // Act & Assert
        assertThrows(IOException.class, () -> imagesService.uploadImage(image));
    }

    @Test
    void testConvert_NullFile() {
        // Arrange
        when(image.getOriginalFilename()).thenReturn(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> imagesService.convert(image));
    }
}
