package com.md.service_request_api.service.util.sync;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class APIKeyService {

    @Value("${external.api.identity:test}")
    private String apiKey;

    @Value("${external.api.secret:test}")
    private String secret;

    public String getPublicKey() {
        return apiKey;
    }

    public String getPrivateKey() {
        return secret;
    }
}
