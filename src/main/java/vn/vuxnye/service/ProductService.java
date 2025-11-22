package vn.vuxnye.service;

import vn.vuxnye.dto.request.ProductRequest;
import vn.vuxnye.dto.response.ProductPageResponse;
import vn.vuxnye.dto.response.ProductResponse;

public interface ProductService {

    // Public: Xem danh sách
    ProductPageResponse findAll(String keyword, String sort, int page, int size);

    // Public: Xem chi tiết
    ProductResponse findById(Long id);

    // Admin: Tạo mới
    ProductResponse create(ProductRequest request);

    // Admin: Cập nhật
    ProductResponse update(Long id, ProductRequest request);

    // Admin: Xóa
    void delete(Long id);
}