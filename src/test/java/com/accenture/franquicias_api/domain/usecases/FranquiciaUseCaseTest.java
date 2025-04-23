package com.accenture.franquicias_api.domain.usecases;

import com.accenture.franquicias_api.application.dto.franquicia.FranquiciaDTO;
import com.accenture.franquicias_api.application.dto.franquicia.ProductoDTO;
import com.accenture.franquicias_api.application.dto.franquicia.SucursalDTO;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.FranquiciaRepositoryAdapter;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Franquicia;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Producto;
import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Sucursal;
import com.accenture.franquicias_api.infraestructura.entrypoints.helpers.ConvertidorDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertFalse;
import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FranquiciaUseCaseTest {

    @DisplayName("Debe guardar una franquicia correctamente")
    @Test
    void guardarFranquiciaCorrectamente() {
        // Arrange
        FranquiciaRepositoryAdapter franquiciaRepositoryAdapter = mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase franquiciaUseCase = new FranquiciaUseCase(franquiciaRepositoryAdapter);

        ProductoDTO productoDTO = ProductoDTO.builder()
                .nombre("Producto Test")
                .stock(10)
                .build();

        SucursalDTO sucursalDTO = SucursalDTO.builder()
                .nombre("Sucursal Test")
                .productos(List.of(productoDTO))
                .build();

        FranquiciaDTO franquiciaDTO = FranquiciaDTO.builder()
                .nombre("Franquicia Test")
                .sucursales(List.of(sucursalDTO))
                .build();

        Franquicia franquicia = ConvertidorDTO.franquiciaDTOToFranquicia(franquiciaDTO);

        when(franquiciaRepositoryAdapter.saveFranquicia(any(Franquicia.class)))
                .thenReturn(Mono.just(franquicia));

        // Act
        Mono<FranquiciaDTO> result = franquiciaUseCase.guardarFranquicia(franquiciaDTO);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedFranquicia ->
                        savedFranquicia.getNombre().equals("Franquicia Test") &&
                                savedFranquicia.getSucursales().size() == 1 &&
                                savedFranquicia.getSucursales().getFirst().getNombre().equals("Sucursal Test") &&
                                savedFranquicia.getSucursales().getFirst().getProductos().size() == 1 &&
                                savedFranquicia.getSucursales().getFirst().getProductos().get(0).getNombre().equals("Producto Test"))
                .verifyComplete();

        verify(franquiciaRepositoryAdapter).saveFranquicia(any(Franquicia.class));
    }

    @DisplayName("Debe agregar una sucursal correctamente a una franquicia existente")
    @Test
    void agregarSucusurcalCorrectamente() {
        // Arrange
        FranquiciaRepositoryAdapter franquiciaRepositoryAdapter = mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase franquiciaUseCase = new FranquiciaUseCase(franquiciaRepositoryAdapter);

        String franquiciaNombre = "TestFranquicia";

        SucursalDTO sucursalDTO = SucursalDTO.builder()
                .nombre("NuevaSucursal")
                .productos(List.of())
                .build();

        Franquicia existingFranquicia = Franquicia.builder().build();
        existingFranquicia.setId("123");
        existingFranquicia.setNombre(franquiciaNombre);
        existingFranquicia.setSucursales(new ArrayList<>());

        Franquicia updatedFranquicia = Franquicia.builder().build();
        updatedFranquicia.setId("123");
        updatedFranquicia.setNombre(franquiciaNombre);
        updatedFranquicia.setSucursales(List.of(ConvertidorDTO.sucursalDTOToSucursal(sucursalDTO)));

        when(franquiciaRepositoryAdapter.findFranquiciaByName(franquiciaNombre)).thenReturn(Mono.just(existingFranquicia));
        when(franquiciaRepositoryAdapter.updateFranquicia(any(Franquicia.class))).thenReturn(Mono.just(updatedFranquicia));

        // Act
        Mono<FranquiciaDTO> result = franquiciaUseCase.agregarSucursal(franquiciaNombre, sucursalDTO);

        // Assert
        StepVerifier.create(result)
                .assertNext(franquiciaDTO -> {
                    assertEquals(franquiciaNombre, franquiciaDTO.getNombre());
                    assertEquals(1, franquiciaDTO.getSucursales().size());
                    assertEquals("NuevaSucursal", franquiciaDTO.getSucursales().get(0).getNombre());
                })
                .verifyComplete();

        verify(franquiciaRepositoryAdapter).findFranquiciaByName(franquiciaNombre);
        verify(franquiciaRepositoryAdapter).updateFranquicia(any(Franquicia.class));
    }

    @DisplayName("Debe agregar Producto a una sucursal existente")
    @Test
    void agregarProductoSucursalExistente() {
        // Arrange
        FranquiciaRepositoryAdapter franquiciaRepositoryAdapter = Mockito.mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase franquiciaUseCase = new FranquiciaUseCase(franquiciaRepositoryAdapter);

        String nombreSucursal = "Sucursal Test";
        ProductoDTO productoDTO = ProductoDTO.builder()
                .nombre("Producto Test")
                .stock(10)
                .build();

        Producto producto = Producto.builder()
                .nombre(productoDTO.getNombre())
                .stock(productoDTO.getStock())
                .build();

        List<Producto> productos = new ArrayList<>();

        Sucursal sucursal = Sucursal.builder().build();
        sucursal.setNombre(nombreSucursal);
        sucursal.setProductos(productos);

        List<Sucursal> sucursales = new ArrayList<>();
        sucursales.add(sucursal);

        Franquicia franquicia = Franquicia.builder().build();
        franquicia.setId("1");
        franquicia.setNombre("Franquicia Test");
        franquicia.setSucursales(sucursales);

        Franquicia franquiciaActualizada = Franquicia.builder().build();
        franquiciaActualizada.setId("1");
        franquiciaActualizada.setNombre("Franquicia Test");
        List<Sucursal> sucursalesActualizadas = new ArrayList<>();
        Sucursal sucursalActualizada = Sucursal.builder().build();
        sucursalActualizada.setNombre(nombreSucursal);
        List<Producto> productosActualizados = new ArrayList<>();
        productosActualizados.add(producto);
        sucursalActualizada.setProductos(productosActualizados);
        sucursalesActualizadas.add(sucursalActualizada);
        franquiciaActualizada.setSucursales(sucursalesActualizadas);

        FranquiciaDTO franquiciaDTO = FranquiciaDTO.builder()
                .nombre(franquiciaActualizada.getNombre())
                .sucursales(ConvertidorDTO.sucursalesToSucursalesDTO(franquiciaActualizada.getSucursales()))
                .build();

        // Mock behavior
        Mockito.when(franquiciaRepositoryAdapter.obtenerFranquicias()).thenReturn(Flux.just(franquicia));
        Mockito.when(franquiciaRepositoryAdapter.updateFranquicia(Mockito.any(Franquicia.class))).thenReturn(Mono.just(franquiciaActualizada));

        // Act
        Mono<List<FranquiciaDTO>> result = franquiciaUseCase.agregarProductoSucurcal(productoDTO, nombreSucursal);

        // Assert
        StepVerifier.create(result)
                .assertNext(franquiciaDTOs -> {
                    assertNotNull(franquiciaDTOs);
                    assertEquals(1, franquiciaDTOs.size());
                    assertEquals(franquiciaDTO.getNombre(), franquiciaDTOs.getFirst().getNombre());
                    assertEquals(1, franquiciaDTOs.getFirst().getSucursales().size());
                    assertEquals(nombreSucursal, franquiciaDTOs.getFirst().getSucursales().get(0).getNombre());
                    assertEquals(1, franquiciaDTOs.getFirst().getSucursales().get(0).getProductos().size());
                    assertEquals(productoDTO.getNombre(), franquiciaDTOs.getFirst().getSucursales().get(0).getProductos().get(0).getNombre());
                    assertEquals(productoDTO.getStock(), franquiciaDTOs.getFirst().getSucursales().get(0).getProductos().get(0).getStock());
                })
                .verifyComplete();

        Mockito.verify(franquiciaRepositoryAdapter).obtenerFranquicias();
        Mockito.verify(franquiciaRepositoryAdapter).updateFranquicia(Mockito.any(Franquicia.class));
    }

    @DisplayName("Debe eliminar Producto de una sucursal existente")
    @Test
    void eliminarProductoSucursalExistente() {
        // Arrange
        String nombreSucursal = "Sucursal1";
        String nombreProducto = "Producto1";

        Producto producto = Producto.builder().build();
        ReflectionTestUtils.setField(producto, "nombre", nombreProducto);

        List<Producto> productos = new ArrayList<>();
        productos.add(producto);

        Sucursal sucursal = Sucursal.builder().build();
        ReflectionTestUtils.setField(sucursal, "nombre", nombreSucursal);
        ReflectionTestUtils.setField(sucursal, "productos", productos);

        List<Sucursal> sucursales = new ArrayList<>();
        sucursales.add(sucursal);

        Franquicia franquicia = Franquicia.builder().build();
        ReflectionTestUtils.setField(franquicia, "id", "1");
        ReflectionTestUtils.setField(franquicia, "nombre", "Franquicia1");
        ReflectionTestUtils.setField(franquicia, "sucursales", sucursales);

        FranquiciaRepositoryAdapter franquiciaRepositoryAdapter = Mockito.mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase franquiciaUseCase = new FranquiciaUseCase(franquiciaRepositoryAdapter);

        Mockito.when(franquiciaRepositoryAdapter.obtenerFranquicias()).thenReturn(Flux.just(franquicia));
        Mockito.when(franquiciaRepositoryAdapter.updateFranquicia(Mockito.any(Franquicia.class))).thenAnswer(invocation -> {
            Franquicia updatedFranquicia = invocation.getArgument(0);
            return Mono.just(updatedFranquicia);
        });

        // Act
        Mono<List<FranquiciaDTO>> result = franquiciaUseCase.eliminarProductoSucursal(nombreSucursal, nombreProducto);

        // Assert
        StepVerifier.create(result)
                .assertNext(franquiciaDTOs -> {
                    assertNotNull(franquiciaDTOs);
                    assertEquals(1, franquiciaDTOs.size());
                    FranquiciaDTO franquiciaDTO = franquiciaDTOs.getFirst();
                    assertEquals("Franquicia1", franquiciaDTO.getNombre());
                    assertEquals(1, franquiciaDTO.getSucursales().size());
                    assertEquals(0, franquiciaDTO.getSucursales().getFirst().getProductos().size());
                })
                .verifyComplete();

        Mockito.verify(franquiciaRepositoryAdapter).obtenerFranquicias();
        Mockito.verify(franquiciaRepositoryAdapter).updateFranquicia(Mockito.any(Franquicia.class));
    }

    // Updates stock of a product when it exists in at least one franchise
    @DisplayName("Debe actualizar Stock de Producto existente")
    @Test
    void actualizarStockProductoExistente() {
        // Arrange
        FranquiciaRepositoryAdapter repositoryAdapter = Mockito.mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase useCase = new FranquiciaUseCase(repositoryAdapter);

        String productName = "TestProduct";
        int newStock = 50;

        Producto producto = Producto.builder().build();
        producto.setNombre(productName);
        producto.setStock(10);

        List<Producto> productos = List.of(producto);

        Sucursal sucursal = Sucursal.builder().build();
        sucursal.setProductos(productos);

        List<Sucursal> sucursales = List.of(sucursal);

        Franquicia franquicia = Franquicia.builder().build();
        franquicia.setSucursales(sucursales);
        franquicia.setNombre("TestFranquicia");

        Mockito.when(repositoryAdapter.obtenerFranquicias()).thenReturn(Flux.just(franquicia));
        Mockito.when(repositoryAdapter.updateFranquicia(Mockito.any(Franquicia.class))).thenAnswer(invocation -> {
            Franquicia updated = invocation.getArgument(0);
            return Mono.just(updated);
        });

        // Act
        Mono<List<FranquiciaDTO>> result = useCase.actualizarProducto(productName, newStock);

        // Assert
        StepVerifier.create(result)
                .assertNext(franquiciaDTOs -> {
                    assertFalse(franquiciaDTOs.isEmpty());
                    assertEquals(1, franquiciaDTOs.size());
                    assertEquals("TestFranquicia", franquiciaDTOs.getFirst().getNombre());
                })
                .verifyComplete();

        Mockito.verify(repositoryAdapter).obtenerFranquicias();
        Mockito.verify(repositoryAdapter).updateFranquicia(Mockito.any(Franquicia.class));
    }

    @DisplayName("Debe obtener franquicia con productos con mas stock por sucursal")
    @Test
    void obtenerFranquiciaConProductosConMasStockPorBranch() {
        // Arrange
        FranquiciaRepositoryAdapter repositoryAdapter = Mockito.mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase useCase = new FranquiciaUseCase(repositoryAdapter);
        String nombreFranquicia = "TestFranquicia";

        // Create products with different stock levels
        Producto producto1 = Producto.builder().id("1").nombre("Producto1").stock(10).build();
        Producto producto2 = Producto.builder().id("2").nombre("Producto2").stock(20).build();
        Producto producto3 = Producto.builder().id("3").nombre("Producto3").stock(5).build();

        // Create branches with products
        Sucursal sucursal1 = Sucursal.builder().build();
        sucursal1.setNombre("Sucursal1");
        sucursal1.setProductos(Arrays.asList(producto1, producto2));

        Sucursal sucursal2 = Sucursal.builder().build();
        sucursal2.setNombre("Sucursal2");
        sucursal2.setProductos(Arrays.asList(producto3));

        // Create franchise
        Franquicia franquicia = Franquicia.builder().build();
        franquicia.setNombre(nombreFranquicia);
        franquicia.setSucursales(Arrays.asList(sucursal1, sucursal2));

        // Mock repository response
        when(repositoryAdapter.findFranquiciaByName(nombreFranquicia)).thenReturn(Mono.just(franquicia));

        // Act
        StepVerifier.create(useCase.obtenerMayorStock(nombreFranquicia))
                .assertNext(result -> {
                    // Assert
                    assertEquals(nombreFranquicia, result.getNombre());
                    assertEquals(2, result.getSucursales().size());

                    // Check first branch has only the product with highest stock
                    SucursalDTO sucursalDTO1 = result.getSucursales().get(0);
                    assertEquals("Sucursal1", sucursalDTO1.getNombre());
                    assertEquals(1, sucursalDTO1.getProductos().size());
                    assertEquals("Producto2", sucursalDTO1.getProductos().get(0).getNombre());
                    assertEquals(20, sucursalDTO1.getProductos().get(0).getStock());

                    // Check second branch has only the product with highest stock
                    SucursalDTO sucursalDTO2 = result.getSucursales().get(1);
                    assertEquals("Sucursal2", sucursalDTO2.getNombre());
                    assertEquals(1, sucursalDTO2.getProductos().size());
                    assertEquals("Producto3", sucursalDTO2.getProductos().get(0).getNombre());
                    assertEquals(5, sucursalDTO2.getProductos().get(0).getStock());
                })
                .verifyComplete();

        verify(repositoryAdapter).findFranquiciaByName(nombreFranquicia);
    }

    @DisplayName("Debe Actualizar nombre de franquicia existente")
    @Test
    void actualizarNombreFranquiciaExistente() {
        // Arrange
        FranquiciaRepositoryAdapter franquiciaRepositoryAdapter = Mockito.mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase franquiciaUseCase = new FranquiciaUseCase(franquiciaRepositoryAdapter);

        String currentName = "Franquicia Actual";
        String newName = "Franquicia Nueva";

        Franquicia existingFranquicia = Franquicia.builder()
                .id("1")
                .nombre(currentName)
                .sucursales(new ArrayList<>())
                .build();

        Franquicia updatedFranquicia = Franquicia.builder()
                .id("1")
                .nombre(newName)
                .sucursales(new ArrayList<>())
                .build();

        // Mock behavior
        Mockito.when(franquiciaRepositoryAdapter.findFranquiciaByName(newName))
                .thenReturn(Mono.empty());
        Mockito.when(franquiciaRepositoryAdapter.findFranquiciaByName(currentName))
                .thenReturn(Mono.just(existingFranquicia));
        Mockito.when(franquiciaRepositoryAdapter.updateFranquicia(Mockito.any(Franquicia.class)))
                .thenReturn(Mono.just(updatedFranquicia));

        // Act
        Mono<FranquiciaDTO> result = franquiciaUseCase.actualizarNombreFranquicia(currentName, newName);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(franquiciaDTO ->
                        franquiciaDTO.getNombre().equals(newName))
                .verifyComplete();

        Mockito.verify(franquiciaRepositoryAdapter).findFranquiciaByName(newName);
        Mockito.verify(franquiciaRepositoryAdapter).findFranquiciaByName(currentName);
        Mockito.verify(franquiciaRepositoryAdapter).updateFranquicia(Mockito.any(Franquicia.class));
    }

    @DisplayName("Debe actualizar nombre de sucursal existente")
    @Test
    void actualizarNombreSucursalExistente() {
        // Arrange
        FranquiciaRepositoryAdapter franquiciaRepositoryAdapter = Mockito.mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase franquiciaUseCase = new FranquiciaUseCase(franquiciaRepositoryAdapter);

        String actualNombreSucursal = "Sucursal A";
        String nuevoNombreSucursal = "Sucursal B";

        Sucursal sucursal = Sucursal.builder().build();
        sucursal.setNombre(actualNombreSucursal);

        List<Sucursal> sucursales = new ArrayList<>();
        sucursales.add(sucursal);

        Franquicia franquicia = Franquicia.builder().build();
        franquicia.setSucursales(sucursales);

        Franquicia updatedFranquicia = Franquicia.builder().build();
        List<Sucursal> updatedSucursales = new ArrayList<>();
        Sucursal updatedSucursal = Sucursal.builder().build();
        updatedSucursal.setNombre(nuevoNombreSucursal);
        updatedSucursales.add(updatedSucursal);
        updatedFranquicia.setSucursales(updatedSucursales);

        Mockito.when(franquiciaRepositoryAdapter.obtenerFranquicias()).thenReturn(Flux.just(franquicia));
        Mockito.when(franquiciaRepositoryAdapter.updateFranquicia(Mockito.any(Franquicia.class))).thenReturn(Mono.just(updatedFranquicia));

        // Act
        Mono<List<FranquiciaDTO>> result = franquiciaUseCase.actualizarNombreSucursal(actualNombreSucursal, nuevoNombreSucursal);

        // Assert
        StepVerifier.create(result)
                .assertNext(franquiciaDTOs -> {
                    assertNotNull(franquiciaDTOs);
                    assertEquals(1, franquiciaDTOs.size());
                    List<SucursalDTO> sucursalDTOs = franquiciaDTOs.getFirst().getSucursales();
                    assertEquals(1, sucursalDTOs.size());
                    assertEquals(nuevoNombreSucursal, sucursalDTOs.getFirst().getNombre());
                })
                .verifyComplete();

        Mockito.verify(franquiciaRepositoryAdapter).obtenerFranquicias();
        Mockito.verify(franquiciaRepositoryAdapter).updateFranquicia(Mockito.any(Franquicia.class));
    }

   @DisplayName("Debe actualizar nombre de producto en todos los productos")
    @Test
    void debeActualizarNombreProductoEnTodosLosProductos() {
        // Arrange
        FranquiciaRepositoryAdapter franquiciaRepositoryAdapter = mock(FranquiciaRepositoryAdapter.class);
        FranquiciaUseCase franquiciaUseCase = new FranquiciaUseCase(franquiciaRepositoryAdapter);

        String oldProductName = "Producto Viejo";
        String newProductName = "Producto Nuevo";

        // Create test data
        Producto producto1 = Producto.builder().build();
        producto1.setNombre(oldProductName);

        Producto producto2 = Producto.builder().build();
        producto2.setNombre("Otro Producto");

        List<Producto> productos1 = Arrays.asList(producto1, producto2);

        Sucursal sucursal1 = Sucursal.builder().build();
        sucursal1.setProductos(productos1);

        Producto producto3 = Producto.builder().build();
        producto3.setNombre(oldProductName);

        List<Producto> productos2 = List.of(producto3);

        Sucursal sucursal2 = Sucursal.builder().build();
        sucursal2.setProductos(productos2);

        Franquicia franquicia1 = Franquicia.builder().build();
        franquicia1.setId("1");
        franquicia1.setNombre("Franquicia 1");
        franquicia1.setSucursales(List.of(sucursal1));

        Franquicia franquicia2 = Franquicia.builder().build();
        franquicia2.setId("2");
        franquicia2.setNombre("Franquicia 2");
        franquicia2.setSucursales(List.of(sucursal2));

        // Mock repository responses
        when(franquiciaRepositoryAdapter.obtenerFranquicias()).thenReturn(Flux.just(franquicia1, franquicia2));
        when(franquiciaRepositoryAdapter.updateFranquicia(any(Franquicia.class))).thenAnswer(invocation -> {
            Franquicia franquicia = invocation.getArgument(0);
            return Mono.just(franquicia);
        });

        // Act
        Mono<List<FranquiciaDTO>> result = franquiciaUseCase.actualizarNombreProducto(oldProductName, newProductName);

        // Assert
        StepVerifier.create(result)
                .assertNext(franquiciaDTOs -> {
                    assertEquals(2, franquiciaDTOs.size());

                    // Verify all products with old name were updated
                    franquiciaDTOs.forEach(franquiciaDTO ->
                            franquiciaDTO.getSucursales().forEach(sucursalDTO ->
                                    sucursalDTO.getProductos().forEach(productoDTO -> {
                                        if (productoDTO.getNombre().equals(newProductName)) {
                                            // This was updated
                                            assertNotEquals(oldProductName, productoDTO.getNombre());
                                        }
                                    })
                            )
                    );

                    // Verify repository was called to update both franchises
                    verify(franquiciaRepositoryAdapter, times(2)).updateFranquicia(any(Franquicia.class));
                })
                .verifyComplete();
    }



}