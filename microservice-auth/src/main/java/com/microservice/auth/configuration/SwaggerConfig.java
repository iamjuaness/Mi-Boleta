package com.microservice.auth.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "Microservice Auth",
                description = "this microservice is in charge of handling security-related requests ",
                contact = @Contact(
                        name = "Juan Esteban Ramírez Tabares",
                        url = "www.linkedin.com/in/juan-esteban-ramirez-tabares-1a85b3275",
                        email = "juane.ramirezt@gmail.com"
                ),
                version = "1.0.0"

        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:8081"
                ),
                @Server(
                        description = "PROD SERVER",
                        url = "http://localhost:8081"
                )
        },
        security = @SecurityRequirement(
                name = "Security Token"
        )
)
@SecurityScheme(
        name = "Security Token",
        description = "Access token for api manage-users",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}
