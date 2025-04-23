package com.accenture.franquicias_api.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class IntegracionExcepcion extends RuntimeException {

    @Getter
    @AllArgsConstructor
    public enum Type {
        ERROR_DB_TRANSACTION("Error en la transacción con la base de datos"),
        ERROR_EN_PETICION("Error en la petición al servicio"),
        NO_SE_ENCONTRARON_RESULTADOS("No se encontraron resultados"),
        ERROR_ELEMENTO_DUPLICADO("El elemento ya existe");


        private final String errorTecnico;

        public IntegracionExcepcion build(String errorTecnico) {
            return new IntegracionExcepcion(this, this.getErrorTecnico() + ": " + errorTecnico);
        }

        public IntegracionExcepcion build() {
            return new IntegracionExcepcion(this);
        }

    }

    private final Type type;

    private IntegracionExcepcion(Type type, String message) {
        super(message);
        this.type = type;
    }
    public IntegracionExcepcion(Type type) {
        super(type.errorTecnico);
        this.type = null;
    }
}
