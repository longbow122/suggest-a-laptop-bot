package me.longbow122.datamodel.repository.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "copypasta", indexes = @Index(columnList = "name"), uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Copypasta {

	//TODO NEED TO INVESTIGATE THIS AT SOME POINT. WE DO NOT NEED OR EVEN USE A LONG ID, BUT CONSTRAINTS SEEM TO BE BROKEN IN A STRANGE WAY WITHOUT THE ID.

	//TODO WE HAVE MANAGED TO REMOVE THIS ID SUCCESSFULLY BUT WE HAVE DONE SO AT THE COST OF A SUPER HACKY IMPLEMENTATION
	// BY OVERRIDING THE SAVE METHOD! HAVE A LOOK AND SEE WHAT NEEDS TO BE DONE ABOUT THIS AND WHETHER DOING THIS IS A GOOD IDEA...
	// WHEN OVERRIDING THE SAVE METHOD, WE ARE DOING SO BY CALLING REPOSITORY-LAYER METHODS INSIDE THE REPOSITORY CLASS. FEELS LIKE BAD PRACTISE.
	/*@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;*/

	@Id
	@Column(unique = true, nullable = false, length = 32)
	@NotNull(message = "Name cannot be null")
	@NotBlank(message = "Name is required")
	@Size(min = 1)
	private String name;

	@Column(nullable = false, length = 100)
	@NotNull(message = "Description cannot be null")
	@NotBlank(message = "Description is required")
	@Size(min = 1)
	private String description;

	@Column(nullable = false, length = 2000)
	@NotNull(message = "Message cannot be null")
	@NotBlank(message = "Message is required")
	@Size(min = 1)
	private String message;

	public Copypasta(String name, String description, String message) {
		this.name = name;
		this.description = description;
		this.message = message;
	}

	public Copypasta() {}
}
