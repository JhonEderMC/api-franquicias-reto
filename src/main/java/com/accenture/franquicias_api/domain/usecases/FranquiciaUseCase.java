package com.accenture.franquicias_api.domain.usecases;

import com.accenture.franquicias_api.application.dto.franquicia.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.franquicia.ProductoDTO;
import com.accenture.franquicias_api.application.dto.franquicia.SucursalDTO;
import com.accenture.franquicias_api.domain.exception.IntegracionExcepcion;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.FranquiciaRepositoryAdapter;
import com.accenture.franquicias_api.infraestructura.entrypoints.helpers.ConvertidorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FranquiciaUseCase {

    private final FranquiciaRepositoryAdapter franquiciaRepositoryAdapter;

    public Mono<FranquiciaDTO> guardarFranquicia(FranquiciaDTO franquiciaDTO) {
        return franquiciaRepositoryAdapter.saveFranquicia(ConvertidorDTO.franquiciaDTOToFranquicia(franquiciaDTO))
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO);
    }

    public Mono<FranquiciaDTO> agregarSucursal(String nombre, SucursalDTO sucursalDTO) {

        return franquiciaRepositoryAdapter.findFranquiciaByName(nombre)
                .switchIfEmpty(Mono.error(IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS.build("No se encontró la franquicia con nombre: " + nombre)))
                .map(franquicia -> {
                    franquicia.getSucursales().add(ConvertidorDTO.sucursalDTOToSucursal(sucursalDTO));
                            return franquicia;
                }).flatMap(franquiciaRepositoryAdapter::updateFranquicia)
               .map(ConvertidorDTO::franquiciaToFranquiciaDTO);
    }


    public Mono<List<FranquiciaDTO>> agregarProductoSucurcal(ProductoDTO productoDTO, String nombreSucursal) {
        return franquiciaRepositoryAdapter.obtenerFranquicias()
                .filter(franquicia -> franquicia.getSucursales().stream().anyMatch(sucursal -> sucursal.getNombre().equals(nombreSucursal)))
                .map(franquicia -> {
                            franquicia.getSucursales().forEach(sucursal -> {
                                if(sucursal.getNombre().equals(nombreSucursal)) {
                                    sucursal.getProductos().add(ConvertidorDTO.productoDtoToProducto(productoDTO));
                                }
                            });
                                    return franquicia;
               }).flatMap(franquiciaRepositoryAdapter::updateFranquicia)
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO)
                .switchIfEmpty(Mono.error(IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS.build("No se encontró la Sucursal con nombre: " + nombreSucursal)))
                .collectList();
    }

    public Mono<List<FranquiciaDTO>> eliminarProductoSucursal(String nombreSucursal, String nombreProducto) {
        return franquiciaRepositoryAdapter.obtenerFranquicias()
                .filter(franquicia -> franquicia.getSucursales().stream().anyMatch(sucursal -> {
                    if(sucursal.getNombre().equals(nombreSucursal)) {
                        return sucursal.getProductos().stream().anyMatch(producto -> producto.getNombre().equals(nombreProducto));
                    }
                    return false;
                })).map(franquicia -> {
                    franquicia.getSucursales().forEach(sucursal -> {
                        if(sucursal.getNombre().equals(nombreSucursal)) {
                            sucursal.getProductos().removeIf(producto -> producto.getNombre().equals(nombreProducto));
                        }
                    });
                    return franquicia;
                })
                .flatMap(franquiciaRepositoryAdapter::updateFranquicia)
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO)
                .switchIfEmpty(Mono.error(IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS.build("No se encontró la Sucursal con nombre: " + nombreSucursal+ " y Producto con nombre: " + nombreProducto)))
                .collectList();
    }
}
