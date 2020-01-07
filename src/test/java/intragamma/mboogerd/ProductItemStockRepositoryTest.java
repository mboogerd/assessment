package intragamma.mboogerd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ProductItemStockRepositoryTest {

    private TestEntityManager entityManager;
    private ProductItemRepository repository;

    @Autowired
    public ProductItemStockRepositoryTest(
            TestEntityManager entityManager,
            ProductItemRepository repository) {

        this.entityManager = entityManager;
        this.repository = repository;
    }

    @Test
    void unknownProductImpliesNoStock() {
        assert repository.countByProductIdAndIsReservedFalse(1) == 0;
    }

    @Test
    void unknownProductImpliesNoReservations() {
        assert repository.countByProductIdAndIsReservedTrue(1) == 0;
    }

    @Test
    void nonReservedProductItemIsAvailable() {
        Long productId = 1L;
        ProductItem productItem = new ProductItem(0L, productId, false);
        entityManager.persist(productItem);
        entityManager.flush();

        assert repository.countByProductIdAndIsReservedFalse(productId) == 1;
        assert repository.countByProductIdAndIsReservedTrue(productId) == 0;
    }

    @Test
    void reservedProductItemIsUnavailable() {
        Long productId = 1L;
        ProductItem productItem = new ProductItem(0L, productId, true);
        entityManager.persist(productItem);
        entityManager.flush();

        assert repository.countByProductIdAndIsReservedFalse(productId) == 0;
        assert repository.countByProductIdAndIsReservedTrue(productId) == 1;
    }

}
