package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.dto.request.AddressRequest;
import vn.vuxnye.dto.response.AddressResponse;
import vn.vuxnye.service.AddressService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("addresses")
@Tag(name = "Address Controller")
@RequiredArgsConstructor
@Slf4j(topic = "ADDRESS-CONTROLLER")
@Validated
@PreAuthorize("isAuthenticated()")
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/list")
    @Operation(summary = "Get address list",description = "API retrieve address list from db")
    public Map<String,Object> getAllAddressByUser(@AuthenticationPrincipal UserDetails userDetails) {;
        log.info("get address list");

        List<AddressResponse> addressList = addressService.getAddressesByUsername(userDetails.getUsername());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", 200);
        result.put("message", "Address list");
        result.put("data", addressList);
        return result;
    }

    @GetMapping("/default")
    @Operation(summary = "Get address default",description = "API retrieve default address of current user")
    public Map<String,Object> getDefaultAddress(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} requesting get default address", userDetails.getUsername());
        AddressResponse addressDefault = addressService.getAddressDefaultByUsername(userDetails.getUsername());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", 200);
        result.put("message", "Address default");
        result.put("data", addressDefault);
        return result;
    }

    @Operation(summary = "Create Address",description = "API add new address to db")
    @PostMapping("/add")
    public Map<String, Object> createAddress( @Valid @RequestBody AddressRequest addressRequest,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} creating address: {}", userDetails.getUsername(), addressRequest);

        addressService.addAddress(userDetails.getUsername(), addressRequest);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", 201);
        result.put("message", "Address created successfully");
        result.put("data", "Address created successfully");
        return result;

    }

    @PutMapping("/upd/{addressId}")
    public Map<String, Object> updateAddress(@PathVariable @Min(value = 1,message ="addressId must be equals or greater than 1") Long addressId,
                                             @Valid @RequestBody AddressRequest addressRequest,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        log.info("User {} updating address ID: {}", userDetails.getUsername(), addressId);
        addressService.updateAddress(addressId, userDetails.getUsername(), addressRequest);

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", 200);
        result.put("message", "Address updated successfully");
        result.put("data", addressRequest);
        return result;
    }


    @Operation(summary = "Delete Address",description = "API delete address by id")
    @DeleteMapping("/del/{addressId}")
    public Map<String, Object> deleteAddress(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable @Min(value = 1,message ="addressId must be equals or greater than 1") Long addressId){

        log.info("User {} deleting address ID: {}", userDetails.getUsername(), addressId);
        addressService.deleteAddress(addressId,userDetails.getUsername());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", 204);
        result.put("message", "Address deleted successfully");
        result.put("data", "");
        return result;

    }

}
