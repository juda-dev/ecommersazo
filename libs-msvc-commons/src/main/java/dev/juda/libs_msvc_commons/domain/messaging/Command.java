package dev.juda.libs_msvc_commons.domain.messaging;

import dev.juda.libs_msvc_commons.domain.enums.CommandType;

public record Command<T>(CommandType type, Long id, T body) {}
