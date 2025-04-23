package com.accenture.franquicias_api.infraestructura.entrypoints.helpers;

import com.accenture.franquicias_api.application.dto.franquicia.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.franquicia.ProductoDTO;
import com.accenture.franquicias_api.application.dto.franquicia.SucursalDTO;
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
                .sucursales(sucursalesDtoToSucursales(franquiciaDTO.getSucursales()))
                .build();
    }

    public static FranquiciaDTO franquiciaToFranquiciaDTO(Franquicia franquicia) {
        return FranquiciaDTO.builder()
                .nombre(franquicia.getNombre())
                .sucursales(sucursalesToSucursalesDTO(franquicia.getSucursales()))
                .build();
    }

    public static List<Sucursal> sucursalesDtoToSucursales(List<SucursalDTO> sucursalesDTO) {
        return  Optional.ofNullable(sucursalesDTO).orElse(List.of())
                .stream()
                .map(ConvertidorDTO::sucursalDTOToSucursal)
                .toList();

    }

    public static Sucursal sucursalDTOToSucursal(SucursalDTO sucursalDTO) {
        return Sucursal.builder()
                .nombre(sucursalDTO.getNombre())
                .productos(productosDtoToProducto(sucursalDTO.getProductos()))
                .build();
    }

    public static List<Producto> productosDtoToProducto(List<ProductoDTO> productosDTO) {
        return Optional.ofNullable(productosDTO).orElse(List.of())
                .stream()
                .map(ConvertidorDTO::productoDtoToProducto)
                .toList();
    }

    public static Producto productoDtoToProducto(ProductoDTO productoDTO) {
        return Producto.builder()
                .nombre(productoDTO.getNombre())
                .stock(productoDTO.getStock())
                .build();
    }

    public static List<SucursalDTO> sucursalesToSucursalesDTO(List<Sucursal> sucursales) {
        return  Optional.ofNullable(sucursales).orElse(List.of())
                .stream()
                .map(sucursal -> SucursalDTO.builder()
                        .nombre(sucursal.getNombre())
                        .productos(productosToProductosDTO(sucursal.getProductos()))
                        .build())
                .toList();

    }

    private static List<ProductoDTO> productosToProductosDTO(List<Producto> productos) {
        return Optional.ofNullable(productos).orElse(List.of())
                .stream()
                .map(ConvertidorDTO::productoDtoToProducto)
                .toList();
    }

    private static ProductoDTO productoDtoToProducto(Producto producto) {
        return ProductoDTO.builder()
                .nombre(producto.getNombre())
                .stock(producto.getStock())
                .build();
    }

}
