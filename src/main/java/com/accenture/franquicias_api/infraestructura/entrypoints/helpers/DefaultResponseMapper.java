package com.accenture.franquicias_api.infraestructura.entrypoints.helpers;

import com.accenture.franquicias_api.application.dto.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultResponseMapper {

    private final ObjectMapper mapper;

    public ResponseDTO buildDefaultServiceResponse(Object dataResponse) {
        return ResponseDTO.builder()
                .data(dataResponse)
                .build();
    }

}