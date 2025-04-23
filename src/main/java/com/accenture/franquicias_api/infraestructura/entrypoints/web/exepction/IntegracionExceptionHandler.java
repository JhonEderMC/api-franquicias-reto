package com.accenture.franquicias_api.infraestructura.entrypoints.web.exepction;

import com.accenture.franquicias_api.application.dto.response.ResponseDTO;
import com.accenture.franquicias_api.domain.exception.IntegracionExcepcion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class IntegracionExceptionHandler {
    @ExceptionHandler(IntegracionExcepcion.class)
    public ResponseEntity<ResponseDTO> interceptorIntegracionException(IntegracionExcepcion exception) {
        Integer status = switch (exception.getType()) {
            case ERROR_EN_PETICION -> HttpStatus.BAD_REQUEST.value();
            case ERROR_ELEMENTO_DUPLICADO -> HttpStatus.CONFLICT.value();
            case NO_SE_ENCONTRARON_RESULTADOS -> HttpStatus.NOT_FOUND.value();
            default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };

        return ExceptionControllerHandler.construirResponseException(status, exception.getMessage(), "");
    }
}
