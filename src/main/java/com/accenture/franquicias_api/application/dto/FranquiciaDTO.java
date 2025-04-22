package com.accenture.franquicias_api.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FranquiciaDTO {

    private String nombre;
    private List<SucursalDTO> sucursales;

}
