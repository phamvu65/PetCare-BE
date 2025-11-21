package vn.vuxnye.service;

import vn.vuxnye.dto.request.ServiceRequest;
import vn.vuxnye.dto.response.ServicePageResponse;
import vn.vuxnye.dto.response.ServiceResponse;

public interface ServiceService {

    ServicePageResponse findAll(String keyword, String sort, int page, int size);

    ServiceResponse findById(Long id);

    // Admin only
    ServiceResponse create(ServiceRequest request);

    // Admin only
    ServiceResponse update(Long id, ServiceRequest request);

    // Admin only
    void delete(Long id);
}