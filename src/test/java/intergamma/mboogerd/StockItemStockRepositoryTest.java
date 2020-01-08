package intergamma.mboogerd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class StockItemStockRepositoryTest {

    private TestEntityManager entityManager;
    private StockItemRepository repository;
    private String productCode = "defaultProductCode";

    @Autowired
    public StockItemStockRepositoryTest(
            TestEntityManager entityManager,
            StockItemRepository repository) {

        this.entityManager = entityManager;
        this.repository = repository;
    }

    @Test
    void unknownProductImpliesNoStock() {
        assert repository.countByProductCodeAndReservationTimestampNull(productCode) == 0;
    }

    @Test
    void unknownProductImpliesNoReservations() {
        assert repository.countByProductCodeAndReservationTimestampNotNull(productCode) == 0;
    }

    @Test
    void nonReservedProductItemIsAvailable() {
        StockItem stockItem = new StockItem(productCode);
        entityManager.persist(stockItem);
        entityManager.flush();

        assert repository.countByProductCodeAndReservationTimestampNull(productCode) == 1;
        assert repository.countByProductCodeAndReservationTimestampNotNull(productCode) == 0;
    }

    @Test
    void reservedProductItemIsUnavailable() {
        StockItem stockItem = new StockItem(productCode);
        stockItem.reserve();
        entityManager.persist(stockItem);
        entityManager.flush();

        assert repository.countByProductCodeAndReservationTimestampNull(productCode) == 0;
        assert repository.countByProductCodeAndReservationTimestampNotNull(productCode) == 1;
    }

    @Test
    void reservingAProductMakesItUnavailable() {
        // Given a single ProductItem for a given productId
        StockItem stockItem = new StockItem(productCode);
        entityManager.persist(stockItem);
        entityManager.flush();

        // When we reserve the ProductItem
        assert stockItem.reserve();
        repository.save(stockItem);

        // Then no product items should be available for the given productId
        assert repository.countByProductCodeAndReservationTimestampNull(productCode) == 0;

    }

}
