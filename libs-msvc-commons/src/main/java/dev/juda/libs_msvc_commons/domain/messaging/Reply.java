package dev.juda.libs_msvc_commons.domain.messaging;

import dev.juda.libs_msvc_commons.domain.enums.ReplyStatus;

public record Reply<T>(ReplyStatus status, String message, T body) {}
