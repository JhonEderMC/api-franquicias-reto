package com.accenture.franquicias_api.infraestructura.adapters.persitence.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "franquicias")
public class Franquicia {

    @Id
    private String id;
    private String nombre;
    private List<Sucursal> sucursales;
}
