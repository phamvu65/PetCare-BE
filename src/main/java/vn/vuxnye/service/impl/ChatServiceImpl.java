package vn.vuxnye.service.impl;

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
import vn.vuxnye.model.ServiceEntity;
import vn.vuxnye.repository.ProductRepository;
import vn.vuxnye.repository.ServiceRepository;
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
    private final ServiceRepository serviceRepository;
    private final ProductRepository productRepository;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    private static final String HOTLINE = "1900-8888";

    public ChatServiceImpl(@Qualifier("geminiRestTemplate") RestTemplate restTemplate,
                           ServiceRepository serviceRepository,
                           ProductRepository productRepository) {
        this.restTemplate = restTemplate;
        this.serviceRepository = serviceRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ChatDTO chat(ChatDTO chatDTO) {
        String contextData = buildContextData();

        // Gọi AI với kèm lịch sử
        String aiReply = callGemini(contextData, chatDTO.getMessage(), chatDTO.getHistory());

        return ChatDTO.builder()
                .reply(aiReply)
                .build();
    }

    private String buildContextData() {
        List<ServiceEntity> services = serviceRepository.findAll();
        List<ProductEntity> products = productRepository.findAll().stream().limit(30).toList();

        String serviceStr = services.isEmpty() ? "Chưa có dịch vụ." :
                services.stream()
                        .map(s -> String.format("- %s: %s VND (%s)", s.getName(), s.getPrice(), s.getDescription()))
                        .collect(Collectors.joining("\n"));

        String productStr = products.isEmpty() ? "Chưa có sản phẩm." :
                products.stream()
                        .map(p -> String.format("- %s: %s VND (Còn: %d)", p.getName(), p.getPrice(), p.getStock()))
                        .collect(Collectors.joining("\n"));

        return """
               DANH SÁCH DỊCH VỤ:
               %s
               
               DANH SÁCH SẢN PHẨM:
               %s
               """.formatted(serviceStr, productStr);
    }

    private String callGemini(String contextData, String currentMessage, List<MessageDTO> history) {
        String url = GEMINI_API_URL + geminiApiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Chuẩn bị System Prompt (Bối cảnh)
        String systemPrompt = String.format("""
            Bạn là trợ lý ảo Pet Care. Nhiệm vụ: Tư vấn dựa trên dữ liệu cửa hàng bên dưới.
            Phong cách: Ngắn gọn, thân thiện, dùng tiếng Việt, có emoji 🐶🐱.
            Lưu ý: Nếu khách hỏi giá, lấy chính xác từ dữ liệu. Nếu không biết hoặc ngoài phạm vi, hướng dẫn gọi HOTLINE %s.
            
            DỮ LIỆU CỬA HÀNG:
            %s
            """, HOTLINE, contextData);

        // 2. Xây dựng danh sách messages (contents) gửi cho Gemini
        List<Map<String, Object>> contents = new ArrayList<>();

        // Bước A: Thêm Context vào đầu cuộc hội thoại (Giả lập user gửi context trước)
        contents.add(createMessage("user", systemPrompt));
        contents.add(createMessage("model", "Ok, tôi đã hiểu thông tin cửa hàng. Tôi sẵn sàng hỗ trợ khách hàng ngay bây giờ."));

        // Bước B: Thêm Lịch sử chat (nếu có)
        if (history != null && !history.isEmpty()) {
            for (MessageDTO msg : history) {
                // Chỉ chấp nhận role là 'user' hoặc 'model' để tránh lỗi API
                String role = "user".equalsIgnoreCase(msg.getRole()) ? "user" : "model";
                contents.add(createMessage(role, msg.getMessage()));
            }
        }

        // Bước C: Thêm Tin nhắn hiện tại
        contents.add(createMessage("user", currentMessage));

        // 3. Đóng gói Request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", contents);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
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
            return "Xin lỗi, mình không tìm thấy câu trả lời.";

        } catch (HttpClientErrorException e) {
            log.error("Gemini Client Error: {}", e.getResponseBodyAsString());
            return "Lỗi kết nối AI (Mã: " + e.getStatusCode() + "). Hotline: " + HOTLINE;
        } catch (Exception e) {
            log.error("Gemini System Error", e);
            return "Hệ thống đang bảo trì. Hotline: " + HOTLINE;
        }
    }

    // Helper để tạo cấu trúc JSON Message của Gemini
    private Map<String, Object> createMessage(String role, String text) {
        Map<String, Object> part = new HashMap<>();
        part.put("text", text);

        Map<String, Object> message = new HashMap<>();
        message.put("role", role);
        message.put("parts", List.of(part));

        return message;
    }
}