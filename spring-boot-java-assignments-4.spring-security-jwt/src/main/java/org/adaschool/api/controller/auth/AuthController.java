package org.adaschool.api.controller.auth;

import org.adaschool.api.data.user.UserEntity;
import org.adaschool.api.data.user.UserService;
import org.adaschool.api.exception.InvalidCredentialsException;
import org.adaschool.api.security.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    private final JwtUtil jwtUtil;


    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        String username=loginDto.getUsername();
        String password=loginDto.getPassword();

        Optional<UserEntity> userOptional=userService.findByEmail(username);

        if (userOptional.isPresent()){
            UserEntity user =userOptional.get();

            if (BCrypt.checkpw(password,user.getPasswordHash())){
                TokenDto tokenDto= jwtUtil.generateToken(username,user.getRoles());
                return ResponseEntity.ok(tokenDto);
            }else {
                throw new InvalidCredentialsException();
            }
        }else {
            throw new InvalidCredentialsException();
        }
    }
}

