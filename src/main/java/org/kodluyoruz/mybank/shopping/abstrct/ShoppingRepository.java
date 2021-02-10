package org.kodluyoruz.mybank.shopping.abstrct;



import org.springframework.data.repository.CrudRepository;
import org.kodluyoruz.mybank.shopping.concrete.Shopping;
public interface ShoppingRepository extends CrudRepository<Shopping,Integer> {
    Shopping findShoppingByProductID(int id);
}
