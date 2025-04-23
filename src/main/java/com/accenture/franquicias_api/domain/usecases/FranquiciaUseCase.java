package com.accenture.franquicias_api.domain.usecases;

import com.accenture.franquicias_api.application.dto.franquicia.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.franquicia.SucursalDTO;
import com.accenture.franquicias_api.domain.exception.IntegracionExcepcion;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.FranquiciaRepositoryAdapter;
import com.accenture.franquicias_api.infraestructura.entrypoints.helpers.ConvertidorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                }).flatMap(franquiciaRepositoryAdapter::saveFranquicia)
                .map(ConvertidorDTO::franquiciaToFranquiciaDTO);
    }


}
