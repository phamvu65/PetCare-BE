package vn.vuxnye.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.dto.request.AddressRequest;
import vn.vuxnye.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddressesByUsername(String username);

    AddressResponse getAddressDefaultByUsername(String username);

    Long addAddress(String username,AddressRequest req);

    void updateAddress(Long addressId,String username, AddressRequest req);

    void deleteAddress(Long addressId,String username);

}
