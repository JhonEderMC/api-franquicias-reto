package com.accenture.franquicias_api.domain.usecases;

import com.accenture.franquicias_api.infraestructura.adapters.persitence.FranquiciaRepositoryAdapter;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Franquicia;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Sucursal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class FranquiciaUseCase {

    private final FranquiciaRepositoryAdapter franquiciaRepositoryAdapter;

    public Mono<Franquicia> guardarFranquicia(Franquicia franquicia) {
        return franquiciaRepositoryAdapter.saveFranquicia(franquicia);
    }

    public Mono<Franquicia> agregarSucursal(String nombre, Sucursal sucursal) {
        return franquiciaRepositoryAdapter.findFranquiciaByName(nombre)
                .flatMap(franquicia -> {
                    if(franquicia == null) {
                      return  Mono.error(new IllegalArgumentException("No se encontró la franquicia con nombre: " + nombre));
                    }
                    franquicia.getSucursales().add(sucursal);
                    return franquiciaRepositoryAdapter.saveFranquicia(franquicia);
                });
    }


}
