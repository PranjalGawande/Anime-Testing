    package com.review.anime.Controller;

    import com.fasterxml.jackson.databind.node.ArrayNode;
    import com.fasterxml.jackson.databind.node.ObjectNode;
    import com.review.anime.AnimeApplication;
    import com.review.anime.dto.ExtraDTO;
    import com.review.anime.dto.ReviewDTO;
    import com.review.anime.dto.Token;
    import com.review.anime.entites.Review;
    import com.review.anime.entites.Role;
    import com.review.anime.entites.User;
    import com.review.anime.entites.WatchList;
    import com.review.anime.service.ReviewService;
    import com.review.anime.service.UserService;
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.bind.annotation.*;
    import javax.naming.AuthenticationException;

    import com.fasterxml.jackson.databind.ObjectMapper;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;

    @RestController
    @CrossOrigin(origins = "*")
    @RequestMapping("/")
    public class UserController {

        private static final Logger logger = LogManager.getLogger(UserController.class);

        @Autowired
        private UserService userService;

        @Autowired
        private ReviewService reviewService;

        @Autowired
        private ObjectMapper objectMapper;


        @PostMapping("/login")
        ResponseEntity<Token> userLogin(@RequestBody User user) {
            logger.info("Attempted Login with Email Id: {}", user.getEmail());

            try {
                // Attempt to authenticate the user
                Token token = new Token(userService.authenticate(user));
                return ResponseEntity.ok().body(token);
            } catch (AuthenticationException e) {
                // Log the error and return an unauthorized response
                logger.error("Authentication failed for Email Id: {}", user.getEmail(), e);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // You can return a custom error message if needed
            }
        }


        @PostMapping("/register")
        ResponseEntity<String> userRegister(@RequestBody User user) {
            User exisitingUser = userService.findUserByEmail(user.getEmail());
            if(exisitingUser != null ) {
                logger.info("Attempted to Create an account with existing Email Id: {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username/Email is already taken.");
            }

            if (user.getRole() == Role.ADMIN) {
                logger.info("Attempted to Create an Admin account with Email Id : {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot register as Admin.");
            }

            userService.addUser(user);
            logger.info("New user account is successfully created with Email Id: {}", user.getEmail());

            return ResponseEntity.ok().body("Registered Successfully.");
        }


        @PostMapping("/addComment")
        @PreAuthorize("hasAuthority('user:post')")
        public ResponseEntity<String> addComment(@RequestBody ExtraDTO extra, @AuthenticationPrincipal UserDetails userDetails) {
            String email = userDetails != null ? userDetails.getUsername() : null;

            logger.info("Adding comment for user: {}", email);

            if (email == null) {
                logger.info("User email is null");
                return ResponseEntity.badRequest().body("User email is required");
            }

            if (extra == null || extra.getRating() == null || extra.getComment() == null || extra.getAnimeId() == null) {
                logger.info("Invalid input data: {}", extra);
                return ResponseEntity.badRequest().body("Invalid comment data provided");  // Update this line
            }
            Review review = new Review();
            review.setRating(extra.getRating());
            review.setComment(extra.getComment());
            review.setAnimeId(extra.getAnimeId());

            User user = userService.findUserByEmail(email);
            if (user == null) {
                logger.info("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            review.setUser(user);

            reviewService.saveReview(review);

            logger.info("Comment added successfully for user: {}", email);
            return ResponseEntity.ok().body("Successfully added Comment");
        }


        @GetMapping("/getComment")
        public ResponseEntity<List<ReviewDTO>> getComment(@RequestParam Integer animeId) {
            logger.info("Fetching comments for animeId: {}", animeId);
            List<Review> reviews = reviewService.getReviewOfAnimeId(animeId);
            List<ReviewDTO> reviewDTOS = new ArrayList<>();

            if(reviews.isEmpty()) {
                logger.info("No comments found for animeId: {}", animeId);
                return ResponseEntity.ok(Collections.emptyList());
            }

            for( Review reviewItem : reviews) {
                ReviewDTO reviewDTO = new ReviewDTO(reviewItem);
                reviewDTOS.add(reviewDTO);
            }

            logger.info("Fetched {} comments for animeId: {}", reviewDTOS.size(), animeId);
            return ResponseEntity.ok(reviewDTOS);
        }

        @GetMapping("/getWatchList")
        @PreAuthorize("hasAuthority('user:get')")
        public ResponseEntity<Object> getWatchList(@AuthenticationPrincipal UserDetails userDetails) {
            String email = userDetails.getUsername();
            logger.info("Fetching watchlist for user: {}", email);

            if (userDetails == null) {
                logger.info("User details are null");
                return ResponseEntity.status(401).body("Unauthorized: User details are null");
            }

            User user = userService.findUserByEmail(email);

            if (user == null) {
                logger.info("User not found: {}", email);
                return ResponseEntity.status(404).body("User not found");
            }

            List<WatchList> watchList = user.getWatchLists();

            // Check if the watchlist is empty
            if (watchList == null || watchList.isEmpty()) {
                logger.info("No watchlist data found for user: {}", email);
                return ResponseEntity.ok("No watchlist data found");  // Return a simple message when empty
            }

            // If the watchlist is not empty, continue with the original logic
            ArrayNode dataArray = objectMapper.createArrayNode();

            for (WatchList watchListItem : watchList) {
                ObjectNode animeObject = objectMapper.createObjectNode();
                animeObject.put("animeId", watchListItem.getAnimeId());

                ObjectNode imagesObject = objectMapper.createObjectNode();
                imagesObject.put("image_url", watchListItem.getImageUrl());
                animeObject.set("images", imagesObject);

                animeObject.put("title", watchListItem.getTitle());
                dataArray.add(animeObject);
            }

            ObjectNode watchListJson = objectMapper.createObjectNode();
            watchListJson.set("data", dataArray);

            logger.info("Fetched watchlist for user: {}", email);
            return ResponseEntity.ok(watchListJson);
        }


        @PostMapping("/addWatchList")
        @PreAuthorize("hasAuthority('user:post')")
        public ResponseEntity<String> addWatchList(@RequestBody WatchList watchList,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
            String email = userDetails.getUsername();
            logger.info("Adding to watchlist for user: {}", email);

            if (email == null) {
                logger.info("User email is null");
                return ResponseEntity.badRequest().body("User not found.");
            }

            String result = userService.addWatchedAnimeId(watchList, email);
            logger.info("Added to watchlist for user: {}", email);
            return ResponseEntity.ok().body(result);
        }


        @PostMapping("/deleteWatchList")
        @PreAuthorize("hasAuthority('user:post')")
        public ResponseEntity<String> deleteWatchList(@RequestBody ExtraDTO extraDTO, @AuthenticationPrincipal UserDetails userDetails) {
            String email = userDetails.getUsername();
            logger.info("Deleting from watchlist for user: {}", email);

            if(email == null) {
                logger.info("User email is null");
                return ResponseEntity.badRequest().body("User not found.");
            }

            User user = userService.findUserByEmail(email);
            String result = userService.deleteWatchList(extraDTO.getAnimeId(), user);
            logger.info("Deleted from watchlist for user: {}", email);
            return ResponseEntity.ok().body(result);
        }

        @PostMapping("/changePassword")
        @PreAuthorize("hasAuthority('user:post')")
        public ResponseEntity<String> changePassword(@RequestBody ExtraDTO extraDTO, @AuthenticationPrincipal UserDetails userDetails) {
            String email = userDetails.getUsername();
            logger.info("Changing password for user: {}", email);

            if(email == null) {
                logger.info("User email is null");
                return ResponseEntity.badRequest().body("User not found.");
            }

            User user = userService.findUserByEmail(email);
            if(!userService.verifyCurrentPassword(email, extraDTO.getOldPassword())) {
                logger.info("Current password is incorrect for user: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current password is incorrect");
            }

            userService.updateUser(email, extraDTO.getNewPassword());
            logger.info("Password changed successfully for user: {}", email);
            return ResponseEntity.ok().body("Password changed successfully.");
        }

        @GetMapping("/deleteComment")
        @PreAuthorize("hasAuthority('admin:post')")
        public ResponseEntity<String> deleteComment(@RequestParam Integer commentId, @AuthenticationPrincipal UserDetails userDetails) {
            String email = userDetails.getUsername();
            logger.info("Attempting to delete comment with id: {} by user: {}", commentId, email);

            // Check for invalid commentId
            if (commentId <= 0) {
                logger.error("Invalid comment ID: {}", commentId);
                return ResponseEntity.badRequest().body("Invalid comment ID");  // Add this check
            }

            User user = userService.findUserByEmail(email);

            if (!user.isUserAdmin()) {
                logger.info("User: {} is not admin and cannot delete comments", email);
                return ResponseEntity.badRequest().body("Only Admin is allowed to delete comment.");
            }

            try {
                reviewService.deleteComment(commentId);
                logger.info("Comment with id: {} deleted successfully by user: {}", commentId, email);
                return ResponseEntity.ok().body("Comment deleted successfully.");
            } catch (IllegalArgumentException e) {
                logger.error("Comment with id: {} not found", commentId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
            } catch (Exception e) {
                logger.error("Error deleting comment with id: {}", commentId, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
            }
        }


    }