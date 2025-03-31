package com.genysyxtechnologies.service_request_system.dtos.security;

import java.io.Serializable;

import com.genysyxtechnologies.service_request_system.model.User;

public record LoginResponse(
    String token,
    User user
) implements Serializable {

}
