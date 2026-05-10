package dev.juda.gateway_server.domain.filters;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(
        ServerWebExchange exchange,
        GatewayFilterChain chain
    ) {
        var headerId = exchange.getRequest().getHeaders().getFirst(HEADER);
        var correlationId = (headerId == null || headerId.isBlank())
            ? UUID.randomUUID().toString()
            : headerId;

        var mutated = exchange
            .mutate()
            .request(b -> b.header(HEADER, correlationId))
            .build();

        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
