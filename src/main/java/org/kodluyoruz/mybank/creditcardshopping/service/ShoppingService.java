package org.kodluyoruz.mybank.creditcardshopping.service;

import org.kodluyoruz.mybank.creditcardshopping.entity.Shopping;
import org.kodluyoruz.mybank.creditcardshopping.repository.ShoppingRepository;
import org.springframework.stereotype.Service;

@Service
public class ShoppingService {
    private final ShoppingRepository shoppingRepository;

    public ShoppingService(ShoppingRepository shoppingRepository) {
        this.shoppingRepository = shoppingRepository;
    }

    public Shopping create(Shopping shopping){
        return shoppingRepository.save(shopping);
    }
}
