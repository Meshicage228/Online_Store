package com.example.coursework.decoder;

import com.example.coursework.exceptions.ProductNotFoundException;
import com.example.coursework.exceptions.UserNotFoundException;
import com.example.coursework.utils.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;

import java.io.IOException;
import java.io.InputStream;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorMessage message = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            var mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ErrorMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        switch (response.status()) {
            case 400 -> {
                return new BadRequestException(message.getMessage() != null ? message.getMessage() : "Bad Request");
            }
            case 404 -> {
                if(StringUtils.startsWith(message.getServiceName(), "product")) {
                    return new ProductNotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
                } else {
                    return new UserNotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
                }
            }
            default -> {
                return new RuntimeException();
            }
        }
    }
}
