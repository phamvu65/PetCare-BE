package vn.vuxnye.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    // Rating chỉ bắt buộc nếu là đánh giá gốc (parentId == null)
    // Logic validate sẽ chuyển vào Service
    private Byte rating;

    @NotBlank(message = "Comment content cannot be empty")
    private String comment;

    //Nếu trả lời bình luận khác thì gửi kèm ID này
    private Long parentId;
}