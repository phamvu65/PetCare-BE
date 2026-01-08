package vn.vuxnye.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Giúp ẩn field null (ví dụ: khi gửi request thì không cần gửi field reply)
public class ChatDTO {

    // Dùng cho Request (Input từ người dùng)
    @NotBlank(message = "Tin nhắn không được để trống")
    private String message;

    // Input: Lịch sử chat trước đó (Gửi từ FE lên)
    // Danh sách các cặp câu hỏi - trả lời cũ
    private List<MessageDTO> history;

    // Dùng cho Response (Output từ AI)
    private String reply;

}