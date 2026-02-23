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
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.dto.request.ProductRequest;
import vn.vuxnye.dto.response.ProductPageResponse;
import vn.vuxnye.dto.response.ProductResponse;
import vn.vuxnye.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Controller")
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-CONTROLLER")
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public ResponseAPI getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "id:asc") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, name = "categoryId") List<Long> categoryIds,
            @RequestParam(required = false, defaultValue = "false") Boolean isDeleted
    ) {
        ProductPageResponse response = productService.findAll(keyword, sort, page, size, categoryIds, isDeleted);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get products success")
                .data(response)
                .build();
    }

    // API KHÔI PHỤC
    @PutMapping("/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Restore deleted product")
    public ResponseAPI restoreProduct(@PathVariable Long id) {
        productService.restore(id);
        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Restore product success")
                .data(null)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product detail")
    public ResponseAPI getProductById(@PathVariable @Min(1) Long id) {
        ProductResponse response = productService.findById(id);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get product detail success")
                .data(response)
                .build();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Create product")
    public ResponseAPI createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);

        return ResponseAPI.builder()
                .status(HttpStatus.CREATED)
                .message("Create product success")
                .data(response)
                .build();
    }

    @PutMapping("/upd/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update product")
    public ResponseAPI updateProduct(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.update(id, request);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Update product success")
                .data(response)
                .build();
    }

    @DeleteMapping("/del/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Delete product")
    public ResponseAPI deleteProduct(@PathVariable @Min(1) Long id) {
        productService.delete(id);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Delete product success")
                .data(null)
                .build();
    }


}