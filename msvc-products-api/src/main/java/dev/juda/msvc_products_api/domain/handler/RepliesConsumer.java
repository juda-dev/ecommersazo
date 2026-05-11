package dev.juda.msvc_products_api.domain.handler;

import dev.juda.libs_msvc_commons.domain.messaging.Reply;
import dev.juda.libs_msvc_commons.domain.messaging.ReplyInbox;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
public class RepliesConsumer {

    private static final Logger log = LoggerFactory.getLogger(
        RepliesConsumer.class
    );
    private final ReplyInbox inbox;

    public RepliesConsumer(ReplyInbox inbox) {
        this.inbox = inbox;
    }

    @Bean
    public Consumer<Message<Reply<?>>> handleReplies() {
        return msg -> {
            String cid = msg.getHeaders().get("correlationId", String.class);
            if (cid == null) {
                log.warn("Reply without correlationId. It is ignored.");
                return;
            }
            log.debug("Reply received | correlationId={}", cid);
            inbox.complete(cid, msg.getPayload());
        };
    }
}
