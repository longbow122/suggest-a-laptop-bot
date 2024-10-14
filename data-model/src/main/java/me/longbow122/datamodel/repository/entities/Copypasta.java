package me.longbow122.datamodel.repository.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "copypasta", indexes = @Index(columnList = "name"), uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Copypasta {

	//TODO NEED TO INVESTIGATE THIS AT SOME POINT. WE DO NOT NEED OR EVEN USE A LONG ID, BUT CONSTRAINTS SEEM TO BE BROKEN IN A STRANGE WAY WITHOUT THE ID.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@NaturalId(mutable = true)
	@Column(unique = true, nullable = false, length = 32)
	@NotNull(message = "Name cannot be null")
	@NotBlank(message = "Name is required")
	@Size(min = 1)
	@Getter
	@Setter
	private String name;

	@Column(nullable = false, length = 100)
	@NotNull(message = "Description cannot be null")
	@NotBlank(message = "Description is required")
	@Size(min = 1)
	@Getter
	@Setter
	private String description;

	@Column(nullable = false, length = 2000)
	@NotNull(message = "Message cannot be null")
	@NotBlank(message = "Message is required")
	@Size(min = 1)
	@Getter
	@Setter
	private String message;

	public Copypasta(String name, String description, String message) {
		this.name = name;
		this.description = description;
		this.message = message;
	}

	public Copypasta() {}
}
