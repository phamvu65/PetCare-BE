package vn.vuxnye.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import vn.vuxnye.dto.ChatDTO;
import vn.vuxnye.dto.MessageDTO;
import vn.vuxnye.model.ProductEntity;
import vn.vuxnye.repository.ProductRepository;
import vn.vuxnye.service.ChatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "AI-CHAT-SERVICE")
public class ChatServiceImpl implements ChatService {

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    // System Prompt dạy AI trả về JSON
    private static final String SYSTEM_INSTRUCTION = """
            Bạn là trợ lý ảo Pet Care.
            
            QUY TẮC QUAN TRỌNG (FUNCTION CALLING):
            1. Nếu khách hỏi thông tin cần tra cứu (giá, tồn kho, mua hàng, tìm đồ chơi, thức ăn...), ĐỪNG TRẢ LỜI NGAY. Hãy trả về JSON sau:
               { "tool": "search_product", "args": { "keyword": "từ khóa chính" } }
            
            2. Nếu là câu hỏi xã giao hoặc đã có thông tin, hãy trả lời bằng text bình thường, thân thiện, có emoji 🐶🐱.
            
            Ví dụ:
            - Khách: "Có bán hạt mèo không?" -> Bạn trả về: { "tool": "search_product", "args": { "keyword": "hạt mèo" } }
            - Khách: "Đồ chơi cho chó" -> Bạn trả về: { "tool": "search_product", "args": { "keyword": "đồ chơi chó" } }
            """;

    public ChatServiceImpl(@Qualifier("geminiRestTemplate") RestTemplate restTemplate,
                           ProductRepository productRepository,
                           ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatDTO chat(ChatDTO chatDTO) {
        // 1. Gửi tin nhắn đầu tiên cho AI
        String aiResponse = callGemini(chatDTO.getMessage(), chatDTO.getHistory(), null);

        // 2. Kiểm tra xem AI có trả về lệnh JSON không
        String toolResult = handleToolCall(aiResponse);

        // 3. Nếu có kết quả từ tool (tức là AI vừa yêu cầu tìm kiếm)
        if (toolResult != null) {
            // Gọi lại AI lần 2 kèm kết quả tìm kiếm để AI trả lời khách
            String finalReply = callGemini(chatDTO.getMessage(), chatDTO.getHistory(), toolResult);
            return ChatDTO.builder().reply(finalReply).build();
        }

        // Nếu không dùng tool, trả về luôn câu trả lời của AI
        return ChatDTO.builder().reply(aiResponse).build();
    }

    // Hàm xử lý logic gọi tool
    private String handleToolCall(String aiResponse) {
        try {
            // Làm sạch chuỗi json
            String cleanJson = aiResponse.replace("```json", "").replace("```", "").trim();

            if (cleanJson.startsWith("{") && cleanJson.endsWith("}")) {
                JsonNode rootNode = objectMapper.readTree(cleanJson);
                if (rootNode.has("tool") && "search_product".equals(rootNode.get("tool").asText())) {

                    String keyword = rootNode.get("args").get("keyword").asText();
                    log.info("AI đang tìm kiếm sản phẩm với từ khóa: {}", keyword);

                    // UPDATE 2: Dùng hàm searchEverything thay vì findByName để tìm cả trong Danh mục & Mô tả
                    // (Đảm bảo bạn đã thêm hàm này vào ProductRepository như hướng dẫn trước)
                    List<ProductEntity> products = productRepository.searchEverything(keyword);

                    if (products.isEmpty()) {
                        return "HỆ THỐNG: Không tìm thấy sản phẩm nào khớp với từ khóa '" + keyword + "'.";
                    }

                    // Convert kết quả sang String gửi lại cho AI
                    String productInfo = products.stream()
                            .limit(6) // Lấy 6 sản phẩm
                            .map(p -> String.format("- %s: %s VND (Kho: %d)", p.getName(), p.getPrice(), p.getStock()))
                            .collect(Collectors.joining("\n"));

                    return "HỆ THỐNG ĐÃ TÌM THẤY DỮ LIỆU:\n" + productInfo;
                }
            }
        } catch (Exception e) {
            // Không phải JSON hoặc lỗi parse -> Bỏ qua
        }
        return null;
    }

    private String callGemini(String currentMessage, List<MessageDTO> history, String toolContext) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, Object>> contents = new ArrayList<>();

        // A. System Prompt
        contents.add(createMessage("user", SYSTEM_INSTRUCTION));
        contents.add(createMessage("model", "Ok, tôi đã hiểu."));

        // B. Lịch sử chat
        if (history != null) {
            for (MessageDTO msg : history) {
                String role = "user".equalsIgnoreCase(msg.getRole()) ? "user" : "model";
                contents.add(createMessage(role, msg.getMessage()));
            }
        }

        // C. Context
        if (toolContext != null) {
            contents.add(createMessage("user", toolContext + "\n\n Dựa vào dữ liệu trên, hãy trả lời câu hỏi: " + currentMessage));
        } else {
            contents.add(createMessage("user", currentMessage));
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", contents);
        requestBody.put("generationConfig", Map.of("temperature", 0.4));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // UPDATE 3: Cơ chế Retry (Thử lại) khi gặp lỗi 429
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_API_URL + geminiApiKey, entity, Map.class);
                return extractTextFromResponse(response);

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 429) {
                    log.warn("Gemini quá tải (429), đang thử lại lần {}...", retryCount + 1);
                    retryCount++;
                    try {
                        Thread.sleep(2000); // Đợi 2 giây trước khi gọi lại
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.error("Gemini API Error: {}", e.getResponseBodyAsString());
                    return "Lỗi kết nối AI (Mã: " + e.getStatusCode() + ").";
                }
            } catch (Exception e) {
                log.error("System Error", e);
                return "Hệ thống đang bảo trì.";
            }
        }

        return "Hiện tại hệ thống AI đang quá tải, vui lòng hỏi lại sau 1 phút ạ.";
    }

    private String extractTextFromResponse(ResponseEntity<Map> response) {
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("candidates")) {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (!candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (!parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
        }
        return "";
    }

    private Map<String, Object> createMessage(String role, String text) {
        Map<String, Object> part = new HashMap<>();
        part.put("text", text);
        Map<String, Object> message = new HashMap<>();
        message.put("role", role);
        message.put("parts", List.of(part));
        return message;
    }
}