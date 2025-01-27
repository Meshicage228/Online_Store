package com.example.coursework.decoder;

import com.example.coursework.exceptions.ProductNotFoundException;
import com.example.coursework.exceptions.UserNotFoundException;
import com.example.coursework.utils.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {
    private final ObjectMapper mapper;

    private final static Map<Predicate<Integer>, Function<ErrorMessage, Exception>> errorHandlers = new HashMap<>() {{
        put(value -> value == NOT_FOUND.value(), errorMessage -> {
            if (startsWith(errorMessage.getServiceName(), "product")) {
                return new ProductNotFoundException(errorMessage.getMessage() != null ? errorMessage.getMessage() : "Not found");
            } else if (startsWith(errorMessage.getServiceName(), "user")) {
                return new UserNotFoundException(errorMessage.getMessage() != null ? errorMessage.getMessage() : "Not found");
            }
            return new RuntimeException("Resource not found");
        });
    }};

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            ErrorMessage errorMessage = parseErrorResponse(response);
            return handleError(response.status(), errorMessage);
        } catch (IOException e) {
            String readFailed = "Failed to read error message: " + e.getMessage();
            log.error(readFailed);
            return new RuntimeException(readFailed);
        }
    }

    private ErrorMessage parseErrorResponse(Response response) throws IOException {
        byte[] arr = response.body().asInputStream().readAllBytes();
        return mapper.readValue(arr, ErrorMessage.class);
    }

    private Exception handleError(int status, ErrorMessage errorMessage) {
        return errorHandlers.entrySet().stream()
                .filter(entry -> entry.getKey().test(status))
                .map(entry -> entry.getValue().apply(errorMessage))
                .findFirst()
                .orElseThrow(() -> {
                    String errorReason = Optional.ofNullable(errorMessage.getMessage()).orElse("Unknown error");
                    log.error("No handler for status : {}; exception message : {}", status, errorReason);
                    return new RuntimeException(errorReason);
                });
    }
}
