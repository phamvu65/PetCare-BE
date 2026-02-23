package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.dto.request.PetCreationRequest;
import vn.vuxnye.dto.request.PetUpdateRequest;
import vn.vuxnye.dto.response.PetPageResponse;
import vn.vuxnye.dto.response.PetResponse;
import vn.vuxnye.service.PetService;

import java.util.List;

@RestController
@RequestMapping("/pets")
@Tag(name = "Pet Controller")
@RequiredArgsConstructor
@Slf4j(topic = "PET-CONTROLLER")
@Validated
@PreAuthorize("isAuthenticated()")  // Require login for all api
public class PetController {

    private final PetService petService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin get pet list", description = "API retrieve pet from db")
    public ResponseAPI getAllPets(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String sort,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        log.info("getPets page: {}, size: {}", page, size);

        PetPageResponse pageResponse = petService.findAll(keyword, sort, page, size);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Pet list")
                .data(pageResponse)
                .build();
    }

    // 🟢 Chú ý: Đã đổi từ "/{userId}" thành "/my-pets"
    @GetMapping("/my-pets")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Customer get pet list", description = "API retrieve pet from db")
    public ResponseAPI getMyPets(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("getMyPets user: {}", userDetails.getUsername());

        List<PetResponse> pets = petService.getMyPets(userDetails);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Pet list")
                .data(pets)
                .build();
    }

    @PostMapping("/add")
    @Operation(summary = "Create Pet", description = "API add new pet to db")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseAPI createPet(
            @Valid @RequestBody PetCreationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        PetResponse newPet = petService.addPet(request, userDetails);

        return ResponseAPI.builder()
                .status(HttpStatus.CREATED)
                .message("Pet created successfully")
                .data(newPet)
                .build();
    }

    @PutMapping("/upd")
    @Operation(summary = "Update Pet", description = "API update pet to db")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseAPI updatePet(
            @Valid @RequestBody PetUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        PetResponse updatedPet = petService.updatePet(request, userDetails);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Pet updated successfully")
                .data(updatedPet)
                .build();
    }

    @DeleteMapping("/del/{petId}")
    @Operation(summary = "Delete Pet", description = "API delete pet by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseAPI deletePet(
            @PathVariable @Min(1) Long petId,
            @AuthenticationPrincipal UserDetails userDetails) {

        petService.deletePet(petId, userDetails);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Pet deleted successfully")
                .data(null)
                .build();
    }
}