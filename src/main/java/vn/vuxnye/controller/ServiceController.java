package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.dto.request.ServiceRequest;
import vn.vuxnye.dto.response.ServicePageResponse;
import vn.vuxnye.service.ServiceService;

@RestController
@RequestMapping("/services")
@Tag(name = "Service Controller")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping("/list")
    public ResponseAPI getAllServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "true") Boolean active,
            @RequestParam(required = false, defaultValue = "id:desc") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        ServicePageResponse pageResponse = serviceService.findAll(keyword, active, sort, page, size);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Get services success")
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{serviceId}")
    public ResponseAPI getServiceById(@PathVariable Long serviceId) {
        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(serviceService.findById(serviceId))
                .build();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseAPI createService(@Valid @RequestBody ServiceRequest request) {
        return ResponseAPI.builder()
                .status(HttpStatus.CREATED)
                .message("Created")
                .data(serviceService.create(request))
                .build();
    }

    @PutMapping("/upd/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseAPI updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Updated")
                .data(serviceService.update(id, request))
                .build();
    }

    @DeleteMapping("/del/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseAPI deleteService(@PathVariable Long id) {
        serviceService.delete(id);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Deleted (Soft)")
                .data(null)
                .build();
    }
}