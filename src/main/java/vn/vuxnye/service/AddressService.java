package vn.vuxnye.service;

import vn.vuxnye.dto.request.AddressRequest;
import vn.vuxnye.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddressesByUserId(Long userId);

    AddressResponse getAddressDefaultByUserId(Long userId);

    Long addAddress(Long userId,AddressRequest req);

    void updateAddress(Long addressId,Long userId, AddressRequest req);

    void deleteAddress(Long addressId,Long userId);

}
