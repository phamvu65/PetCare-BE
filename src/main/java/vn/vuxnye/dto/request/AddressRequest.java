package vn.vuxnye.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest implements java.io.Serializable{
//    @NotBlank(message = "Address id must be not blank")
//    private Long id;

    @NotBlank (message = "Tên người nhận không được để trống")
    private String recipientName;

    @NotBlank(message = "SĐT người nhận không được để trống")
    private String recipientPhone;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city;

    @NotBlank(message = "Xã/Phường không được để trống")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String addressDetail; // Số nhà, đường...

    private Boolean isDefault = false; // Mặc định là 'false'
}
