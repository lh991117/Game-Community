package com.example.gamecommunity.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupResponse {

    private String nickname;
    private String roles;
}
