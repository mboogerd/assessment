package intragamma.mboogerd;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This represents a unique instance of a particular `product`
 */
@Entity
@Data
public class ProductItem {

    @Id
    @GeneratedValue
    Long id;
    final Long productId;
    Boolean isReserved = false;

}
