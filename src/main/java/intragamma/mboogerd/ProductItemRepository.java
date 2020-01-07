package intragamma.mboogerd;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProductItemRepository extends CrudRepository<ProductItem, Long> {

    long countByProductIdAndIsReservedTrue(long productId);

    long countByProductIdAndIsReservedFalse(long productId);

    @Modifying(clearAutomatically = true)
    @Query("update ProductItem productItem set productItem.isReserved = true where productItem.id = :id")
    int reserveProductItem(@Param("id") Long id);
}
