package vn.vuxnye.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.dto.request.AddressRequest;
import vn.vuxnye.model.AddressEntity;

import java.io.Serializable;

@Getter
@Setter
public class AddressResponse implements Serializable {
    private String recipientName;
    private String recipientPhone;
    private String city;
    private String ward;
    private String addressDetail; // Số nhà, đường...

    public AddressResponse(AddressEntity addressEntity){
        this.recipientName = addressEntity.getRecipientName();
        this.recipientPhone = addressEntity.getRecipientPhone();
        this.city = addressEntity.getCity();
        this.ward = addressEntity.getWard();
        this.addressDetail = addressEntity.getAddressDetail();
    }
}
