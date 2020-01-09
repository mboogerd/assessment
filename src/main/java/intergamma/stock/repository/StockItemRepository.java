package intergamma.stock.repository;

import intergamma.stock.domain.StockItem;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface StockItemRepository extends CrudRepository<StockItem, Long> {

    long countByProductCodeAndReservationTimestampNotNull(String productCode);

    long countByProductCodeAndReservationTimestampNull(String productCode);

    Iterable<StockItem> findByProductCode(String productCode);

    Iterable<StockItem> findByReservationTimestampLessThan(LocalDateTime localDateTime);
}
