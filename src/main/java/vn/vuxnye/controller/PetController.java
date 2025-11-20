package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.dto.request.PetCreationRequest;
import vn.vuxnye.dto.request.PetUpdateRequest;
import vn.vuxnye.dto.response.PetPageResponse;
import vn.vuxnye.dto.response.PetResponse;
import vn.vuxnye.service.PetService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pets")
@Tag(name = "Pet Controller")
@RequiredArgsConstructor
@Slf4j(topic = "PET-CONTROLLER")
@Validated
@PreAuthorize("isAuthenticated()")  //Require login for all api
public class PetController {

    private final PetService petService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin get pet list",description = "API retrieve pet from db")
    public Map<String,Object> getAllPets(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size){
        log.info("getPets page: {}, size: {}", page, size);

        PetPageResponse pageResponse = petService.findAll(keyword, sort, page, size);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status",200);
        result.put("message","Pet list");
        result.put("data",pageResponse);

        return result;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Customer get pet list",description = "API retrieve pet from db")
    public Map<String, Object> getMyPets(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("getMyPets user: {}", userDetails);

        List<PetResponse> pets = petService.getMyPets(userDetails);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status",200);
        result.put("message","Pet list");
        result.put("data",pets);
        return result;
    }

    @PostMapping("/add")
    @Operation(summary = "Create Pet",description = "API add new pet to db")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Map<String, Object> createPet(
            @Valid @RequestBody PetCreationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        PetResponse newPet = petService.addPet(request, userDetails);

        Map<String, Object> result = new HashMap<>();
        result.put("status",201);
        result.put("message","Pet created successfully");
        result.put("data",newPet);
        return result;
    }

    @PutMapping("/upd")
    @Operation(summary = "Update Pet",description = "API update pet to db")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Map<String, Object> updatePet(
            @Valid @RequestBody PetUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        PetResponse updatedPet = petService.updatePet(request, userDetails);

        Map<String, Object> result = new HashMap<>();
        result.put("status",200);
        result.put("message","Pet updated successfully");
        result.put("data",updatedPet);
        return result;
    }

    @DeleteMapping("/del/{petId}")
    @Operation(summary = "Delete Pet",description = "API delete pet by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public Map<String, Object> deletePet(
            @PathVariable @Min(1) Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {

        petService.deletePet(petId, userDetails);

        Map<String, Object> result = new HashMap<>();
        result.put("status",204);
        result.put("message","Pet deleted successfully");
        result.put("data","");
        return result;
    }

}
