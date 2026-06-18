package configurationservice.configuration.application;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class BroadcastService {

    private static final Logger log = LoggerFactory.getLogger(
        BroadcastService.class
    );

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        emitters.add(emitter);
        log.info("New SSE subscriber. Total subscribers: {}", emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info(
                "SSE subscriber disconnected. Total subscribers: {}",
                emitters.size()
            );
        });

        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.info(
                "SSE subscriber timed out. Total subscribers: {}",
                emitters.size()
            );
        });

        emitter.onError(ex -> {
            emitters.remove(emitter);
            log.info(
                "SSE subscriber error. Total subscribers: {}",
                emitters.size()
            );
        });

        try {
            emitter.send(
                SseEmitter.event()
                    .name("connected")
                    .data(
                        "{\"message\": \"Subscribed to configuration changes\"}"
                    )
            );
        } catch (IOException e) {
            emitters.remove(emitter);
            log.error("Failed to send initial SSE event", e);
        }

        return emitter;
    }

    public void broadcast(ConfigurationEvent event) {
        log.debug(
            "Broadcasting event: {} for key: {}",
            event.type(),
            event.key()
        );

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(
                    SseEmitter.event().name("configuration-changed").data(event)
                );
            } catch (IOException e) {
                emitters.remove(emitter);
                log.debug("Removed dead SSE subscriber");
            }
        }
    }

    public int getSubscriberCount() {
        return emitters.size();
    }
}
