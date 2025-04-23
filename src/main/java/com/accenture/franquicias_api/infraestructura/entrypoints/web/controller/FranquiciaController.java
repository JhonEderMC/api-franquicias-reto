package com.accenture.franquicias_api.infraestructura.entrypoints.web.controller;

import com.accenture.franquicias_api.application.dto.franquicia.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.franquicia.SucursalDTO;
import com.accenture.franquicias_api.application.dto.response.ResponseDTO;
import com.accenture.franquicias_api.domain.usecases.FranquiciaUseCase;
import com.accenture.franquicias_api.infraestructura.entrypoints.helpers.DefaultResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.base.path}/franquicias")
public class FranquiciaController {

    private final FranquiciaUseCase franquiciaUseCase;
    private final DefaultResponseMapper defaultResponseMapper;

    @PostMapping
    public Mono<ResponseEntity<ResponseDTO>> agregarFranquicia(@RequestBody FranquiciaDTO franquiciaDTO) {
        return franquiciaUseCase.guardarFranquicia(franquiciaDTO)
                .map(response -> (ResponseEntity.status(HttpStatus.CREATED)
                        .body(defaultResponseMapper.buildDefaultServiceResponse(response)))
                );
    }

    @PostMapping("/{nombre}/sucursal")
    public Mono<ResponseEntity<ResponseDTO>> agregarSucursal(@PathVariable("nombre") String nombre, @RequestBody SucursalDTO sucursalDTO) {
        return franquiciaUseCase.agregarSucursal(nombre, sucursalDTO)
                .map(response -> (ResponseEntity.status(HttpStatus.CREATED)
                        .body(defaultResponseMapper.buildDefaultServiceResponse(response)))
                );
    }
}
