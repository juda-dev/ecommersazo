package configurationservice.configuration.presentation;

import configurationservice.configuration.application.BroadcastService;
import configurationservice.configuration.application.ConfigurationResponse;
import configurationservice.configuration.application.ConfigurationService;
import configurationservice.configuration.application.CreateConfigurationRequest;
import configurationservice.configuration.application.UpdateConfigurationRequest;
import dev.juda.pagination.PageResult;
import dev.juda.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/configurations")
public class ConfigurationController {

    private static final Logger log = LoggerFactory.getLogger(
        ConfigurationController.class
    );
    private final ConfigurationService service;
    private final BroadcastService broadcastService;

    public ConfigurationController(
        ConfigurationService service,
        BroadcastService broadcastService
    ) {
        this.service = service;
        this.broadcastService = broadcastService;
    }

    @PostMapping
    public ResponseEntity<?> create(
        @Valid @RequestBody CreateConfigurationRequest request,
        HttpServletRequest httpRequest
    ) {
        Result<ConfigurationResponse> result = service.create(request);
        return switch (result) {
            case Result.Success<
                ConfigurationResponse
            > s -> ResponseEntity.status(201).body(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus()).body(
                e.toErrorResponse(httpRequest.getRequestURI())
            );
        };
    }

    @GetMapping
    public ResponseEntity<?> findAll(
        @RequestParam(required = false) String namespace,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Result<PageResult<ConfigurationResponse>> result = service.findAll(
            namespace,
            page,
            size
        );
        return switch (result) {
            case Result.Success<
                PageResult<ConfigurationResponse>
            > s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus()).body(
                e.toErrorResponse(null)
            );
        };
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> findByKey(
        @PathVariable String key,
        HttpServletRequest request
    ) {
        Result<ConfigurationResponse> result = service.findByKey(key);
        return switch (result) {
            case Result.Success<ConfigurationResponse> s -> ResponseEntity.ok(
                s.value()
            );
            case Result.Error e -> ResponseEntity.status(e.httpStatus()).body(
                e.toErrorResponse(request.getRequestURI())
            );
        };
    }

    @PutMapping("/{key}")
    public ResponseEntity<?> update(
        @PathVariable String key,
        @Valid @RequestBody UpdateConfigurationRequest request,
        HttpServletRequest httpRequest
    ) {
        Result<ConfigurationResponse> result = service.update(key, request);
        return switch (result) {
            case Result.Success<ConfigurationResponse> s -> ResponseEntity.ok(
                s.value()
            );
            case Result.Error e -> ResponseEntity.status(e.httpStatus()).body(
                e.toErrorResponse(httpRequest.getRequestURI())
            );
        };
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> delete(
        @PathVariable String key,
        HttpServletRequest request
    ) {
        Result<Void> result = service.delete(key);
        return switch (result) {
            case Result.Success<Void> s -> ResponseEntity.noContent().build();
            case Result.Error e -> ResponseEntity.status(e.httpStatus()).body(
                e.toErrorResponse(request.getRequestURI())
            );
        };
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return broadcastService.subscribe();
    }
}
