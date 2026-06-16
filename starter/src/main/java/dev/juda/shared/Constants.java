package dev.juda.shared;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int MAX_PAGE_SIZE = 100;

    public static final int DEFAULT_PAGE_NUMBER = 0;

    public static final long CACHE_TTL_SECONDS = 600;

    public static final String REDIS_CACHE_MANAGER = "redisCacheManager";

    public static final String ORDER_EVENTS_TOPIC = "order-events";

    public static final String PAYMENT_EVENTS_TOPIC = "payment-events";

    public static final String ORDER_SERVICE_GROUP = "order-service-group";

    public static final String PAYMENT_SERVICE_GROUP = "payment-service-group";

    public static final String KEYCLOAK_SERVER_URL = "http://localhost:8080";

    public static final String KEYCLOAK_REALM = "ecommerce";

    public static final String KEYCLOAK_CLIENT_ID = "ecommerce-client";
}
