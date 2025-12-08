package www.stock.az.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI warehousesManagementOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8087");
        devServer.setDescription("Development server");

        Server prodServer = new Server();
        prodServer.setUrl("https://warehouses-api.stock.az");
        prodServer.setDescription("Production server");

        Contact contact = new Contact();
        contact.setEmail("info@stock.az");
        contact.setName("Stock.az Team");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Warehouses Management API")
                .version("1.0.0")
                .contact(contact)
                .description("Warehouses Management Microservice API. " +
                        "This service handles brands, categories, products, warehouses, and stock management.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
