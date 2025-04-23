package com.accenture.franquicias_api.infraestructura.adapters.persitence;

import com.accenture.franquicias_api.domain.exception.IntegracionExcepcion;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Franquicia;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Sucursal;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class FranquiciaRepositoryAdapterTest {

    @DisplayName("Debe guardar una franquicia correctamente")
    @Test
    void gudeSaveFranquiciaCorrectamente() {
        // Arrange
        ReactiveMongoTemplate mongoTemplate = mock(ReactiveMongoTemplate.class);
        FranquiciaRepositoryAdapter repositoryAdapter = new FranquiciaRepositoryAdapter(mongoTemplate);

        Franquicia franquicia = Franquicia.builder()
                .id("1")
                .nombre("Test Franquicia")
                .sucursales(Collections.emptyList())
                .build();

        when(mongoTemplate.save(franquicia)).thenReturn(Mono.just(franquicia));

        // Act
        Mono<Franquicia> result = repositoryAdapter.saveFranquicia(franquicia);

        // Assert
        StepVerifier.create(result)
                .expectNext(franquicia)
                .verifyComplete();

        verify(mongoTemplate).save(franquicia);
    }

    @DisplayName("Debe retornar error al guardar una franquicia")
    @Test
    void guardarFranquiciaError() {
        // Arrange
        ReactiveMongoTemplate mongoTemplate = mock(ReactiveMongoTemplate.class);
        FranquiciaRepositoryAdapter repositoryAdapter = new FranquiciaRepositoryAdapter(mongoTemplate);

        Franquicia franquicia = Franquicia.builder()
                .id("1")
                .nombre("Test Franquicia")
                .sucursales(Collections.emptyList())
                .build();

        String errorMessage = "Database connection failed";
        RuntimeException dbException = new RuntimeException(errorMessage);
        when(mongoTemplate.save(franquicia)).thenReturn(Mono.error(dbException));

        // Act
        Mono<Franquicia> result = repositoryAdapter.saveFranquicia(franquicia);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(IntegracionExcepcion.class);
                    IntegracionExcepcion integrationError = (IntegracionExcepcion) error;
                    assertThat(integrationError.getType()).isEqualTo(IntegracionExcepcion.Type.ERROR_DB_TRANSACTION);
                    assertThat(error.getMessage()).contains(errorMessage);
                })
                .verify();

        verify(mongoTemplate).save(franquicia);
    }

    @DisplayName("Debe consultar una franquicia por nombre correctamente")
    @Test
    void encontrarFranquiciaPorNombreCorrectamente() {
        // Arrange
        ReactiveMongoTemplate mongoTemplate = mock(ReactiveMongoTemplate.class);
        FranquiciaRepositoryAdapter repositoryAdapter = new FranquiciaRepositoryAdapter(mongoTemplate);

        String nombreFranquicia = "TestFranquicia";
        Franquicia expectedFranquicia = Franquicia.builder()
                .id("1")
                .nombre(nombreFranquicia)
                .sucursales(new ArrayList<>())
                .build();

        when(mongoTemplate.findOne(any(Query.class), eq(Franquicia.class)))
                .thenReturn(Mono.just(expectedFranquicia));

        // Act
        Mono<Franquicia> result = repositoryAdapter.findFranquiciaByName(nombreFranquicia);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedFranquicia)
                .verifyComplete();

        verify(mongoTemplate).findOne(any(Query.class), eq(Franquicia.class));
    }

    @DisplayName("Debe actualizar una franquicia correctamente")
    @Test
    void actualizarFranquiciaCorrectamente() {
        // Arrange
        ReactiveMongoTemplate mongoTemplate = mock(ReactiveMongoTemplate.class);
        FranquiciaRepositoryAdapter repositoryAdapter = new FranquiciaRepositoryAdapter(mongoTemplate);

        String id = "123";
        String nombre = "Franquicia Test";
        List<Sucursal> sucursales = new ArrayList<>();

        Franquicia franquicia = Franquicia.builder()
                .id(id)
                .nombre(nombre)
                .sucursales(sucursales)
                .build();

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(1L);

        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Franquicia.class)))
                .thenReturn(Mono.just(updateResult));

        when(mongoTemplate.findById(id, Franquicia.class)).thenReturn(Mono.just(franquicia));

        // Act
        Mono<Franquicia> result = repositoryAdapter.updateFranquicia(franquicia);

        // Assert
        StepVerifier.create(result)
                .expectNext(franquicia)
                .verifyComplete();

        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(Franquicia.class));
        verify(mongoTemplate).findById(id, Franquicia.class);
    }

    @DisplayName("No debe actualizar una franquicia cuando no existe")
    @Test
    void noDebeActualizarFranquiciaCuandoNoExiste() {
        // Arrange
        ReactiveMongoTemplate mongoTemplate = mock(ReactiveMongoTemplate.class);
        FranquiciaRepositoryAdapter repositoryAdapter = new FranquiciaRepositoryAdapter(mongoTemplate);

        String id = "nonexistent";
        String nombre = "Franquicia No Existe";
        List<Sucursal> sucursales = new ArrayList<>();

        Franquicia franquicia = Franquicia.builder()
                .id(id)
                .nombre(nombre)
                .sucursales(sucursales)
                .build();

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(0L);

        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Franquicia.class)))
                .thenReturn(Mono.just(updateResult));

        // Act
        Mono<Franquicia> result = repositoryAdapter.updateFranquicia(franquicia);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(Franquicia.class));
        verify(mongoTemplate, never()).findById(anyString(), eq(Franquicia.class));
    }


}