package com.accenture.franquicias_api.infraestructura.entrypoints.web.controller;

import com.accenture.franquicias_api.application.dto.franquicia.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.franquicia.ProductoDTO;
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
@RequestMapping("${api.base.path}/franquicia")
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

    @PostMapping(path = "/agregar/sucursal", params = {"nombre"})

    public Mono<ResponseEntity<ResponseDTO>> agregarSucursal(@RequestParam("nombre") String nombre, @RequestBody SucursalDTO sucursalDTO) {
        return franquiciaUseCase.agregarSucursal(nombre, sucursalDTO)
                .map(response -> (ResponseEntity.status(HttpStatus.CREATED)
                        .body(defaultResponseMapper.buildDefaultServiceResponse(response)))
                );
    }

    @PostMapping(path = "/agregar/producto/sucursal", params = {"nombreSucursal"})
    public Mono<ResponseEntity<ResponseDTO>> agregarProductoSucursal(@RequestBody ProductoDTO productoDTO, @RequestParam("nombreSucursal") String nombreSucursal) {
        return franquiciaUseCase.agregarProductoSucurcal(productoDTO, nombreSucursal)
                .map(response -> (ResponseEntity.status(HttpStatus.CREATED)
                        .body(defaultResponseMapper.buildDefaultServiceResponse(response)))
                );
    }

    @DeleteMapping(path = "/eliminar/producto/sucursal", params = {"nombreSucursal", "nombreProducto"})
    public Mono<ResponseEntity<ResponseDTO>> eliminarProductoSucursal(@RequestParam("nombreSucursal") String nombreSucursal, @RequestParam("nombreProducto") String nombreProducto) {
        return franquiciaUseCase.eliminarProductoSucursal(nombreSucursal, nombreProducto)
                .map(response -> (ResponseEntity.status(HttpStatus.CREATED)
                        .body(defaultResponseMapper.buildDefaultServiceResponse(response)))
                );
    }

    @PatchMapping (path = "/actualizar/producto", params = {"nombreProducto", "stock"})
    public Mono<ResponseEntity<ResponseDTO>> actualizarProducto(@RequestParam("nombreProducto") String nombreProducto, @RequestParam("stock") int stock) {
        return franquiciaUseCase.actualizarProducto(nombreProducto, stock)
                .map(response -> (ResponseEntity.status(HttpStatus.CREATED)
                        .body(defaultResponseMapper.buildDefaultServiceResponse(response)))
                );
    }

    @GetMapping(path = "/obtener/mayorStock", params = {"nombreFranquicia"})
    public Mono<ResponseEntity<ResponseDTO>> obtenerMayorStock(@RequestParam("nombreFranquicia") String nombreFranquicia) {
        return franquiciaUseCase.obtenerMayorStock(nombreFranquicia)
                .map(response -> (ResponseEntity.status(HttpStatus.CREATED)
                        .body(defaultResponseMapper.buildDefaultServiceResponse(response)))
                );
    }
}
