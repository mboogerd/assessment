package intergamma.mboogerd;

import org.springframework.data.repository.CrudRepository;

public interface StockItemRepository extends CrudRepository<StockItem, Long> {

    long countByProductCodeAndReservationTimestampNotNull(String productCode);

    long countByProductCodeAndReservationTimestampNull(String productCode);
}
