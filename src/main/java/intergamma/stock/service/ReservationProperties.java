package intergamma.stock.service;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@Value
@ConfigurationProperties("reservation")
@ConstructorBinding
public class ReservationProperties {
    Duration reservationDuration;
}
