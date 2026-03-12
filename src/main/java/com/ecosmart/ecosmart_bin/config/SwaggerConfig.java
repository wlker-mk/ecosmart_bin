// SwaggerConfig.java
package com.ecosmart.ecosmart_bin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("ECOSMART-BIN API")
                        .description("API backend pour la borne intelligente de collecte de plastique avec récompenses")
                        .version("1.0")
                        .contact(new Contact()
                                .name("ECOSMART Team")
                                .email("contact@ecosmart.com")));
    }
}