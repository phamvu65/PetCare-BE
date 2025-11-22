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
import vn.vuxnye.dto.request.ProductRequest;
import vn.vuxnye.dto.response.ProductPageResponse;
import vn.vuxnye.dto.response.ProductResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.CategoryEntity;
import vn.vuxnye.model.ProductEntity;
import vn.vuxnye.model.ProductImageEntity;
import vn.vuxnye.repository.CategoryRepository;
import vn.vuxnye.repository.ProductRepository;
import vn.vuxnye.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "PRODUCT-IMPL")
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // Cần repo này để check Category

    @Override
    @Transactional(readOnly = true)
    public ProductPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Find all products with keyword: {}", keyword);

        // Logic sort (Giống Pet/Service)
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

        Page<ProductEntity> entityPage = productRepository.searchProducts(keyword, pageable);

        // Convert DTO
        List<ProductResponse> productList = entityPage.stream()
                .map(ProductResponse::fromEntity)
                .toList();

        ProductPageResponse response = new ProductPageResponse();
        response.setProducts(productList);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(entityPage.getTotalElements());
        response.setTotalPages(entityPage.getTotalPages());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        ProductEntity entity = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(entity);
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        log.info("Create product: {}", request.getName());

        // 1. Tìm Category
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found id: " + request.getCategoryId()));

        // 2. Tạo Product
        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        // 3. Xử lý Images (Nếu có)
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImageEntity> images = new ArrayList<>();
            for (String url : request.getImageUrls()) {
                ProductImageEntity img = new ProductImageEntity();
                img.setProduct(product);
                img.setImageUrl(url);
                images.add(img);
            }
            product.setImages(images);
        }

        ProductEntity savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        log.info("Update product id: {}", id);

        // 1. Tìm Product cũ (dùng findById thường để Hibernate quản lý session tốt hơn khi update)
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // 2. Update Category nếu thay đổi
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            CategoryEntity newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found id: " + request.getCategoryId()));
            product.setCategory(newCategory);
        }

        // 3. Update thông tin cơ bản
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        // 4. Update Images (Xóa cũ, thêm mới - Cách đơn giản nhất)
        if (request.getImageUrls() != null) {
            // Xóa list cũ (orphanRemoval=true sẽ xóa khỏi DB)
            product.getImages().clear();

            // Thêm list mới
            for (String url : request.getImageUrls()) {
                ProductImageEntity img = new ProductImageEntity();
                img.setProduct(product);
                img.setImageUrl(url);
                product.getImages().add(img);
            }
        }

        ProductEntity updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }

    @Override
    public void delete(Long id) {
        log.warn("Delete product id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}