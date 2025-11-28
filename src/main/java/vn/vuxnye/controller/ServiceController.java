package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.dto.request.ServiceRequest;
import vn.vuxnye.dto.response.ServicePageResponse;
import vn.vuxnye.dto.response.ServiceResponse;
import vn.vuxnye.service.ServiceService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/services")
@Tag(name = "Service Controller")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping("/list")
    public Map<String, Object> getAllServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "true") Boolean active, // 👈 Thêm cái này
            @RequestParam(required = false, defaultValue = "id:desc") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        ServicePageResponse pageResponse = serviceService.findAll(keyword, active, sort, page, size);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Get services success");
        result.put("data", pageResponse);
        return result;
    }

    @GetMapping("/{serviceId}")
    public Map<String, Object> getServiceById(@PathVariable Long serviceId) {
        return createResponse(HttpStatus.OK, "Success", serviceService.findById(serviceId));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, Object> createService(@Valid @RequestBody ServiceRequest request) {
        return createResponse(HttpStatus.CREATED, "Created", serviceService.create(request));
    }

    @PutMapping("/upd/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, Object> updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        return createResponse(HttpStatus.OK, "Updated", serviceService.update(id, request));
    }

    @DeleteMapping("/del/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, Object> deleteService(@PathVariable Long id) {
        serviceService.delete(id);
        return createResponse(HttpStatus.OK, "Deleted (Soft)", null);
    }

    private Map<String, Object> createResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("message", message);
        result.put("data", data);
        return result;
    }
}