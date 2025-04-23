package com.accenture.franquicias_api.application.dto.franquicia;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SucursalDTO {

    private String nombre;
    private List<ProductoDTO> productos;

}
