package com.microservice.pays.configuration;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(
        info = @Info(
                title = "Microservice Pays",
                description = "this microservice is in charge of handling the pays ",
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
                        url = "http://localhost:8086"
                ),
                @Server(
                        description = "PROD SERVER",
                        url = "http://localhost:8086"
                )
        }

)

public class SwaggerConfig {
}
