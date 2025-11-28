package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.dto.request.ProductRequest;
import vn.vuxnye.dto.response.ProductPageResponse;
import vn.vuxnye.dto.response.ProductResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.service.ProductService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Controller")
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-CONTROLLER")
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public Map<String, Object> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "id:asc") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, name = "categoryId") List<Long> categoryIds,
            // 🟢 Nhận thêm param từ FE: true = xem thùng rác, false/null = xem hàng bán
            @RequestParam(required = false, defaultValue = "false") Boolean isDeleted
    ) {
        // Truyền isDeleted xuống Service
        ProductPageResponse response = productService.findAll(keyword, sort, page, size, categoryIds, isDeleted);
        return createResponse(HttpStatus.OK, "Get products success", response);
    }

    // 🟢 API KHÔI PHỤC
    @PutMapping("/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Restore deleted product")
    public Map<String, Object> restoreProduct(@PathVariable Long id) {
        productService.restore(id);
        return createResponse(HttpStatus.OK, "Restore product success", null);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get product detail")
    public Map<String, Object> getProductById(@PathVariable @Min(1) Long id) {
        ProductResponse response = productService.findById(id);
        return createResponse(HttpStatus.OK, "Get product detail success", response);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Create product")
    public Map<String, Object> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return createResponse(HttpStatus.CREATED, "Create product success", response);
    }

    @PutMapping("/upd/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update product")
    public Map<String, Object> updateProduct(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.update(id, request);
        return createResponse(HttpStatus.OK, "Update product success", response);
    }

    @DeleteMapping("/del/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Delete product")
    public Map<String, Object> deleteProduct(@PathVariable @Min(1) Long id) {
        productService.delete(id);
        return createResponse(HttpStatus.NO_CONTENT, "Delete product success", null);
    }

    private Map<String, Object> createResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ResourceNotFoundException ex) {
        return createResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }
}