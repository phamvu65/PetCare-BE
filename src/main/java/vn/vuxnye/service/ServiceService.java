package vn.vuxnye.service;

import vn.vuxnye.dto.request.ServiceRequest;
import vn.vuxnye.dto.response.ServicePageResponse;
import vn.vuxnye.dto.response.ServiceResponse;

public interface ServiceService {

    // Thêm tham số boolean active
    ServicePageResponse findAll(String keyword, Boolean active, String sort, int page, int size);

    ServiceResponse findById(Long id);

    ServiceResponse create(ServiceRequest request);

    ServiceResponse update(Long id, ServiceRequest request);

    void delete(Long id);
}