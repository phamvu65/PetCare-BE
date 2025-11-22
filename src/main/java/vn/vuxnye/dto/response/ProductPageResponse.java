package vn.vuxnye.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponse extends PageResponseAbstract{
    private List<ProductResponse> products;
}
