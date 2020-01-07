package intragamma.mboogerd;

import org.springframework.data.repository.CrudRepository;

public interface ProductItemRepository extends CrudRepository<ProductItem, Long> {

    long countByProductIdAndIsReservedTrue(long productId);

    long countByProductIdAndIsReservedFalse(long productId);

}
