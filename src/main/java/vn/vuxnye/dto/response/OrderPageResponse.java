package vn.vuxnye.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderPageResponse extends PageResponseAbstract{
    private List<OrderResponse> orders;

}