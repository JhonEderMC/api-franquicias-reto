package com.accenture.franquicias_api.infraestructura.adapters.persitence.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "productos")
public class Producto {

    @Id
    private String id;
    private String nombre;
    private int stock;

}
