package org.kodluyoruz.mybank.shopping.abstrct;

import org.kodluyoruz.mybank.shopping.concrete.Shopping;
import org.springframework.data.repository.CrudRepository;

public interface ShoppingRepository extends CrudRepository<Shopping,Integer> {
}
