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
import vn.vuxnye.dto.request.ServiceRequest;
import vn.vuxnye.dto.response.ServicePageResponse;
import vn.vuxnye.dto.response.ServiceResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.ServiceEntity;
import vn.vuxnye.repository.ServiceRepository;
import vn.vuxnye.service.ServiceService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "SERVICE-IMPL")
@RequiredArgsConstructor
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    @Override
    @Transactional(readOnly = true)
    public ServicePageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Find all services with keyword: {}", keyword);

        // Sorting logic
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

        Page<ServiceEntity> entityPage = serviceRepository.searchServices(keyword, pageable);

        // Convert to DTO
        List<ServiceResponse> serviceList = entityPage.stream()
                .map(ServiceResponse::fromEntity)
                .toList();

        ServicePageResponse response = new ServicePageResponse();
        response.setServices(serviceList);
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(entityPage.getTotalElements());
        response.setTotalPages(entityPage.getTotalPages());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse findById(Long id) {
        log.info("Find service by id: {}", id);
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return ServiceResponse.fromEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResponse create(ServiceRequest request) {
        log.info("Create new service: {}", request.getName());

        // (Optional) Check if name exists
        // if (serviceRepository.existsByName(request.getName())) {
        //     throw new RuntimeException("Service name already exists");
        // }

        ServiceEntity newService = ServiceEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .durationMin(request.getDurationMin())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        ServiceEntity savedService = serviceRepository.save(newService);
        return ServiceResponse.fromEntity(savedService);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResponse update(Long id, ServiceRequest request) {
        log.info("Update service id: {}", id);

        ServiceEntity serviceToUpdate = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        serviceToUpdate.setName(request.getName());
        serviceToUpdate.setDescription(request.getDescription());
        serviceToUpdate.setImageUrl(request.getImageUrl());
        serviceToUpdate.setPrice(request.getPrice());
        serviceToUpdate.setDurationMin(request.getDurationMin());
        if (request.getActive() != null) {
            serviceToUpdate.setActive(request.getActive());
        }

        ServiceEntity updatedService = serviceRepository.save(serviceToUpdate);
        return ServiceResponse.fromEntity(updatedService);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.warn("Delete service id: {}", id);

        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service not found with id: " + id);
        }
        // You can choose Soft Delete (active = false) or Hard Delete
        serviceRepository.deleteById(id);
    }
}