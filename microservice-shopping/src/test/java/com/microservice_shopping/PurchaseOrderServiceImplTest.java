package com.microservice_shopping;

import com.microservice_shopping.persistence.model.entities.PurchaseOrder;
import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.persistence.model.vo.EventVO;
import com.microservice_shopping.persistence.repository.PurchaseOrderRepository;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import com.microservice_shopping.presentation.dto.StatusOrderDTO;
import com.microservice_shopping.service.implementation.PurchaseOrderServiceImpl;
import com.microservice_shopping.utils.AppUtil;
import com.microservice_shopping.utils.mapper.MapperOrder;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseOrderServiceImplTest {

    @Mock
    private AppUtil appUtil;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private MapperOrder mapperOrder;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    PurchaseOrderServiceImpl purchaseOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks
    }

    @Test
    void createPurchaseOrder_success_alternative() {
        // Arrange
        PurchaseOrderDTO mockPurchaseOrderDTO = mock(PurchaseOrderDTO.class);
        PurchaseOrder mockPurchaseOrderEntity = mock(PurchaseOrder.class);

        // Simulación de datos del DTO
        List<EventVO> mockCart = mock(List.class);
        when(mockPurchaseOrderDTO.cart()).thenReturn(mockCart);  // Carrito no nulo
        when(mockPurchaseOrderDTO.idOrder()).thenReturn("123");

        // Verificar que la orden no existe
        when(appUtil.checkOrderExists("123")).thenReturn(false);

        // Mapear el DTO a la entidad
        when(mapperOrder.dtoOrderToEntity(mockPurchaseOrderDTO)).thenReturn(mockPurchaseOrderEntity);

        // Act
        State actualState = purchaseOrderService.createPurchaseOrder(mockPurchaseOrderDTO);

        // Assert
        verify(purchaseOrderRepository, times(1)).save(mockPurchaseOrderEntity); // Verificar una vez la llamada al guardado
        verify(mapperOrder, times(1)).dtoOrderToEntity(mockPurchaseOrderDTO); // Verificar que se realizó el mapeo
        verify(appUtil, times(1)).checkOrderExists("123");  // Verificar que se chequeó si la orden existe

        // Asertar el resultado esperado
        assertSame(State.SUCCESS, actualState, "The order should be created successfully.");
    }

    @Test
    void createPurchaseOrder_nullPurchaseOrder_throwsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            purchaseOrderService.createPurchaseOrder(null);
        });
    }

    @Test
    void createPurchaseOrder_nullCart_throwsException() {
        // Arrange
        PurchaseOrderDTO mockPurchaseOrderDTO = mock(PurchaseOrderDTO.class);
        when(mockPurchaseOrderDTO.cart()).thenReturn(null);  // Simular un carrito nulo

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            purchaseOrderService.createPurchaseOrder(mockPurchaseOrderDTO);
        });
    }

    @Test
    void createPurchaseOrder_orderExists_throwsException() {
        // Arrange
        PurchaseOrderDTO mockPurchaseOrderDTO = mock(PurchaseOrderDTO.class);
        when(mockPurchaseOrderDTO.cart()).thenReturn(mock(List.class));
        when(mockPurchaseOrderDTO.idOrder()).thenReturn("123");
        when(appUtil.checkOrderExists("123")).thenReturn(true);  // Simular que la orden ya existe

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            purchaseOrderService.createPurchaseOrder(mockPurchaseOrderDTO);
        });
    }

    @Test
    void createPurchaseOrder_databaseError_returnsErrorState() {
        // Arrange
        PurchaseOrderDTO mockPurchaseOrderDTO = mock(PurchaseOrderDTO.class);
        PurchaseOrder mockPurchaseOrderEntity = mock(PurchaseOrder.class);

        when(mockPurchaseOrderDTO.cart()).thenReturn(mock(List.class));
        when(mockPurchaseOrderDTO.idOrder()).thenReturn("123");
        when(appUtil.checkOrderExists("123")).thenReturn(false);  // Orden no existe
        when(mapperOrder.dtoOrderToEntity(mockPurchaseOrderDTO)).thenReturn(mockPurchaseOrderEntity);

        // Simular una excepción de Mongo al guardar
        doThrow(new MongoException("DB Error")).when(purchaseOrderRepository).save(mockPurchaseOrderEntity);

        // Act
        State result = purchaseOrderService.createPurchaseOrder(mockPurchaseOrderDTO);

        // Assert
        assertEquals(State.ERROR, result);
    }

    @Test
    void updateStatusPurchaseOrder_orderNotExists() {
        // Arrange
        StatusOrderDTO mockStatusOrderDTO = mock(StatusOrderDTO.class);

        when(mockStatusOrderDTO.idPurchaseOrder()).thenReturn("123");

        // Simular que la orden no existe
        when(appUtil.checkOrderExists("123")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.updateStatusPurchaseOrder(mockStatusOrderDTO));

        assertEquals("la orden no existe", exception.getMessage());
        verify(mongoTemplate, never()).updateFirst(any(Query.class), any(Update.class), eq(PurchaseOrder.class));  // Verificar que no se intentó actualizar
    }

    @Test
    void updateStatusPurchaseOrder_updateFailed() {
        // Arrange
        StatusOrderDTO mockStatusOrderDTO = mock(StatusOrderDTO.class);
        UpdateResult mockUpdateResult = mock(UpdateResult.class);

        when(mockStatusOrderDTO.idPurchaseOrder()).thenReturn("123");
        when(mockStatusOrderDTO.stateOrder()).thenReturn(State.SUCCESS);

        // Simular que la orden existe
        when(appUtil.checkOrderExists("123")).thenReturn(true);

        // Simular el resultado de la actualización fallida (ningún documento modificado)
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(PurchaseOrder.class)))
                .thenReturn(mockUpdateResult);
        when(mockUpdateResult.getModifiedCount()).thenReturn(0L); // No se modifica ningún documento

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.updateStatusPurchaseOrder(mockStatusOrderDTO));

        assertEquals("Failed to update purchase order", exception.getMessage());
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq(PurchaseOrder.class));  // Verificar que se intentó actualizar
    }

    @Test
    void updateStatusPurchaseOrder_success() {
        // Arrange
        StatusOrderDTO statusOrderDTO = new StatusOrderDTO("123", State.SUCCESS); // En lugar de mock, uso una instancia real de DTO
        UpdateResult updateResult = mock(UpdateResult.class);  // Mantengo el mock para UpdateResult

        // Simular que la orden existe
        doReturn(true).when(appUtil).checkOrderExists(statusOrderDTO.idPurchaseOrder());

        // Simular que se modificó un documento en la actualización
        doReturn(updateResult).when(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(PurchaseOrder.class));
        when(updateResult.getModifiedCount()).thenReturn(1L);

        // Act
        State result = purchaseOrderService.updateStatusPurchaseOrder(statusOrderDTO);

        // Assert
        assertEquals(State.SUCCESS, result);  // Verifico que el resultado sea SUCCESS
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(PurchaseOrder.class));  // Verifico que se llamó al método updateFirst
        verify(appUtil).checkOrderExists(statusOrderDTO.idPurchaseOrder());  // Verifico que se comprobó la existencia de la orden
    }

    @Test
    void getAllPurchaseOrdersByUserId_success() {
        // Arrange
        String userId = "user123";
        List<PurchaseOrderDTO> mockPurchaseOrders = Arrays.asList(mock(PurchaseOrderDTO.class), mock(PurchaseOrderDTO.class));

        // Simular que el repositorio devuelve una lista de órdenes de compra
        when(purchaseOrderRepository.findByUserId(userId)).thenReturn(mockPurchaseOrders);

        // Act
        List<PurchaseOrderDTO> result = purchaseOrderService.getAllPurchaseOrdersByUserId(userId);

        // Assert
        assertNotNull(result);  // Verificar que el resultado no sea nulo
        assertEquals(2, result.size());  // Verificar que se devuelve la cantidad correcta de órdenes
        verify(purchaseOrderRepository).findByUserId(userId);  // Verificar que se llamó al repositorio correctamente
    }

    @Test
    void getAllPurchaseOrdersByUserId_nullUserId_throwsException() {
        // Arrange
        String userId = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseOrderService.getAllPurchaseOrdersByUserId(userId);
        });

        assertEquals("User id cannot be null", exception.getMessage());  // Verificar que el mensaje de excepción es correcto
    }

    @Test
    void getAllPurchaseOrdersByUserId_noPurchaseOrdersFound_throwsException() {
        // Arrange
        String userId = "user123";

        // Simular que el repositorio devuelve una lista vacía
        when(purchaseOrderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseOrderService.getAllPurchaseOrdersByUserId(userId);
        });

        assertEquals("No purchaseOrder found", exception.getMessage());  // Verificar que el mensaje de excepción es correcto
    }




}
