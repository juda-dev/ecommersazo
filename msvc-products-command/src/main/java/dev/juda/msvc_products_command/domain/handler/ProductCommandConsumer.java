package dev.juda.msvc_products_command.domain.handler;

import dev.juda.libs_msvc_commons.domain.messaging.Command;
import dev.juda.libs_msvc_commons.domain.messaging.Reply;
import dev.juda.msvc_products_command.domain.dto.ProductDto;
import dev.juda.msvc_products_command.domain.service.ProductService;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
public class ProductCommandConsumer {

    private static final Logger log = LoggerFactory.getLogger(
        ProductCommandConsumer.class
    );
    private final ProductService service;

    public ProductCommandConsumer(ProductService service) {
        this.service = service;
    }

    @Bean
    public Function<
        Message<Command<ProductDto>>,
        Message<Reply<?>>
    > handleCommands() {
        return msg -> {
            Command<ProductDto> cmd = msg.getPayload();
            String type = cmd.type() != null ? cmd.type().toUpperCase() : "";
            String correlationId = msg
                .getHeaders()
                .get("correlationId", String.class);

            log.info("Procesando {} | correlationId={}", type, correlationId);

            Reply<?> reply = switch (type) {
                case "CREATE" -> create(cmd);
                case "UPDATE" -> update(cmd);
                case "DELETE" -> delete(cmd);
                default -> new Reply<>(
                    "ERROR",
                    "Unrecognized command: " + type,
                    null
                );
            };

            @SuppressWarnings("unchecked")
            Message<Reply<?>> result = (Message<Reply<?>>) (Message<
                ?
            >) MessageBuilder.withPayload(reply)
                .setHeader("correlationId", correlationId)
                .build();
            return result;
        };
    }

    private Reply<ProductDto> create(Command<ProductDto> cmd) {
        if (cmd.body() == null) return new Reply<>("ERROR", "Body vacío", null);
        try {
            ProductDto created = service.create(cmd.body());
            log.info("Product created: id={}", created.id());
            return new Reply<>("SUCCESS", "Product created", created);
        } catch (Exception e) {
            log.error("Error creating product", e);
            return new Reply<>("ERROR", e.getMessage(), null);
        }
    }

    private Reply<ProductDto> update(Command<ProductDto> cmd) {
        if (cmd.id() == null || cmd.body() == null) return new Reply<>(
            "ERROR",
            "ID and body required",
            null
        );
        try {
            ProductDto updated = service.update(cmd.id(), cmd.body());
            return new Reply<>("SUCCESS", "Product updated", updated);
        } catch (Exception e) {
            log.error("Error updated product", e);
            return new Reply<>("ERROR", e.getMessage(), null);
        }
    }

    private Reply<ProductDto> delete(Command<ProductDto> cmd) {
        if (cmd.id() == null) return new Reply<>("ERROR", "ID required", null);
        try {
            service.delete(cmd.id());
            return new Reply<>("SUCCESS", "Product deleted", null);
        } catch (Exception e) {
            log.error("Error deleted product", e);
            return new Reply<>("ERROR", e.getMessage(), null);
        }
    }
}
