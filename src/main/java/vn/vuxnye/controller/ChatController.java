package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.dto.ChatDTO;
import vn.vuxnye.service.ChatService;

@RestController
@RequestMapping("/chat")
@Tag(name = "AI Chat Controller")
@RequiredArgsConstructor
@Slf4j(topic = "CHAT-CONTROLLER")
public class ChatController {

    private final ChatService chatService;

    /**
     * API Chat với Trợ lý ảo
     */
    @PostMapping
    @Operation(summary = "Chat with AI Assistant", description = "Ask questions about services and products")
    public ResponseAPI chatWithAI(@Valid @RequestBody ChatDTO chatDTO) {

        log.info("Guest asking AI: {}", chatDTO.getMessage());

        ChatDTO responseDTO = chatService.chat(chatDTO);

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .data(responseDTO)
                .build();
    }
}