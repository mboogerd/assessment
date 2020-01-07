package intragamma.mboogerd;

import lombok.Value;
import lombok.With;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * This represents a unique instance of a particular `product`
 */
@Entity
@Value
public class ProductItem {

    @Id
    @GeneratedValue
    Long id;

    @With
    Long productId;

    @With
    Boolean isReserved;

}
