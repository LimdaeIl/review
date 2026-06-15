package com.app.backend.auth.presentation;

import com.app.backend.auth.application.SignupService;
import com.app.backend.auth.application.result.SignupResult;
import com.app.backend.auth.presentation.request.SignupRequest;
import com.app.backend.auth.presentation.response.SignupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
           @Valid @RequestBody SignupRequest request
    ) {
        SignupResult result = signupService.signup(request.toCommand());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SignupResponse.from(result));
    }

}
