package com.example.coursework.decoder;

import com.example.coursework.exceptions.ProductNotFoundException;
import com.example.coursework.exceptions.UserNotFoundException;
import com.example.coursework.utils.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.startsWith;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorMessage message = null;
        try {
            byte[] arr = response.body().asInputStream().readAllBytes();
            var mapper = new ObjectMapper();
            message = mapper.readValue(arr, ErrorMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        if (response.status() > 400 && response.status() < 500) {
            if (startsWith(message.getServiceName(), "product")) {
                return new ProductNotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
            } else if (startsWith(message.getServiceName(), "user")) {
                return new UserNotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
            }
        }
        return new RuntimeException();
    }
}
