package com.accenture.franquicias_api.infraestructura.entrypoints.web.exepction;

import com.accenture.franquicias_api.application.dto.response.ErrorResponse;
import com.accenture.franquicias_api.application.dto.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ExceptionControllerHandler {
    private ExceptionControllerHandler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static ResponseEntity<ResponseDTO> construirResponseException(Integer status, String message, String code) {

        List<ErrorResponse> errors = new ArrayList<>();
        ErrorResponse responseExceptionHandler = ErrorResponse.builder()
                .status(status)
                .description(message)
                .code(code)
                .date(LocalDateTime.now().toString())
                .build();

        errors.add(responseExceptionHandler);

        ResponseDTO apiResponse = ResponseDTO.builder()
                .errores(errors)
                .build();

        return new ResponseEntity<>(apiResponse, Objects.requireNonNull(HttpStatus.resolve(status)));
    }


}
