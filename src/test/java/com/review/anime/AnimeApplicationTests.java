package com.review.anime;

import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import com.review.anime.service.UserService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnimeApplicationTests {

	private static final Logger logger = LogManager.getLogger(AnimeApplicationTests.class);

	@Mock
	private UserService userService;

	@InjectMocks
	private AnimeApplication.AdminIntiailizer adminIntiailizer;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Initializes the mocks
	}

	@Test
	void contextLoads() {
		// This test ensures the Spring Boot context loads correctly
	}

//	@Test
//	void testAdminUserCreationWhenNoAdminsExist() throws Exception {
//		// Mocking the behavior of getAdminList() to return an empty list
//		when(userService.getAdminList()).thenReturn(Collections.emptyList());
//
//		// Run the initializer
//		adminIntiailizer.run();
//
//		// Verifying that addUser was called and capturing the user parameter
//		verify(userService, times(1)).addUser(userCaptor.capture());
//
//		// You can then assert on the captured user
//		User capturedUser = userCaptor.getValue();
//		assertNotNull(capturedUser);
//		assertEquals("admin@gmail.com", capturedUser.getEmail());
//	}

	@Test
	void testAdminUserExistsAndNoNewAdminCreated() throws Exception {
		// Given: An admin user already exists
		User existingAdmin = new User();
		existingAdmin.setEmail("admin@gmail.com");
		existingAdmin.setPassword("Admin");
		existingAdmin.setName("Admin");
		existingAdmin.setRole(Role.ADMIN);
		existingAdmin.setStatus(true);
		when(userService.getAdminList()).thenReturn(java.util.Collections.singletonList(existingAdmin));

		// When: The run method is invoked
		adminIntiailizer.run();

		// Then: The addUser method should NOT be called since an admin already exists
		verify(userService, times(0)).addUser(any(User.class)); // Verify that addUser is not called
	}

//	@Test
//	void testLoggingWhenCreatingAdmin() throws Exception {
//		// Mocking the behavior of getAdminList() to return an empty list
//		when(userService.getAdminList()).thenReturn(Collections.emptyList());
//
//		// Run the initializer
//		adminIntiailizer.run();
//
//		// Verifying that addUser was called
//		verify(userService, times(1)).addUser(any(User.class));
//	}

	@Test
	void testLoggingWhenAdminExists() throws Exception {
		// Given: An admin already exists
		User existingAdmin = new User();
		existingAdmin.setEmail("admin@gmail.com");
		existingAdmin.setPassword("Admin");
		existingAdmin.setName("Admin");
		existingAdmin.setRole(Role.ADMIN);
		existingAdmin.setStatus(true);
		when(userService.getAdminList()).thenReturn(java.util.Collections.singletonList(existingAdmin));

		// When: The run method is invoked
		adminIntiailizer.run();

		// Then: Check that the logger was NOT called to log the admin creation
		verify(userService, times(0)).addUser(any(User.class)); // Ensure addUser is not called
	}
}



