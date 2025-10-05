package com.example.coursework.exception.decoder;

import com.example.coursework.exception.NotEnoughMoneyException;
import com.example.coursework.exception.OutOfStockException;
import com.example.coursework.exception.ProductNotFoundException;
import com.example.coursework.exception.UserNotFoundException;
import com.example.coursework.exception.dto.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class FeignErrorDecoder implements ErrorDecoder {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(final String methodKey, final Response response) {
        ErrorMessage message = null;
        try {
            if (response.body() != null) {
                final byte[] arr = response.body().asInputStream().readAllBytes();
                message = mapper.readValue(arr, ErrorMessage.class);
            }
        } catch (final IOException e) {
            return new RuntimeException("Failed to parse error body", e);
        }

        if (message == null) {
            return new RuntimeException("Empty error message from remote service");
        }

        final String msg = message.getMessage() != null ? message.getMessage() : "Unknown error";
        final int status = response.status();

        if (status == 404) {
            if (message.getServiceName().contains("user")) {
                return new UserNotFoundException(msg);
            } else if (message.getServiceName().contains("product")) {
                return new ProductNotFoundException(msg);
            }
        } else if (status == 400) {
            if (msg.toLowerCase().contains("money")) {
                return new NotEnoughMoneyException(msg);
            } else if (msg.toLowerCase().contains("stock")) {
                return new OutOfStockException(msg);
            }
        }

        return new RuntimeException(msg);
    }
}