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
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductPageResponse findAll(String keyword, String sort, int page, int size, List<Long> categoryIds) {
        // Lưu ý: Đã đổi tham số categoryId (Integer) -> categoryIds (List<Long>)
        log.info("Find all products with keyword: {} and categoryIds: {}", keyword, categoryIds);

        // 1. Xử lý Sort
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

        // 2. Gọi Repository (Query mới đã handle việc list null hoặc rỗng)
        // Không cần if/else check null ở đây nữa
        Page<ProductEntity> entityPage = productRepository.searchProducts(keyword, categoryIds, pageable);

        // 3. Convert Entity sang DTO
        List<ProductResponse> productList = entityPage.stream()
                .map(ProductResponse::fromEntity)
                .toList();

        // 4. Tạo response
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

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found id: " + request.getCategoryId()));

        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

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

        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            CategoryEntity newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found id: " + request.getCategoryId()));
            product.setCategory(newCategory);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        if (request.getImageUrls() != null) {
            product.getImages().clear();
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