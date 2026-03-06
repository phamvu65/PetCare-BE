🐾 PetCare - Nền tảng Thương mại điện tử & Dịch vụ Thú cưng

📖 Giới thiệu (Overview)

PetCare là hệ thống Backend cung cấp các API RESTful cho một nền tảng thương mại điện tử kết hợp đặt lịch dịch vụ chăm sóc thú cưng (Spa, Cắt tỉa lông, Khám bệnh).

Dự án được xây dựng với mục tiêu xử lý các luồng nghiệp vụ phức tạp trong thực tế như: quản lý vòng đời đơn hàng, giải quyết bài toán chống trùng lịch hẹn (double-booking), tối ưu hóa truy vấn cơ sở dữ liệu và tích hợp các dịch vụ bên thứ ba.

🚀 Tính năng nổi bật & Thành tựu Kỹ thuật

🛡️ Bảo mật & Phân quyền: Xác thực người dùng và phân quyền (Admin, Staff, Customer) sử dụng Spring Security và JWT (JSON Web Token).

🛍️ Thương mại điện tử: Xây dựng giỏ hàng, đặt hàng, quản lý sản phẩm. Thiết kế CSDL chặt chẽ cho các mối quan hệ Nhiều-Nhiều (N-N) thông qua bảng trung gian có thuộc tính (VD: OrderDetails).

📅 Hệ thống Đặt lịch (Service Booking): Áp dụng logic quản lý thời gian khắt khe với Instant/LocalDateTime của Java 8+ để kiểm tra khoảng thời gian trống, ngăn chặn xung đột trùng lịch hẹn.

💳 Thanh toán trực tuyến: Tích hợp cổng thanh toán VNPay, xử lý tạo URL thanh toán an toàn, xác thực chữ ký (Checksum) và cập nhật trạng thái đơn hàng tự động.

🤖 Trợ lý ảo AI (Chatbot): Tích hợp Google Gemini AI API ứng dụng kỹ thuật RAG, cho phép AI tư vấn dịch vụ và sản phẩm dựa trên dữ liệu ngữ cảnh thực tế của cửa hàng.

⚡ Tối ưu hóa Hiệu năng (Performance Optimization): Xử lý triệt để bài toán N+1 Query kinh điển trong Hibernate bằng kỹ thuật JPQL LEFT JOIN FETCH, kết hợp thiết kế Dynamic Query để phục vụ bộ lọc tìm kiếm linh hoạt.

🛠 Công nghệ sử dụng (Tech Stack)

Ngôn ngữ: Java 17

Framework chính: Spring Boot 3 (Spring Web, Spring Security, Spring Data JPA)

Cơ sở dữ liệu: MySQL 8.0

Công cụ khác: Lombok, Maven

Tài liệu API: Swagger UI / OpenAPI 3.0

External APIs: VNPay Payment Gateway, Google Gemini AI

🗄 Cấu trúc thư mục (Project Structure)

Dự án được tổ chức theo kiến trúc phân tầng (Layered Architecture) chuẩn mực:

src/main/java/vn/vuxnye/petcare/
 ├── config/        # Cấu hình Security, Swagger, VNPay, AI
 ├── controller/    # Các API Endpoint (@RestController)
 ├── dto/           # Đối tượng truyền tải dữ liệu (Request/Response)
 ├── entity/        # Các lớp ánh xạ với CSDL (@Entity)
 ├── exception/     # Xử lý lỗi tập trung (GlobalExceptionHandler)
 ├── repository/    # Giao tiếp với CSDL (Kế thừa JpaRepository)
 └── service/       # Chứa logic nghiệp vụ lõi (Business Logic)


⚙️ Hướng dẫn cài đặt & Chạy dự án (Getting Started)

1. Yêu cầu môi trường (Prerequisites)

JDK 17+

Maven 3.6+

MySQL Server 8.0+

2. Cấu hình Cơ sở dữ liệu

Tạo một database trống trong MySQL với tên: petcare_db

Mở file src/main/resources/application.yml và cập nhật thông tin đăng nhập:

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petcare_db?useSSL=false&serverTimezone=UTC
    username: root
    password: <mật_khẩu_mysql_của_bạn>


3. Cấu hình Biến môi trường (Environment Variables)

Để sử dụng các API bên ngoài, bạn cần thiết lập các giá trị sau trong application.yml (hoặc truyền qua biến môi trường):

jwt.secret: Khóa bí mật dùng để tạo token.

vnpay.tmnCode / vnpay.hashSecret: Cấp bởi VNPay.

gemini.api.key: Cấp bởi Google AI Studio.

4. Khởi chạy

Mở Terminal tại thư mục gốc của dự án và chạy lệnh:

mvn spring-boot:run


Ứng dụng sẽ khởi chạy tại cổng mặc định http://localhost:8080. (Hibernate sẽ tự động gen các bảng dựa theo cấu hình spring.jpa.hibernate.ddl-auto=update).

📚 Tài liệu API (Swagger UI)

Sau khi ứng dụng chạy thành công, bạn có thể xem tài liệu API chi tiết và thử nghiệm trực tiếp tại:
👉 http://localhost:8080/swagger-ui/index.html
