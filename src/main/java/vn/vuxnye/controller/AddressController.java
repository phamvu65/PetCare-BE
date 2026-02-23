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
import vn.vuxnye.dto.request.AddressRequest;
import vn.vuxnye.dto.response.AddressResponse;
import vn.vuxnye.service.AddressService;


import java.util.List;

@RestController
@RequestMapping("/addresses")
@Tag(name = "Address Controller")
@RequiredArgsConstructor
@Slf4j(topic = "ADDRESS-CONTROLLER")
@Validated
@PreAuthorize("isAuthenticated()")
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/list")
    @Operation(summary = "Get address list", description = "API retrieve address list from db")
    public ResponseAPI getAllAddressByUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("get address list");

        List<AddressResponse> addressList = addressService.getAddressesByUsername(userDetails.getUsername());

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Address list")
                .data(addressList)
                .build();
    }

    @GetMapping("/default")
    @Operation(summary = "Get address default", description = "API retrieve default address of current user")
    public ResponseAPI getDefaultAddress(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} requesting get default address", userDetails.getUsername());

        AddressResponse addressDefault = addressService.getAddressDefaultByUsername(userDetails.getUsername());

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Address default")
                .data(addressDefault)
                .build();
    }

    @PostMapping("/add")
    @Operation(summary = "Create Address", description = "API add new address to db")
    public ResponseAPI createAddress(@Valid @RequestBody AddressRequest addressRequest,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} creating address: {}", userDetails.getUsername(), addressRequest);

        addressService.addAddress(userDetails.getUsername(), addressRequest);

        return ResponseAPI.builder()
                .status(HttpStatus.CREATED)
                .message("Address created successfully")
                .data(null)
                .build();
    }

    @PutMapping("/upd/{addressId}")
    public ResponseAPI updateAddress(@PathVariable @Min(value = 1, message = "addressId must be equals or greater than 1") Long addressId,
                                     @Valid @RequestBody AddressRequest addressRequest,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} updating address ID: {}", userDetails.getUsername(), addressId);

        addressService.updateAddress(addressId, userDetails.getUsername(), addressRequest);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Address updated successfully")
                .data(addressRequest)
                .build();
    }

    @DeleteMapping("/del/{addressId}")
    @Operation(summary = "Delete Address", description = "API delete address by id")
    public ResponseAPI deleteAddress(@AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable @Min(value = 1, message = "addressId must be equals or greater than 1") Long addressId) {

        log.info("User {} deleting address ID: {}", userDetails.getUsername(), addressId);

        addressService.deleteAddress(addressId, userDetails.getUsername());

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Address deleted successfully")
                .data(null)
                .build();
    }
}