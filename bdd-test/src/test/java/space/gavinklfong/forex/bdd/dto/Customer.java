package space.gavinklfong.forex.bdd.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Customer {
    long id;
    String name;
    int tier;
}
