package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@Slf4j(topic = "SERVICE-CONTROLLER")
@Validated
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * Get service list (Public or Authenticated)
     */
    @GetMapping("/list")
    @Operation(summary = "Get all services", description = "Retrieve list of services with pagination and search")
    // @PreAuthorize("permitAll()") // Use this if you want to open it completely
    public Map<String, Object> getAllServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "id:asc") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        ServicePageResponse pageResponse = serviceService.findAll(keyword, sort, page, size);
        return createResponse(HttpStatus.OK, "Get services success", pageResponse);
    }

    /**
     * Get service detail by ID
     */
    @GetMapping("/{serviceId}")
    @Operation(summary = "Get service detail", description = "Retrieve a service by ID")
    public Map<String, Object> getServiceById(@PathVariable @Min(1) Long serviceId) {
        ServiceResponse response = serviceService.findById(serviceId);
        return createResponse(HttpStatus.OK, "Get service detail success", response);
    }

    /**
     * Create new service (Admin only)
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Create service", description = "Create a new service")
    public Map<String, Object> createService(@Valid @RequestBody ServiceRequest request) {
        ServiceResponse response = serviceService.create(request);
        return createResponse(HttpStatus.CREATED, "Create service success", response);
    }

    /**
     * Update service (Admin only)
     */
    @PutMapping("/upd/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update service", description = "Update an existing service")
    public Map<String, Object> updateService(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody ServiceRequest request) {
        ServiceResponse response = serviceService.update(id, request);
        return createResponse(HttpStatus.OK, "Update service success", response);
    }

    /**
     * Delete service (Admin only)
     */
    @DeleteMapping("/del/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Delete service", description = "Delete a service by ID")
    public Map<String, Object> deleteService(@PathVariable @Min(1) Long id) {
        serviceService.delete(id);
        return createResponse(HttpStatus.NO_CONTENT, "Delete service success", null);
    }


    private Map<String, Object> createResponse(HttpStatus status, String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", status.value());
        result.put("message", message);
        result.put("data", data);
        return result;
    }

}