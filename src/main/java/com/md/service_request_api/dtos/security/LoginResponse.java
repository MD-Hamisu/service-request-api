package com.md.service_request_api.dtos.security;

import java.io.Serializable;

import com.md.service_request_api.model.User;

public record LoginResponse(
    String token,
    User user
) implements Serializable {

}
