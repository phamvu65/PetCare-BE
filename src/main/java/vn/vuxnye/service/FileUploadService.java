package vn.vuxnye.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileUploadService {
    String uploadImage(MultipartFile file) throws IOException;
    String uploadFromUrl(String url) throws IOException;
}