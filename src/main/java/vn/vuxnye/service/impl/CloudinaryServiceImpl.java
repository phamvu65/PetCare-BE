package vn.vuxnye.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.vuxnye.service.FileUploadService;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CLOUDINARY-SERVICE")
public class CloudinaryServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID().toString();

        // Upload lên Cloudinary
        Map params = ObjectUtils.asMap(
                "public_id", fileName,
                "folder", "pet_care_products" // Tên thư mục trên Cloudinary
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        // Lấy đường dẫn URL an toàn (https)
        String url = uploadResult.get("secure_url").toString();
        log.info("Image uploaded successfully: {}", url);

        return url;
    }

    @Override
    public String uploadFromUrl(String url) throws IOException {
        // Tạo tên file ngẫu nhiên
        String fileName = UUID.randomUUID().toString();

        Map params = ObjectUtils.asMap(
                "public_id", fileName,
                "folder", "pet_care_products"
        );

        // Cloudinary hỗ trợ truyền URL vào hàm upload()
        Map uploadResult = cloudinary.uploader().upload(url, params);

        String secureUrl = uploadResult.get("secure_url").toString();
        log.info("Uploaded from URL successfully: {}", secureUrl);

        return secureUrl;
    }


    @Override
    public String uploadBase64(String base64String) throws IOException {
        String fileName = UUID.randomUUID().toString();

        Map params = ObjectUtils.asMap(
                "public_id", fileName,
                "folder", "pet_care_products",
                "resource_type", "image"
        );

        Map uploadResult = cloudinary.uploader().upload(base64String, params);

        String secureUrl = uploadResult.get("secure_url").toString();
        log.info("Base64 image uploaded successfully: {}", secureUrl);

        return secureUrl;
    }
}