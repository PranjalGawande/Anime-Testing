package com.review.anime;

import com.review.anime.entites.Role;
import com.review.anime.entites.User;
import com.review.anime.service.UserService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AnimeApplication {

	private static final Logger logger = LogManager.getLogger(AnimeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AnimeApplication.class, args);
	}

	// Making the AdminIntiailizer class static so it can be instantiated for tests
	@Component
	public static class AdminIntiailizer implements CommandLineRunner {

		private final UserService userService;

		@Autowired
		public AdminIntiailizer(UserService userService) {
			this.userService = userService;
		}

		@Transactional
		@Override
		public void run(String... args) throws Exception {

			// Check if there are any admins, if so, skip creating a new one
			if (!userService.getAdminList().isEmpty()) return;

			// Creating a new admin user
			User user = new User();
			user.setEmail("admin@gmail.com");
			user.setPassword("Admin");
			user.setName("Admin");
			user.setRole(Role.ADMIN);
			user.setStatus(true);

			// Adding the new admin user to the database
			userService.addUser(user);
			logger.info("Initializing default Admin account with Email ID: {}.", user.getEmail());
		}
	}
}
