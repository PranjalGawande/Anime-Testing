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

	@InjectMocks
	private AnimeApplication.AdminIntiailizer adminIntiailizer;

	@BeforeEach
	void setUp() {
		adminInitializer = new AnimeApplication.AdminIntiailizer(userService);
		animeApplication = new AnimeApplication();
	}

	@Test
	void contextLoads() {
	}

	// RestTemplate Tests
	@Test
	void testRestTemplate() {
		RestTemplate expectedRestTemplate = new RestTemplate();
		when(restTemplateBuilder.build()).thenReturn(expectedRestTemplate);

		RestTemplate actualRestTemplate = animeApplication.restTemplate(restTemplateBuilder);

		assertNotNull(actualRestTemplate);
		verify(restTemplateBuilder).build();
	}

	@Test
	void testRestTemplateNotNull() {
		when(restTemplateBuilder.build()).thenReturn(new RestTemplate());

		RestTemplate restTemplate = animeApplication.restTemplate(restTemplateBuilder);

		assertNotNull(restTemplate);
	}

	// Admin Initialization Tests
	@Test
	void testInitNewAdmin() throws Exception {
		when(userService.getAdminList()).thenReturn(Collections.emptyList());

		adminInitializer.run();

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
	void testInitExistingAdmin() throws Exception {
		List<User> existingAdmins = new ArrayList<>();
		existingAdmins.add(new User());
		when(userService.getAdminList()).thenReturn(existingAdmins);

		adminInitializer.run();

		verify(userService).getAdminList();
		verify(userService, never()).addUser(any(User.class));
	}

	@Test
	void testAdminFields() throws Exception {
		when(userService.getAdminList()).thenReturn(Collections.emptyList());

		adminInitializer.run();

		verify(userService).addUser(userCaptor.capture());
		User adminUser = userCaptor.getValue();

		assertAll(
				() -> assertEquals("admin@gmail.com", adminUser.getEmail()),
				() -> assertEquals("Admin", adminUser.getPassword()),
				() -> assertEquals("Admin", adminUser.getName()),
				() -> assertEquals(Role.ADMIN, adminUser.getRole()),
				() -> assertTrue(adminUser.isStatus())
		);
	}

	@Test
	void testAdminTransaction() throws Exception {
		when(userService.getAdminList()).thenReturn(Collections.emptyList());
		doThrow(new RuntimeException("Database error")).when(userService).addUser(any(User.class));

		assertThrows(RuntimeException.class, () -> adminInitializer.run());
	}

	@Test
	void testMultipleAdminChecks() throws Exception {
		when(userService.getAdminList())
				.thenReturn(Collections.emptyList())
				.thenReturn(Collections.singletonList(new User()));

		adminInitializer.run();
		adminInitializer.run();

		verify(userService, times(2)).getAdminList();
		verify(userService, times(1)).addUser(any(User.class));
	}

	// Application Tests
	@Test
	void testMain() {
		assertDoesNotThrow(() -> AnimeApplication.main(new String[]{}));
	}

	// Constructor Tests
	@Test
	void testInitializerConstructor() {
		AnimeApplication.AdminIntiailizer initializer = new AnimeApplication.AdminIntiailizer(userService);
		assertNotNull(initializer);
	}

	@Test
	void testNullUserService() {
		NullPointerException exception = assertThrows(NullPointerException.class,
				() -> new AnimeApplication.AdminIntiailizer(null));

		assertEquals("UserService cannot be null", exception.getMessage());
	}
}