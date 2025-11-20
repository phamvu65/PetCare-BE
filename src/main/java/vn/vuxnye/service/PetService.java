package vn.vuxnye.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.dto.request.PetCreationRequest;
import vn.vuxnye.dto.request.PetUpdateRequest;
import vn.vuxnye.dto.response.PetPageResponse;
import vn.vuxnye.dto.response.PetResponse;

import java.util.List;

public interface PetService {
    PetPageResponse findAll(String keyword, String sort, int page, int size);

    PetResponse findById(Long id);

    List<PetResponse> getMyPets(UserDetails userDetails);

    PetResponse addPet(PetCreationRequest req, UserDetails userDetails);

    PetResponse updatePet(PetUpdateRequest req, UserDetails userDetails);

    void deletePet(Long petId, UserDetails userDetails);
}
