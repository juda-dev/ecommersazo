package dev.juda.msvc_products_api.domain.service.impl;

import dev.juda.libs_msvc_commons.domain.enums.CommandType;
import dev.juda.libs_msvc_commons.domain.enums.ReplyStatus;
import dev.juda.libs_msvc_commons.domain.exception.TimeoutException;
import dev.juda.libs_msvc_commons.domain.messaging.Command;
import dev.juda.libs_msvc_commons.domain.messaging.Reply;
import dev.juda.libs_msvc_commons.domain.messaging.ReplyInbox;
import dev.juda.msvc_products_api.domain.dto.request.CreateProductRequestDto;
import dev.juda.msvc_products_api.domain.dto.request.UpdateProductRequestDto;
import dev.juda.msvc_products_api.domain.service.ProductCommandService;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class ProductCommandServiceImpl implements ProductCommandService {

    private static final Logger log = LoggerFactory.getLogger(
        ProductCommandServiceImpl.class
    );
    private static final String OUT = "commands-out-0";

    private final StreamBridge bridge;
    private final ReplyInbox inbox;

    public ProductCommandServiceImpl(StreamBridge bridge, ReplyInbox inbox) {
        this.bridge = bridge;
        this.inbox = inbox;
    }

    @Override
    public Reply<?> sendCreateAndAwait(
        CreateProductRequestDto dto,
        Duration timeout
    ) {
        return send(new Command<>(CommandType.CREATE, null, dto), timeout);
    }

    @Override
    public Reply<?> sendDeleteAndAwait(Long id, Duration timeout) {
        return send(new Command<>(CommandType.DELETE, id, null), timeout);
    }

    @Override
    public Reply<?> sendUpdateAndAwait(
        Long id,
        UpdateProductRequestDto dto,
        Duration timeout
    ) {
        return send(new Command<>(CommandType.UPDATE, id, dto), timeout);
    }

    private Reply<?> send(Command<?> cmd, Duration timeout) {
        String cid = UUID.randomUUID().toString();
        log.debug("Enviando {} | correlationId={}", cmd.type(), cid);

        CompletableFuture<Reply<?>> future = inbox.register(cid);

        @SuppressWarnings("unchecked")
        Message<Command<?>> msg = (Message<Command<?>>) (Message<
            ?
        >) MessageBuilder.withPayload(cmd)
            .setHeader("correlationId", cid)
            .build();

        if (!bridge.send(OUT, msg)) {
            inbox.complete(
                cid,
                new Reply<>(ReplyStatus.ERROR, "Kafka not available", null)
            );
            throw new IllegalStateException("Kafka could not be sent");
        }

        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            inbox.complete(cid, null);
            throw new TimeoutException("Timeout para " + cmd.type());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TimeoutException("Interrumpido");
        } catch (ExecutionException e) {
            throw new TimeoutException("Error: " + e.getMessage());
        }
    }
}
