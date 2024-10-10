package com.microservice_shopping;

import com.microservice_shopping.persistence.model.entities.PurchaseOrder;
import com.microservice_shopping.persistence.model.enums.State;
import com.microservice_shopping.persistence.model.vo.EventVO;
import com.microservice_shopping.persistence.repository.PurchaseOrderRepository;
import com.microservice_shopping.presentation.dto.PurchaseOrderDTO;
import com.microservice_shopping.service.implementation.PurchaseOrderServiceImpl;
import com.microservice_shopping.utils.AppUtil;
import com.microservice_shopping.utils.mapper.MapperOrder;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PurchaseOrderServiceImplTest {

    @Mock
    private AppUtil appUtil;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private MapperOrder mapperOrder;

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


}
