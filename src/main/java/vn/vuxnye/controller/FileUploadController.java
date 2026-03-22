package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.vuxnye.service.FileUploadService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
@Tag(name = "File Upload Controller")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Image", description = "Upload image file and get URL")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileUploadService.uploadImage(file);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("status", HttpStatus.OK.value());
            result.put("message", "Upload success");
            result.put("url", imageUrl);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload-base64")
    @Operation(summary = "Upload Base64 Image", description = "Upload image from base64 string")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadBase64(@RequestBody Map<String, String> request) {
        try {
            String base64String = request.get("image");
            if (base64String == null || base64String.isEmpty()) {
                return ResponseEntity.badRequest().body("Image data is required");
            }

            String imageUrl = fileUploadService.uploadBase64(base64String);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("status", HttpStatus.OK.value());
            result.put("message", "Upload success");
            result.put("url", imageUrl);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}