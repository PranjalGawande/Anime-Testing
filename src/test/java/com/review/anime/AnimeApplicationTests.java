package com.review.anime;

import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import com.review.anime.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
		import static org.mockito.Mockito.*;

@SpringBootTest
class AnimeApplicationTests {

	@Mock
	private UserService userService;

	@Mock
	private RestTemplateBuilder restTemplateBuilder;

	@Captor
	private ArgumentCaptor<User> userCaptor;

	private AnimeApplication.AdminIntiailizer adminInitializer;
	private AnimeApplication animeApplication;

//	private static final Logger logger = LogManager.getLogger(AnimeApplicationTests.class);


	@InjectMocks
	private AnimeApplication.AdminIntiailizer adminIntiailizer;

//	@BeforeEach
//	void setUp() {
//		MockitoAnnotations.openMocks(this); // Initializes the mocks
//	}

	@Test
	void contextLoads() {
		// This test ensures the Spring Boot context loads correctly
	}
	@BeforeEach
	void setUp() {
		adminInitializer = new AnimeApplication.AdminIntiailizer(userService);
		animeApplication = new AnimeApplication();
	}

	@Test
	void testRestTemplateBean() {
		// Arrange
		RestTemplate expectedRestTemplate = new RestTemplate();
		when(restTemplateBuilder.build()).thenReturn(expectedRestTemplate);

		// Act
		RestTemplate actualRestTemplate = animeApplication.restTemplate(restTemplateBuilder);

		// Assert
		assertNotNull(actualRestTemplate, "RestTemplate should not be null");
		verify(restTemplateBuilder).build();
	}

	@Test
	void testRestTemplateBeanNotNull() {
		// Arrange
		when(restTemplateBuilder.build()).thenReturn(new RestTemplate());

		// Act
		RestTemplate restTemplate = animeApplication.restTemplate(restTemplateBuilder);

		// Assert
		assertNotNull(restTemplate, "RestTemplate should not be null");
	}

	@Test
	void testAdminInitialization_WhenNoAdminExists() throws Exception {
		// Arrange
		when(userService.getAdminList()).thenReturn(Collections.emptyList());

		// Act
		adminInitializer.run();

		// Assert
		verify(userService).getAdminList();
		verify(userService).addUser(userCaptor.capture());

		User capturedUser = userCaptor.getValue();
		assertEquals("admin@gmail.com", capturedUser.getEmail());
		assertEquals("Admin", capturedUser.getPassword());
		assertEquals("Admin", capturedUser.getName());
		assertEquals(Role.ADMIN, capturedUser.getRole());
		assertTrue(capturedUser.isStatus());
	}

	@Test
	void testAdminInitialization_WhenAdminAlreadyExists() throws Exception {
		// Arrange
		List<User> existingAdmins = new ArrayList<>();
		existingAdmins.add(new User());
		when(userService.getAdminList()).thenReturn(existingAdmins);

		// Act
		adminInitializer.run();

		// Assert
		verify(userService).getAdminList();
		verify(userService, never()).addUser(any(User.class));
	}

	@Test
	void testAdminInitialization_VerifyAllUserFields() throws Exception {
		// Arrange
		when(userService.getAdminList()).thenReturn(Collections.emptyList());

		// Act
		adminInitializer.run();

		// Assert
		verify(userService).addUser(userCaptor.capture());
		User adminUser = userCaptor.getValue();

		// Verify each field was set correctly
		assertAll(
				() -> assertEquals("admin@gmail.com", adminUser.getEmail(), "Email should be set correctly"),
				() -> assertEquals("Admin", adminUser.getPassword(), "Password should be set correctly"),
				() -> assertEquals("Admin", adminUser.getName(), "Name should be set correctly"),
				() -> assertEquals(Role.ADMIN, adminUser.getRole(), "Role should be set to ADMIN"),
				() -> assertTrue(adminUser.isStatus(), "Status should be set to true")
		);
	}

	@Test
	void testAdminInitialization_VerifyTransactional() throws Exception {
		// Arrange
		when(userService.getAdminList()).thenReturn(Collections.emptyList());
		doThrow(new RuntimeException("Database error")).when(userService).addUser(any(User.class));

		// Act & Assert
		assertThrows(RuntimeException.class, () -> adminInitializer.run(),
				"Should throw exception when database operation fails");
	}

	@Test
	void testAdminInitialization_CheckAdminListMultipleTimes() throws Exception {
		// Arrange
		when(userService.getAdminList())
				.thenReturn(Collections.emptyList())
				.thenReturn(Collections.singletonList(new User()));

		// Act
		adminInitializer.run();
		adminInitializer.run();

		// Assert
		verify(userService, times(2)).getAdminList();
		verify(userService, times(1)).addUser(any(User.class));
	}

	@Test
	void testMain() {
		// This test verifies that the main method can be called without throwing exceptions
		assertDoesNotThrow(() -> AnimeApplication.main(new String[]{}),
				"Main method should execute without throwing exceptions");
	}

	@Test
	void testAdminInitializer_Constructor() {
		// Arrange & Act
		AnimeApplication.AdminIntiailizer initializer = new AnimeApplication.AdminIntiailizer(userService);

		// Assert
		assertNotNull(initializer, "AdminInitializer should be created successfully");
	}

	@Test
	void testAdminInitialization_NullUserService() {
		// Act & Assert
		NullPointerException exception = assertThrows(NullPointerException.class,
				() -> new AnimeApplication.AdminIntiailizer(null),
				"Should throw NullPointerException when UserService is null");

		// Verify the exception message
		assertEquals("UserService cannot be null", exception.getMessage(),
				"Exception message should indicate that UserService cannot be null");
	}
}