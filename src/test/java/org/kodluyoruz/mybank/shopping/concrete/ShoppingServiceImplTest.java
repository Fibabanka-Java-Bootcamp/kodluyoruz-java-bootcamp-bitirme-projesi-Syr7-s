package org.kodluyoruz.mybank.shopping.concrete;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kodluyoruz.mybank.shopping.abstrct.ShoppingRepository;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShoppingServiceImplTest {
    @Mock
    ShoppingRepository shoppingRepository;

    @InjectMocks
    ShoppingServiceImpl shoppingService;

    static Shopping shopping = new Shopping();
    @BeforeAll
    static void init(){
        shopping.setProductID(6);
        shopping.setProductType("Technology");
        shopping.setProductName("Phone");
        shopping.setProductPrice(6000);
        shopping.setCurrency(Currency.TRY);
        shopping.setProductReceiveDate(LocalDate.of(2021,2,9));
        shopping.setCreditCard(null);
    }

    @BeforeEach
    void setUp() {
        shoppingService = new ShoppingServiceImpl(shoppingRepository,null,null);
        shoppingRepository.save(shopping);

        Mockito.when(shoppingRepository.findShoppingByProductID(shopping.getProductID())).thenReturn(shopping);
        assert shopping != null;
    }

    @Test
    void productNamePhone(){
        assertEquals("Phone",shoppingService
                .getShoppingByProductID(shopping.getProductID())
                .getProductName());
    }
    @Test
    void productPriceGreaterThanFiveThousand(){
        assertTrue(shoppingService
                .getShoppingByProductID(shopping.getProductID())
                .getProductPrice()>5000);
    }
}