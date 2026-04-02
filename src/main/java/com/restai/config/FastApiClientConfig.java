package com.restai.config;
import java.net.http.HttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FastApiClientConfig {
	@Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    @Bean
    public RestClient fastApiClient(RestClient.Builder builder) {
    	 HttpClient httpClient = HttpClient.newBuilder()
    	            .version(HttpClient.Version.HTTP_1_1)
    	            .build();
        return builder
                .baseUrl(fastApiBaseUrl)
                .requestFactory(new org.springframework.http.client.JdkClientHttpRequestFactory(httpClient))
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
