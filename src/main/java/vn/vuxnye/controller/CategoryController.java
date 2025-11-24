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
import vn.vuxnye.dto.CategoryDTO; // Import DTO chung
import vn.vuxnye.dto.response.CategoryPageResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.service.CategoryService;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public Map<String, Object> getAllCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "id:asc") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        CategoryPageResponse pageResponse = categoryService.findAll(keyword, sort, page, size);
        return createResponse(HttpStatus.OK, "Get categories success", pageResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category detail")
    public Map<String, Object> getCategoryById(@PathVariable @Min(1) Long id) {
        CategoryDTO response = categoryService.findById(id);
        return createResponse(HttpStatus.OK, "Get category detail success", response);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Create category")
    public Map<String, Object> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO response = categoryService.create(categoryDTO);
        return createResponse(HttpStatus.CREATED, "Create category success", response);
    }

    @PutMapping("/upd/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update category")
    public Map<String, Object> updateCategory(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO response = categoryService.update(id, categoryDTO);
        return createResponse(HttpStatus.OK, "Update category success", response);
    }

    @DeleteMapping("/del/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Delete category")
    public Map<String, Object> deleteCategory(@PathVariable @Min(1) Long id) {
        categoryService.delete(id);
        return createResponse(HttpStatus.NO_CONTENT, "Delete category success", null);
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