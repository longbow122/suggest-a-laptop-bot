package me.longbow122.datamodel.repository.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@AllArgsConstructor
@Getter
@Table(name = "copypasta")
public class Copypasta {

	@Id
	@Column(unique = true, nullable = false, length = 32)
	private String name;

	@Column(nullable = false, length = 100)
	private String description;

	@Column(nullable = false, length = 2000)
	private String message;

	//TODO NOT REALLY NEEDED, REMOVE WHEN TESTS ARE DONE
	@Override
	public String toString() {
		return "Name: " + name + "\nDescription: " + description + "\nMessage: " + message;
	}

	public Copypasta() {}
}
