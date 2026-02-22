package vn.vuxnye.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Builder
public class ResponseAPI {

    private Map<String, Object> response = new LinkedHashMap<>();
    private HttpStatus status = HttpStatus.OK;
    private String message = "Success";
    private Object data;

}