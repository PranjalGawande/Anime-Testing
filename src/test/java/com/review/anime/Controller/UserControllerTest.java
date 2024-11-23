//package com.review.anime.Controller;
//
//import com.review.anime.Controller.UserController;
//import com.review.anime.entites.Role;
//import com.review.anime.entites.User;
//import com.review.anime.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(UserController.class)
//public class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private UserService userService;
//
////    @Test
////    void testGetAdminList() throws Exception {
////        User admin = new User(1, "Admin", "admin@test.com", "password", Role.ADMIN, true, null, null);
////        when(userService.getAdminList()).thenReturn(List.of(admin));
////
////        mockMvc.perform(get("/api/admins"))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$[0].email").value("admin@test.com"));
////    }
//
//    @Test
//    void testAddUser() throws Exception {
//        User user = new User(null, "User", "user@test.com", "password", Role.USER, true, null, null);
//        doNothing().when(userService).addUser(any());
//
//        mockMvc.perform(post("/api/user")
//                        .contentType("application/json")
//                        .content("{\"name\": \"User\", \"email\": \"user@test.com\", \"password\": \"password\", \"role\": \"USER\"}"))
//                .andExpect(status().isOk());
//    }
//}