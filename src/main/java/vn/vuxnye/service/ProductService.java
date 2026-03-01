package vn.vuxnye.service;

import vn.vuxnye.dto.request.ProductRequest;
import vn.vuxnye.dto.response.ProductPageResponse;
import vn.vuxnye.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductPageResponse findAll(String keyword, String sort, int page, int size, List<Long> categoryIds, Boolean isDeleted);

    void restore(Long id);

    ProductResponse findById(Long id);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}