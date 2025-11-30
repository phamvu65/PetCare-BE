package vn.vuxnye.dto.response;

import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.model.AddressEntity;
import java.io.Serializable;

@Getter
@Setter
public class AddressResponse implements Serializable {
    // 🟢 1. THÊM TRƯỜNG ID
    private Long id;

    private String recipientName;
    private String recipientPhone;
    private String city;
    private String ward;
    private String addressDetail;

    // 🟢 2. THÊM TRƯỜNG isDefault (để FE biết cái nào mặc định)
    private Boolean isDefault;

    public AddressResponse(AddressEntity addressEntity){
        // 🟢 3. GÁN GIÁ TRỊ ID
        this.id = addressEntity.getId();

        this.recipientName = addressEntity.getRecipientName();
        this.recipientPhone = addressEntity.getRecipientPhone();
        this.city = addressEntity.getCity();
        this.ward = addressEntity.getWard();
        this.addressDetail = addressEntity.getAddressDetail();

        // 🟢 4. GÁN GIÁ TRỊ isDefault
        this.isDefault = addressEntity.getIsDefault();
    }
}