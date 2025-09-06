package com.ntt.lms.controller;

import com.ntt.lms.dto.UserLoginRequest;
import com.ntt.lms.dto.UserReponseDTO;
import com.ntt.lms.dto.UserSignUpRequest;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.service.UserService;
import com.ntt.lms.utils.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@ModelAttribute @Valid UserSignUpRequest userSignUpRequest) {
        try {
            this.userService.createUser(userSignUpRequest);
            return ResponseEntity.ok("Đăng ký thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        try {
            Users user = this.userService.findByEmail(userLoginRequest.getEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email không chính xác!");
            }

            boolean isPasswordValid = this.userService.validatePassword(userLoginRequest.getPassword(), user.getPassword());
            if (!isPasswordValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu chưa chính xác!");
            }

            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getUserType().getUserTypeName(),
                    user.getEmail(),
                    user.getAvatar()
            );


            UserReponseDTO userResponse = new UserReponseDTO();
            userResponse.setUserId(user.getUserId());
            userResponse.setEmail(user.getEmail());
            userResponse.setAvatar(user.getAvatar());
            userResponse.setUserTypeId(user.getUserType().getUserTypeId());
            userResponse.setRole(user.getUserType().getUserTypeName());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", userResponse


            ));

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu hoặc email không hợp lệ");
        }
    }




//    @PostMapping("/google")
//    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> payload) {
//        String idToken = payload.get("idToken");
//
//        try {
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                    new NetHttpTransport(), new JacksonFactory())
//                    .setAudience(Collections.singletonList("YOUR_GOOGLE_CLIENT_ID"))
//                    .build();
//
//            GoogleIdToken googleIdToken = verifier.verify(idToken);
//            if (googleIdToken == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
//            }
//
//            GoogleIdToken.Payload googlePayload = googleIdToken.getPayload();
//            String email = googlePayload.getEmail();
//
//            // Tìm hoặc tạo tài khoản người dùng
//            Users user = userService.findOrCreateGoogleUser(email);
//
//            // Sinh JWT nội bộ
//            String token = jwtService.generateToken(email, user.getUserType().getUserTypeName());
//
//            return ResponseEntity.ok(Map.of(
//                    "token", token,
//                    "email", user.getEmail(),
//                    "role", user.getUserType().getUserTypeName()
//            ));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google login failed");
//        }
//    }

}
