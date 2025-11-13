package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.dto.request.AddressRequest;
import vn.vuxnye.dto.response.AddressResponse;
import vn.vuxnye.service.AddressService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/{userId}/addresses")
@Tag(name = "Address Controller")
@RequiredArgsConstructor
@Slf4j(topic = "ADDRESS-CONTROLLER")
@Validated
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get address list",description = "API retrieve address list from db")
    public Map<String,Object> getAllAddressByUser(@PathVariable @Min(value = 1,message ="UserId must be equals or greater than 1") Long userId) {;
        log.info("get address list");

        List<AddressResponse> addressList = addressService.getAddressesByUserId(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", 200);
        result.put("message", "Address list");
        result.put("data", addressList);
        return result;
    }

    @GetMapping("/default")
    @Operation(summary = "Get address default",description = "API retrieve address default by id")
    public Map<String,Object> getDefaultAddress(@PathVariable @Min(value = 1,message ="UserId must be equals or greater than 1") Long userId) {
        log.info("Get Address default of user ID:{}", userId);

        AddressResponse addressDefault = addressService.getAddressDefaultByUserId(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", 200);
        result.put("message", "Address default");
        result.put("data", addressDefault);
        return result;
    }

    @Operation(summary = "Create Address",description = "API add new address to db")
    @PostMapping
    public Map<String, Object> createAddress(@PathVariable @Min(value = 1,message ="UserId must be equals or greater than 1") Long userId,
                                             @RequestBody AddressRequest addressRequest) {
        log.info("Create Address:{}", addressRequest);

        addressService.addAddress(userId, addressRequest);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", 201);
        result.put("message", "Address created successfully");
        result.put("data", "Address created successfully");
        return result;

    }

    @PutMapping("/{addressId}")
    public Map<String, Object> updateAddress(@PathVariable @Min(value = 1,message ="UserId must be equals or greater than 1") Long userId,
                                             @PathVariable @Min(value = 1,message ="addressId must be equals or greater than 1") Long addressId,
                                             @RequestBody AddressRequest addressRequest) {
        log.info("Update ID address: {} for user ID:{}", addressId, userId);

        addressService.updateAddress(userId, addressId, addressRequest);

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", 200);
        result.put("message", "Address updated successfully");
        result.put("data", addressRequest);
        return result;
    }


    @Operation(summary = "Delete Address",description = "API delete address by id")
    @DeleteMapping("/{addressId}")
    public Map<String, Object> deleteAddress(@PathVariable @Min(value = 1,message ="UserId must be equals or greater than 1") Long userId,
                                             @PathVariable @Min(value = 1,message ="addressId must be equals or greater than 1") Long addressId){

        log.info("Delete ID address: {} for user ID:{}", addressId, userId);
        addressService.deleteAddress(addressId,userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", 204);
        result.put("message", "Address deleted successfully");
        result.put("data", "");
        return result;

    }



}
