package vn.vuxnye.service;

import vn.vuxnye.dto.CategoryDTO;
import vn.vuxnye.dto.response.CategoryPageResponse;

public interface CategoryService {

    // Public
    CategoryPageResponse findAll(String keyword, String sort, int page, int size);

    // Public
    CategoryDTO findById(Long id);

    // Admin only
    CategoryDTO create(CategoryDTO categoryDTO);

    // Admin only
    CategoryDTO update(Long id, CategoryDTO categoryDTO);

    // Admin only
    void delete(Long id);
}