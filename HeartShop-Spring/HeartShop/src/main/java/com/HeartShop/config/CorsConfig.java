package com.HeartShop.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://127.0.0.1:3000,http://127.0.0.1:5173}")
    private String allowedOriginsRaw;

    @Bean
    public CorsFilter corsFilter() {
        List<String> allowedOrigins = Arrays.asList(allowedOriginsRaw.split(","));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 綠界 callback 端點：允許任何來源（不需帶 Cookie）
        CorsConfiguration ecpayConfig = new CorsConfiguration();
        ecpayConfig.setAllowedOriginPatterns(List.of("*"));
        ecpayConfig.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        ecpayConfig.setAllowedHeaders(List.of("*"));
        ecpayConfig.setAllowCredentials(false);
        source.registerCorsConfiguration("/orders/ecpay/**", ecpayConfig);

        // 一般前端 API
        CorsConfiguration apiConfig = new CorsConfiguration();
        apiConfig.setAllowedOriginPatterns(allowedOrigins);
        apiConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        apiConfig.setAllowedHeaders(List.of("*"));
        apiConfig.setAllowCredentials(true);
        apiConfig.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", apiConfig);

        return new CorsFilter(source);
    }
}