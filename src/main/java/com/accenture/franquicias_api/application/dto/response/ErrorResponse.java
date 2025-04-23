package com.accenture.franquicias_api.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ErrorResponse {

    private String code;
    private String description;
    private Integer status;
    private String date;
}
