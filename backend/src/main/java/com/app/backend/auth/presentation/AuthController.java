package com.app.backend.auth.presentation;

import com.app.backend.auth.application.SignupService;
import com.app.backend.auth.application.result.SignupResult;
import com.app.backend.auth.presentation.request.SignupRequest;
import com.app.backend.auth.presentation.response.SignupResponse;
import com.app.backend.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public CommonResponse<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResult result = signupService.signup(request.toCommand());

        return CommonResponse.created(
                "인증/인가: 회원 가입이 완료되었습니다.",
                SignupResponse.from(result)
        );
    }

}
