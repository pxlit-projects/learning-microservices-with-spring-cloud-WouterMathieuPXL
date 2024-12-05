package be.pxl.services.services;

import org.springframework.web.multipart.MultipartFile;

public interface IImageService {

    String storeImage(MultipartFile image);
}
