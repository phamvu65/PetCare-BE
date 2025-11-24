package vn.vuxnye.dto.response;

import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.dto.CategoryDTO; // Import DTO mới

import java.util.List;

@Getter
@Setter
public class CategoryPageResponse extends PageResponseAbstract{
    private List<CategoryDTO> categories;

}