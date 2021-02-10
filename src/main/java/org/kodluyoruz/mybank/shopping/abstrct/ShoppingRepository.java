package org.kodluyoruz.mybank.shopping.abstrct;

import org.kodluyoruz.mybank.shopping.concrete.Shopping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingRepository extends CrudRepository<Shopping,Integer> {
}
