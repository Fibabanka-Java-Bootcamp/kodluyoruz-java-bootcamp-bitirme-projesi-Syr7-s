package org.kodluyoruz.mybank.shopping.abstrct;




import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.kodluyoruz.mybank.shopping.concrete.Shopping;
public interface ShoppingRepository extends CrudRepository<Shopping,Integer> {
    Shopping findShoppingByProductID(int id);
    //Page<Shopping> findAll(Pageable pageable);
}
