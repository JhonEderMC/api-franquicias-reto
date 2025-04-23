package com.accenture.franquicias_api.domain.usecases;

import com.accenture.franquicias_api.application.dto.franquicia.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.franquicia.ProductoDTO;
import com.accenture.franquicias_api.application.dto.franquicia.SucursalDTO;
import com.accenture.franquicias_api.domain.exception.IntegracionExcepcion;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.FranquiciaRepositoryAdapter;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Franquicia;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Producto;
import com.accenture.franquicias_api.infraestructura.entrypoints.helpers.ConvertidorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FranquiciaUseCase {

    public static final String NO_SE_ENCONTRO_LA_FRANQUICIA_CON_NOMBRE = "No se encontró la franquicia con nombre: ";
    public static final String NO_SE_ENCONTRO_LA_SUCURSAL_CON_NOMBRE = "No se encontró la Sucursal con nombre: ";
    private final FranquiciaRepositoryAdapter franquiciaRepositoryAdapter;

    public Mono<FranquiciaDTO> guardarFranquicia(FranquiciaDTO franquiciaDTO) {
        return franquiciaRepositoryAdapter.saveFranquicia(ConvertidorDTO.franquiciaDTOToFranquicia(franquiciaDTO))
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO);
    }

    public Mono<FranquiciaDTO> agregarSucursal(String nombre, SucursalDTO sucursalDTO) {

        return franquiciaRepositoryAdapter.findFranquiciaByName(nombre)
                .switchIfEmpty(Mono.error(IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS.build(NO_SE_ENCONTRO_LA_FRANQUICIA_CON_NOMBRE + nombre)))
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
                .switchIfEmpty(Mono.error(IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS.build(NO_SE_ENCONTRO_LA_SUCURSAL_CON_NOMBRE + nombreSucursal)))
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
                .switchIfEmpty(Mono.error(IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS.build(NO_SE_ENCONTRO_LA_SUCURSAL_CON_NOMBRE + nombreSucursal+ " y Producto con nombre: " + nombreProducto)))
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO)
                .collectList();
    }

    public Mono<List<FranquiciaDTO>> actualizarProducto(String nombreProducto, int stock) {
        return franquiciaRepositoryAdapter.obtenerFranquicias()
                .filter(franquicia -> franquicia.getSucursales().stream().anyMatch(sucursal ->
                    sucursal.getProductos().stream().anyMatch(producto -> producto.getNombre().equals(nombreProducto))
                )).map(franquicia -> {
                   franquicia.getSucursales().forEach(sucursal -> sucursal.getProductos().forEach(producto -> {
                       if(producto.getNombre().equals(nombreProducto)) {
                           producto.setStock(stock);
                       }
                   }));
                   return franquicia;
               }).flatMap(franquiciaRepositoryAdapter::updateFranquicia)
                .switchIfEmpty(Mono.error(IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS.build("No se encontró producto con nombre: " + nombreProducto )))
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO)
                .collectList();
    }

    public Mono<FranquiciaDTO> obtenerMayorStock(String nombreFranquicia) {
        return franquiciaRepositoryAdapter.findFranquiciaByName(nombreFranquicia)
                .switchIfEmpty(Mono.error(
                        IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS.build(NO_SE_ENCONTRO_LA_FRANQUICIA_CON_NOMBRE + nombreFranquicia)))
                .map(franquicia -> {
                    franquicia.getSucursales().forEach(sucursal -> {
                        Optional<Producto> maxProducto = sucursal.getProductos().stream()
                                .max(Comparator.comparingInt(Producto::getStock));
                        sucursal.setProductos(
                                maxProducto.map(List::of).orElseGet(List::of)
                        );
                    });
                    return franquicia;
                })
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO);
    }

    public Mono<FranquiciaDTO> actualizarNombreFranquicia(String actualNombreFranquicia, String nuevoNombreFranquicia) {
        return franquiciaRepositoryAdapter.findFranquiciaByName(nuevoNombreFranquicia)
                .flatMap(f -> Mono.<Franquicia>error(IntegracionExcepcion.Type.ERROR_ELEMENTO_DUPLICADO
                        .build("Ya existe una franquicia con el nombre: " + nuevoNombreFranquicia)))
                .switchIfEmpty(Mono.defer(() ->
                        franquiciaRepositoryAdapter.findFranquiciaByName(actualNombreFranquicia)
                                .switchIfEmpty(Mono.error(IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS
                                        .build(NO_SE_ENCONTRO_LA_FRANQUICIA_CON_NOMBRE + actualNombreFranquicia)))
                ))
                .map(franquicia -> {
                    franquicia.setNombre(nuevoNombreFranquicia);
                    return franquicia;
                })
                .flatMap(franquiciaRepositoryAdapter::updateFranquicia)
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO);
    }



}
