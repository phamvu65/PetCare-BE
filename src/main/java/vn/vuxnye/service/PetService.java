package vn.vuxnye.service;

import vn.vuxnye.dto.request.PetCreationRequest;
import vn.vuxnye.dto.response.PetPageResponse;
import vn.vuxnye.dto.response.PetResponse;

public interface PetService {
    PetPageResponse findAll(String keyword, String sort, int page, int size);

    PetResponse findById(Long id);

    PetResponse findByOwner(Long ownerId);

    PetResponse findByName(String name);

    Long save(PetCreationRequest req);

    void update(Long id, Long ownerId);

    void delete(Long id);
}
