package com.accenture.franquicias_api.infraestructura.entrypoints.helpers;

import com.accenture.franquicias_api.application.dto.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.ProductoDTO;
import com.accenture.franquicias_api.application.dto.SucursalDTO;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Franquicia;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Producto;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Sucursal;

import java.util.List;
import java.util.Optional;

public class ConvertidorDTO {

    private ConvertidorDTO() {
        throw new IllegalStateException("Utility class");
    }

    public static Franquicia franquiciaDTOToFranquicia(FranquiciaDTO franquiciaDTO) {
        return Franquicia.builder()
                .nombre(franquiciaDTO.getNombre())
                .sucursales(getSucursales(franquiciaDTO))
                .build();
    }

    public static FranquiciaDTO franquiciaToFranquiciaDTO(Franquicia franquicia) {
        return FranquiciaDTO.builder()
                .nombre(franquicia.getNombre())
                .sucursales(getSucursalesDTO(franquicia))
                .build();
    }

    private static List<Sucursal> getSucursales(FranquiciaDTO franquiciaDTO) {
        return  Optional.ofNullable(franquiciaDTO.getSucursales()).orElse(List.of())
                .stream()
                .map(sucursalDTO -> Sucursal.builder()
                        .nombre(sucursalDTO.getNombre())
                        .productos(getProductos(sucursalDTO))
                        .build())
                .toList();

    }

    private static List<Producto> getProductos(SucursalDTO sucursalDTO) {
        return Optional.ofNullable(sucursalDTO.getProductos()).orElse(List.of())
                .stream()
                .map(productoDTO -> Producto.builder()
                        .nombre(productoDTO.getNombre())
                        .stock(productoDTO.getStock())
                        .build())
                .toList();
    }

    private static List<SucursalDTO> getSucursalesDTO(Franquicia franquicia) {
        return  Optional.ofNullable(franquicia.getSucursales()).orElse(List.of())
                .stream()
                .map(sucursal -> SucursalDTO.builder()
                        .nombre(sucursal.getNombre())
                        .productos(getProductosDTO(sucursal))
                        .build())
                .toList();

    }

    private static List<ProductoDTO> getProductosDTO(Sucursal sucursal) {
        return Optional.ofNullable(sucursal.getProductos()).orElse(List.of())
                .stream()
                .map(producto -> ProductoDTO.builder()
                        .nombre(producto.getNombre())
                        .stock(producto.getStock())
                        .build())
                .toList();
    }
}
