package space.gavinklfong.forex.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "customer")
public class Customer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private Integer tier;
	
	public Customer() {
		super();
	}
	
	public Customer(Long id) {
		this.id = id;
	}
	
	public Customer(String name, Integer tier) {
		super();
		this.name = name;
		this.tier = tier;
	}

	public Customer(Long id, String name, Integer tier) {
		super();
		this.id = id;
		this.name = name;
		this.tier = tier;
	}

}
