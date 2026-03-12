package com.ecosmart.ecosmart_bin.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("ECOSMART-BIN API")
                        .description("API pour la borne intelligente de collecte plastique")
                        .version("1.0"));
    }
}