package com.microservice_shopping.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "API SHOPPING",
                description = "this microservice is in charge of handling everything that has to do with the purchasing process, such as creating purchase orders, providing them, updating them, and so on.",
                contact = @Contact(
                        name = "Juan Esteban Ramírez Tabares",
                        url = "https://github.com/esteban2505J",
                        email = "juane.ramirezt@gmail.com"
                ),
                version = "1.0.0"
        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:8086"
                ),
                @Server(
                        description = "PROD SERVER",
                        url = "http://localhost:8086"
                )
        }
)

public class SwaggerConfig {}
