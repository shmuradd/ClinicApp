package bda.Clinics.util.location;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class CoordinatesResponse {
    private double latitude;
    private double longitude;
    private String address;
    private String error;
}
