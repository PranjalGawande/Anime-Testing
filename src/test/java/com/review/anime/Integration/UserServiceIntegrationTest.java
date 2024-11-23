//package com.review.anime.Integration;
//
//import com.review.anime.entites.User;
//import com.review.anime.repository.UserRepository;
//import com.review.anime.security.JwtService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import com.review.anime.service.UserService;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.mockito.Mockito.*;
//public class UserServiceIntegrationTest {
//    @InjectMocks
//    private UserService userService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JwtService jwtService;
//
//    @Test
//    void testAddUser() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password");
//
//        userService.addUser(user);
//
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    void testAuthenticate() {
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("password");
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
//        when(jwtService.generateToken(user)).thenReturn("jwt_token");
//
//        String token = userService.authenticate(user);
//
//        assert token.equals("jwt_token");
//    }
//
//}
