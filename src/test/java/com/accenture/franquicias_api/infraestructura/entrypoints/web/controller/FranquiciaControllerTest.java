package com.accenture.franquicias_api.infraestructura.entrypoints.web.controller;

import com.accenture.franquicias_api.application.dto.franquicia.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.franquicia.ProductoDTO;
import com.accenture.franquicias_api.application.dto.franquicia.SucursalDTO;
import com.accenture.franquicias_api.application.dto.response.ResponseDTO;
import com.accenture.franquicias_api.domain.exception.IntegracionExcepcion;
import com.accenture.franquicias_api.domain.usecases.FranquiciaUseCase;
import com.accenture.franquicias_api.infraestructura.entrypoints.helpers.DefaultResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FranquiciaControllerTest {

    @DisplayName("Debe agregar la franquicia correctamente y responder con 201 Created")
    @Test
    void agregarFranquicaCorrectamente() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        FranquiciaDTO franquiciaDTO = FranquiciaDTO.builder()
                .nombre("Franquicia Test")
                .sucursales(new ArrayList<>())
                .build();

        FranquiciaDTO savedFranquiciaDTO = FranquiciaDTO.builder()
                .nombre("Franquicia Test")
                .sucursales(new ArrayList<>())
                .build();

        ResponseDTO responseDTO = ResponseDTO.builder()
                .data(savedFranquiciaDTO)
                .build();

        when(franquiciaUseCase.guardarFranquicia(franquiciaDTO)).thenReturn(Mono.just(savedFranquiciaDTO));
        when(defaultResponseMapper.buildDefaultServiceResponse(savedFranquiciaDTO)).thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.agregarFranquicia(franquiciaDTO);

        // Assert
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(responseDTO, responseEntity.getBody());
                })
                .verifyComplete();

        verify(franquiciaUseCase).guardarFranquicia(franquiciaDTO);
        verify(defaultResponseMapper).buildDefaultServiceResponse(savedFranquiciaDTO);
    }

    @DisplayName("No Debe agregar la sucursal correctamente y responder error")
    @Test
    void agregarSucursalError() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        String nombreFranquicia = "Franquicia Inexistente";
        SucursalDTO sucursalDTO = SucursalDTO.builder()
                .nombre("Nueva Sucursal")
                .productos(new ArrayList<>())
                .build();

        IntegracionExcepcion excepcion = IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS
                .build(FranquiciaUseCase.NO_SE_ENCONTRO_LA_FRANQUICIA_CON_NOMBRE + nombreFranquicia);

        when(franquiciaUseCase.agregarSucursal(nombreFranquicia, sucursalDTO))
                .thenReturn(Mono.error(excepcion));

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.agregarSucursal(nombreFranquicia, sucursalDTO);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IntegracionExcepcion &&
                                throwable.getMessage().contains(nombreFranquicia))
                .verify();

        verify(franquiciaUseCase).agregarSucursal(nombreFranquicia, sucursalDTO);
        Mockito.verifyNoInteractions(defaultResponseMapper);
    }


    @DisplayName("Debe agregar la sucursal correctamente y responder con 201 Created")
    @Test
    void agregarSucursalCorrectamente() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        SucursalDTO sucursalDTO = SucursalDTO.builder().nombre("Sucursal1").build();
        FranquiciaDTO franquiciaDTO = FranquiciaDTO.builder().nombre("Franquicia1").build();
        ResponseDTO responseDTO = ResponseDTO.builder().data(franquiciaDTO).build();

        when(franquiciaUseCase.agregarSucursal(anyString(), any(SucursalDTO.class)))
                .thenReturn(Mono.just(franquiciaDTO));
        when(defaultResponseMapper.buildDefaultServiceResponse(any()))
                .thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> response = franquiciaController.agregarSucursal("Franquicia1", sucursalDTO);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(entity -> entity.getStatusCode() == HttpStatus.CREATED)
                .verifyComplete();
    }

    @DisplayName("Debe agregar el producto correctamente y responder con 201 Created")
    @Test
    void agregarProductoSucursalCorrectamente() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        ProductoDTO productoDTO = ProductoDTO.builder()
                .nombre("Producto Test")
                .stock(10)
                .build();

        String nombreSucursal = "Sucursal Test";
        List<FranquiciaDTO> franquiciaDTOList = new ArrayList<>();
        ResponseDTO responseDTO = ResponseDTO.builder().data(franquiciaDTOList).build();

        when(franquiciaUseCase.agregarProductoSucurcal(productoDTO, nombreSucursal))
                .thenReturn(Mono.just(franquiciaDTOList));
        when(defaultResponseMapper.buildDefaultServiceResponse(franquiciaDTOList))
                .thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.agregarProductoSucursal(productoDTO, nombreSucursal);

        // Assert
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(responseDTO, responseEntity.getBody());
                })
                .verifyComplete();

        verify(franquiciaUseCase).agregarProductoSucurcal(productoDTO, nombreSucursal);
        verify(defaultResponseMapper).buildDefaultServiceResponse(franquiciaDTOList);
    }

    @DisplayName("Debe eliminar el producto correctamente y responder con 201")
    @Test
    void eliminarProductoSucursal() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = Mockito.mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = Mockito.mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        String nombreSucursal = "Sucursal1";
        String nombreProducto = "Producto1";

        List<FranquiciaDTO> franquiciaDTOList = new ArrayList<>();
        ResponseDTO responseDTO = ResponseDTO.builder().data(franquiciaDTOList).build();

        Mockito.when(franquiciaUseCase.eliminarProductoSucursal(nombreSucursal, nombreProducto))
                .thenReturn(Mono.just(franquiciaDTOList));
        Mockito.when(defaultResponseMapper.buildDefaultServiceResponse(franquiciaDTOList))
                .thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.eliminarProductoSucursal(nombreSucursal, nombreProducto);

        // Assert
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(responseDTO, responseEntity.getBody());
                })
                .verifyComplete();
    }

    @DisplayName("Debe actualizar el stock del producto correctamente y responder con 201")
    @Test
    void actualizarStockProductoCorrectamente() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = Mockito.mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = Mockito.mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        String nombreProducto = "Producto1";
        int stock = 10;
        List<FranquiciaDTO> franquiciaDTOList = List.of(FranquiciaDTO.builder().build());
        ResponseDTO responseDTO = ResponseDTO.builder().data(franquiciaDTOList).build();

        Mockito.when(franquiciaUseCase.actualizarProducto(nombreProducto, stock))
                .thenReturn(Mono.just(franquiciaDTOList));
        Mockito.when(defaultResponseMapper.buildDefaultServiceResponse(franquiciaDTOList))
                .thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.actualizarProducto(nombreProducto, stock);

        // Assert
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(responseDTO, responseEntity.getBody());
                })
                .verifyComplete();

        Mockito.verify(franquiciaUseCase).actualizarProducto(nombreProducto, stock);
        Mockito.verify(defaultResponseMapper).buildDefaultServiceResponse(franquiciaDTOList);
    }

    @DisplayName("No debe actualizar el stock del producto correctamente y responder not found 404")
    @Test
    void actualizarStockProductoNoEncontradoError() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = Mockito.mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = Mockito.mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        String nombreProducto = "ProductoInexistente";
        int stock = 10;

        IntegracionExcepcion exception = IntegracionExcepcion.Type.NO_SE_ENCONTRARON_RESULTADOS
                .build(FranquiciaUseCase.NO_SE_ENCONTRO_PRODUCTO_CON_NOMBRE + nombreProducto);

        Mockito.when(franquiciaUseCase.actualizarProducto(nombreProducto, stock))
                .thenReturn(Mono.error(exception));

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.actualizarProducto(nombreProducto, stock);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IntegracionExcepcion &&
                                throwable.getMessage().contains(nombreProducto))
                .verify();

        Mockito.verify(franquiciaUseCase).actualizarProducto(nombreProducto, stock);
        Mockito.verify(defaultResponseMapper, Mockito.never()).buildDefaultServiceResponse(Mockito.any());
    }

    // Successfully retrieves a franchise with the product of highest stock in each branch
    @DisplayName("Debe obtener el mayor stock de franquicias correctamente y responder con 201")
    @Test
    void obtenerMayorStockFranquicias() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        String nombreFranquicia = "TestFranquicia";
        FranquiciaDTO franquiciaDTO = FranquiciaDTO.builder().build();
        franquiciaDTO.setNombre(nombreFranquicia);

        ResponseDTO responseDTO = ResponseDTO.builder()
                .data(franquiciaDTO)
                .build();

        when(franquiciaUseCase.obtenerMayorStock(nombreFranquicia)).thenReturn(Mono.just(franquiciaDTO));
        when(defaultResponseMapper.buildDefaultServiceResponse(franquiciaDTO)).thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.obtenerMayorStock(nombreFranquicia);

        // Assert
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(responseDTO, responseEntity.getBody());
                    assertEquals(franquiciaDTO, responseEntity.getBody().getData());
                })
                .verifyComplete();

        verify(franquiciaUseCase).obtenerMayorStock(nombreFranquicia);
        verify(defaultResponseMapper).buildDefaultServiceResponse(franquiciaDTO);
    }

    @DisplayName("Debe actualizar el nombre de la franquicia correctamente y responder con 201")
    @Test
    void actualizarNombreFranquiciaCorrectamente() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = Mockito.mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = Mockito.mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        String actualNombreFranquicia = "OldName";
        String nuevoNombreFranquicia = "NewName";

        FranquiciaDTO updatedFranquicia = FranquiciaDTO.builder().build();
        updatedFranquicia.setNombre(nuevoNombreFranquicia);

        ResponseDTO responseDTO = ResponseDTO.builder()
                .data(updatedFranquicia)
                .build();

        Mockito.when(franquiciaUseCase.actualizarNombreFranquicia(actualNombreFranquicia, nuevoNombreFranquicia))
                .thenReturn(Mono.just(updatedFranquicia));
        Mockito.when(defaultResponseMapper.buildDefaultServiceResponse(updatedFranquicia))
                .thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.actualizarNombreFranquicia(actualNombreFranquicia, nuevoNombreFranquicia);

        // Assert
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(responseDTO, responseEntity.getBody());
                    assertEquals(updatedFranquicia, responseEntity.getBody().getData());
                })
                .verifyComplete();

        Mockito.verify(franquiciaUseCase).actualizarNombreFranquicia(actualNombreFranquicia, nuevoNombreFranquicia);
        Mockito.verify(defaultResponseMapper).buildDefaultServiceResponse(updatedFranquicia);
    }

    @DisplayName("Debe actualizar el nombre de la sucursal correctamente y responder con 201")
    @Test
    void actualizarNombreSucursalCorrectamente() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = Mockito.mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = Mockito.mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        String actualNombreSucursal = "SucursalVieja";
        String nuevoNombreSucursal = "SucursalNueva";

        List<FranquiciaDTO> franquiciaDTOList = new ArrayList<>();
        franquiciaDTOList.add(FranquiciaDTO.builder().build());

        ResponseDTO responseDTO = ResponseDTO.builder().data(franquiciaDTOList).build();

        Mockito.when(franquiciaUseCase.actualizarNombreSucursal(actualNombreSucursal, nuevoNombreSucursal))
                .thenReturn(Mono.just(franquiciaDTOList));
        Mockito.when(defaultResponseMapper.buildDefaultServiceResponse(franquiciaDTOList))
                .thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.actualizarNombreSucursal(actualNombreSucursal, nuevoNombreSucursal);

        // Assert
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(responseDTO, responseEntity.getBody());
                })
                .verifyComplete();

        Mockito.verify(franquiciaUseCase).actualizarNombreSucursal(actualNombreSucursal, nuevoNombreSucursal);
        Mockito.verify(defaultResponseMapper).buildDefaultServiceResponse(franquiciaDTOList);
    }

    @DisplayName("Debe actualizar el nombre del producto correctamente y responder con 201")
    @Test
    void debeActualizarNombreProductoCorrectamente() {
        // Arrange
        FranquiciaUseCase franquiciaUseCase = Mockito.mock(FranquiciaUseCase.class);
        DefaultResponseMapper defaultResponseMapper = Mockito.mock(DefaultResponseMapper.class);
        FranquiciaController franquiciaController = new FranquiciaController(franquiciaUseCase, defaultResponseMapper);

        String actualNombreProducto = "ProductoViejo";
        String nuevoNombreProducto = "ProductoNuevo";

        List<FranquiciaDTO> franquiciaDTOList = new ArrayList<>();
        franquiciaDTOList.add(FranquiciaDTO.builder().build());

        ResponseDTO responseDTO = ResponseDTO.builder().data(franquiciaDTOList).build();

        Mockito.when(franquiciaUseCase.actualizarNombreProducto(actualNombreProducto, nuevoNombreProducto))
                .thenReturn(Mono.just(franquiciaDTOList));
        Mockito.when(defaultResponseMapper.buildDefaultServiceResponse(franquiciaDTOList))
                .thenReturn(responseDTO);

        // Act
        Mono<ResponseEntity<ResponseDTO>> result = franquiciaController.actualizarNombreProducto(actualNombreProducto, nuevoNombreProducto);

        // Assert
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(responseDTO, responseEntity.getBody());
                })
                .verifyComplete();

        Mockito.verify(franquiciaUseCase).actualizarNombreProducto(actualNombreProducto, nuevoNombreProducto);
        Mockito.verify(defaultResponseMapper).buildDefaultServiceResponse(franquiciaDTOList);
    }



}