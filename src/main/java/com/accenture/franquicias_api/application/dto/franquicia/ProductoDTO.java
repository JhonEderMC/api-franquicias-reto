package com.accenture.franquicias_api.application.dto.franquicia;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductoDTO {

    private String nombre;
    private int stock;

}
