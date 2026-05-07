package dev.juda.libs_msvc_commons.domain.messaging;

public record Reply<T>(String status, String message, T body) {}
