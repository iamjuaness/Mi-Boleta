package com.microservice.manage_event.service.implementation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.microservice.manage_event.service.interfaces.ImagesService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ImagesServiceImpl implements ImagesService {

    final Cloudinary cloudinary;

    public ImagesServiceImpl() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dsnq0pvey");
        config.put("api_key", "426898348794871");
        config.put("api_secret", "gm3jfG_tgMY0FLrt_6oXvkNIluM");
        cloudinary = new Cloudinary(config);
    }

    @Override
    public Map uploadImage(MultipartFile image) throws IOException {
        // Obtener el nombre original del archivo
        String originalFilename = image.getOriginalFilename();

        // Renombrar el archivo para eliminar la extensión y otros caracteres problemáticos
        String newFilename = originalFilename.substring(0, originalFilename.lastIndexOf('.')).replace(".", "_").replace(" ", "_");

        File file = convert(image);
        if (file == null) {
            throw new NullPointerException("File cannot be null");
        }

        // Usa el nuevo nombre al subir (sin la extensión)
        return cloudinary.uploader().upload(file, ObjectUtils.asMap("folder", "mi-boleta", "public_id", newFilename));
    }

    @Override
    public Map deleteImage(String idImage) throws IOException {
        return cloudinary.uploader().destroy(idImage, ObjectUtils.emptyMap());
    }

    @Override
    public File convert(MultipartFile image) throws IOException {
        File file = File.createTempFile(Objects.requireNonNull(image.getOriginalFilename()), null);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(image.getBytes());
            return file;
        } catch (NullPointerException e) {
            return null;
        }
    }
}