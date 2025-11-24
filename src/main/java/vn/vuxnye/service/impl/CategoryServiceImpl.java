package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.vuxnye.dto.CategoryDTO; // Dùng DTO chung
import vn.vuxnye.dto.response.CategoryPageResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.CategoryEntity;
import vn.vuxnye.repository.CategoryRepository;
import vn.vuxnye.service.CategoryService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "CATEGORY-IMPL")
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public CategoryPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Find all categories with keyword: {}", keyword);

        // (Logic sort giữ nguyên)
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, columnName);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }

        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<CategoryEntity> entityPage = categoryRepository.searchCategories(keyword, pageable);

        // Convert to DTO
        List<CategoryDTO> list = entityPage.stream()
                .map(CategoryDTO::fromEntity)
                .toList();

        CategoryPageResponse response = new CategoryPageResponse();
        response.setCategories(list);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(entityPage.getTotalElements());
        response.setTotalPages(entityPage.getTotalPages());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        log.info("Find category by id: {}", id);
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return CategoryDTO.fromEntity(entity);
    }

    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
        log.info("Create category: {}", categoryDTO.getName());

        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new RuntimeException("Category name already exists: " + categoryDTO.getName());
        }

        // Dùng hàm toEntity trong DTO cho gọn
        CategoryEntity newCategory = CategoryDTO.toEntity(categoryDTO);

        CategoryEntity savedCategory = categoryRepository.save(newCategory);
        return CategoryDTO.fromEntity(savedCategory);
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        log.info("Update category id: {}", id);

        CategoryEntity categoryToUpdate = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!categoryToUpdate.getName().equals(categoryDTO.getName()) &&
                categoryRepository.existsByName(categoryDTO.getName())) {
            throw new RuntimeException("Category name already exists: " + categoryDTO.getName());
        }

        categoryToUpdate.setName(categoryDTO.getName());

        CategoryEntity updatedCategory = categoryRepository.save(categoryToUpdate);
        return CategoryDTO.fromEntity(updatedCategory);
    }

    @Override
    public void delete(Long id) {
        log.warn("Delete category id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}