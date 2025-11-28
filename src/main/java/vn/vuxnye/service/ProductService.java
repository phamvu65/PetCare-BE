package vn.vuxnye.service;

import vn.vuxnye.dto.request.ProductRequest;
import vn.vuxnye.dto.response.ProductPageResponse;
import vn.vuxnye.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    // Public: Xem danh sách
    // Lưu ý: Đã đổi Integer categoryId -> List<Long> categoryIds
    // Thêm tham số isDeleted vào findAll
    ProductPageResponse findAll(String keyword, String sort, int page, int size, List<Long> categoryIds, Boolean isDeleted);

    // Thêm hàm khôi phục
    void restore(Long id);

    // Public: Xem chi tiết
    ProductResponse findById(Long id);

    // Admin: Tạo mới
    ProductResponse create(ProductRequest request);

    // Admin: Cập nhật
    ProductResponse update(Long id, ProductRequest request);

    // Admin: Xóa
    void delete(Long id);
}