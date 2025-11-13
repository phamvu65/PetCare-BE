package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vuxnye.dto.request.AddressRequest;
import vn.vuxnye.dto.response.AddressResponse;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.AddressEntity;
import vn.vuxnye.model.UserEntity;
import vn.vuxnye.repository.AddressRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.AddressService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "ADDRESS-SERVICE")
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        log.info("Get address list by user ID:{}", userId);

        if(!userRepository.existsById(userId)){
            log.warn("User not found for address list (userId: {})", userId);
            throw new ResourceNotFoundException("User not found");
        }
        List<AddressEntity> addressEntityList = addressRepository.findByUserId(userId);
       return addressEntityList.stream()
               .map(AddressResponse::new)
               .collect(Collectors.toList());
    }

    @Override
    public AddressResponse getAddressDefaultByUserId(Long userId) {
        log.info("Get default address by user ID:{}", userId);

        if(!userRepository.existsById(userId)){
            log.warn("User not found for address list (userId: {})", userId);
            throw new ResourceNotFoundException("User not found");
        }

        AddressEntity addressEntity= addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return new AddressResponse(addressEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAddress(Long userId, AddressRequest req) {
        log.info("Add new address for user ID:{}", userId);

        UserEntity user= userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Boolean isDefault = (req.getIsDefault()!=null) ? req.getIsDefault() : false;
        if(isDefault){
            addressRepository.clearOldDefaults(userId);
        }

        //create Address
        AddressEntity newAddress = new AddressEntity();
        newAddress.setUser(user);
        newAddress.setRecipientName(req.getRecipientName());
        newAddress.setRecipientPhone(req.getRecipientPhone());
        newAddress.setCity(req.getCity());
        newAddress.setWard(req.getWard());
        newAddress.setAddressDetail(req.getAddressDetail());
        newAddress.setIsDefault(isDefault);

        addressRepository.save(newAddress);
        log.info("Saved new address:{}", newAddress);

        return newAddress.getId() != null ? newAddress.getId() : 0L ;
    }

    @Override
    public void updateAddress(Long addressId, Long userId, AddressRequest req) {
        log.info("update addressId {} for userId {}", addressId, userId);

        AddressEntity addressToUpdate = addressRepository.findByIdAndUserId(addressId,userId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Boolean isDefault = (req.getIsDefault()!=null) ? req.getIsDefault() : false;
        if(isDefault){
            addressRepository.clearOldDefaults(userId);
        }

        addressToUpdate.setRecipientName(req.getRecipientName());
        addressToUpdate.setRecipientPhone(req.getRecipientPhone());
        addressToUpdate.setCity(req.getCity());
        addressToUpdate.setWard(req.getWard());
        addressToUpdate.setAddressDetail(req.getAddressDetail());
        addressToUpdate.setIsDefault(isDefault);

        addressRepository.save(addressToUpdate);

        log.info("Updated address:{}", addressToUpdate);
    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {
        log.info("delete addressId {} for userId {}", addressId, userId);

        AddressEntity addressToDelete = addressRepository.findByIdAndUserId(addressId,userId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        //if delete address default
        if(addressToDelete.getIsDefault()){
            List<AddressEntity> addressList = addressRepository.findByUserIdAndIdNot(userId, addressId);
            if(!addressList.isEmpty()){
                addressList.get(0).setIsDefault(true);
                addressRepository.save(addressList.get(0));
            }
        }

        addressRepository.delete(addressToDelete);
    }

}
