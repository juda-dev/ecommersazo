package dev.juda.libs_msvc_commons.domain.messaging;

public record Command<T>(String type, Long id, T body) {}
