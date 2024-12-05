package be.pxl.services.services.implementation;

import be.pxl.services.services.IImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService implements IImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    @Value("${image.storage.directory}")
    private String storageDirectory;

    @Override
    public String storeImage(MultipartFile image) {

        try {
            Path dirPath = Paths.get(storageDirectory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = image.getOriginalFilename();
            assert fileName != null;
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = System.currentTimeMillis() + extension;
            Path filePath = dirPath.resolve(newFileName);

            Files.copy(image.getInputStream(), filePath);
            log.info("Image saved successfully to {}", filePath.toString());

            return newFileName;
        } catch (IOException ex) {
            log.error("Error while saving the image", ex);
            throw new RuntimeException("Error while saving the image", ex);
        }
    }
}
