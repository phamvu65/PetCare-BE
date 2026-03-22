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
import vn.vuxnye.dto.CategoryDTO;
import vn.vuxnye.dto.response.CategoryPageResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.service.CategoryService;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category Controller")
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-CONTROLLER")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "Get all categories")
    public ResponseAPI getAllCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "id:asc") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        CategoryPageResponse pageResponse = categoryService.findAll(keyword, sort, page, size);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get categories success")
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category detail")
    public ResponseAPI getCategoryById(@PathVariable @Min(1) Long id) {
        CategoryDTO response = categoryService.findById(id);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get category detail success")
                .data(response)
                .build();
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Create category")
    public ResponseAPI createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO response = categoryService.create(categoryDTO);

        return ResponseAPI.builder()
                .status(HttpStatus.CREATED)
                .message("Create category success")
                .data(response)
                .build();
    }

    @PutMapping("/upd/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update category")
    public ResponseAPI updateCategory(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO response = categoryService.update(id, categoryDTO);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Update category success")
                .data(response)
                .build();
    }

    @DeleteMapping("/del/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Delete category")
    public ResponseAPI deleteCategory(@PathVariable @Min(1) Long id) {
        categoryService.delete(id);

        return ResponseAPI.builder()
                .status(HttpStatus.OK) // Đổi từ NO_CONTENT sang OK để bảo toàn JSON body
                .message("Delete category success")
                .data(null)
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseAPI handleNotFound(ResourceNotFoundException ex) {
        return ResponseAPI.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .data(null)
                .build();
    }
}