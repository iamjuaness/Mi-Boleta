package com.microservice.manage_event.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ImagesService {
    Map uploadImage(MultipartFile image) throws IOException;
    Map deleteImage(String idImage) throws IOException;

    File convert(MultipartFile image) throws IOException;
}
