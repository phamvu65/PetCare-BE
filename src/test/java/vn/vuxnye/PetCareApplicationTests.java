package vn.vuxnye;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.vuxnye.controller.*;

@SpringBootTest
class PetCareApplicationTests {

	@InjectMocks
	private UserController userController;

	@InjectMocks
	private AuthenticationController authenticationController;

	@InjectMocks
	private EmailController emailController;

	@InjectMocks
	private PetController petController;

	@InjectMocks
	private AppointmentController appointmentController;

	@InjectMocks
	private OrderController orderController;

	@InjectMocks
	private CouponController couponController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(userController);
		Assertions.assertNotNull(authenticationController);
		Assertions.assertNotNull(emailController);
		Assertions.assertNotNull(petController);
		Assertions.assertNotNull(appointmentController);
		Assertions.assertNotNull(orderController);
		Assertions.assertNotNull(couponController);
	}

}
