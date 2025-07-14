package com.md.service_request_api.service.util.sync;

import com.md.service_request_api.exceptions.SynchronizationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>Class for handling fetching of data and conversion</p>
 * @param <T> The type to expect from network
 * @param <R> The type to convert to by converter
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class NetworkRequest<T,R> {
    private final URLUtil urlUtil;

    /**
     * <p>Reads data from network</p>
     * @param link the path to fetch from
     * @param params the parameters to send along
     * @param returnType the return type
     * @param bodyConverter function to convert {@link T} to {@link R}
     * @param exceptionHandler function to handle exception
     * @return {@link R}
     */
    public R makeRequest(String link, Map<String, String> params, HttpResponse.BodyHandler<T> returnType, Function<T, R> bodyConverter, Runnable exceptionHandler, APIKeyService apiKeyService){
        // build url
        if(!params.isEmpty()){
            var url = new StringBuilder();
            for (var ks : params.entrySet()){
                url.append("&").append(ks.getKey()).append("=").append(URLEncoder.encode(ks.getValue(), StandardCharsets.UTF_8));
            }

            // replace first & with ?
            url.replace(0,1, "?");
            link = link + url;
        }
        // make http request
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlUtil.build(link)))
                    .header("Content-Type", "application/json")
                    .header("Identity", apiKeyService.getPublicKey())
                    .header("Secret", apiKeyService.getPrivateKey())
                    .GET()
                    .build();

            log.info(request.uri().toString());
            var response = client.send(request, returnType);
            if (response.statusCode() == 200) {
                return bodyConverter.apply(response.body());
            } else {
                log.error("Failed to fetch data from {}: Status Code {}, Body: {}", link, response.statusCode(), response.body());
                if (response.body().toString().startsWith("{")) {
                    var js = new JSONObject(response.body().toString());
                    throw new SynchronizationException("An error has occurred: " + js.getString("message"), response.statusCode());
                }
                throw new SynchronizationException("An error has occurred: " + response.statusCode(), response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
