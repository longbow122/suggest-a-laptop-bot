package me.longbow122.datamodel.repository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import me.longbow122.datamodel.repository.entities.Copypasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CopypastaRepository extends JpaRepository<Copypasta, String> {
	//TODO ALL WELL AND FINE IF THIS WORKS AS INTENDED IN-MEMORY
	//TODO BUT WE NEED TO ENSURE THIS MAKES A DB SOMEWHERE THAT WE CAN ACCESS AND USE

	//TODO OUR TEST SEEMS TO BE WORKING PROPERLY USING IN-MEMORY WITH PROPERTIES
	// NEED TO ENSURE WE CAN USE FILE-BASED FOR OURSELVES SOMEHOW, OR WORST-CASE, GCP SQL

	Copypasta findCopypastaByName(String name);

	List<Copypasta> findCopypastaByNameStartingWith(String name);

	// ? JPA does not support delete query methods, need to write our own. JPA only supports find, read, query, count and get. Updates and Deletes will need to be handled ourselves.
	//* Transactional and Modifying annotations are required if we are modifying the database in anyways.
	@Modifying
	@Query("DELETE FROM Copypasta WHERE name = ?1")
	int deleteCopypastaByNameNaturalId(String name);

	@Transactional
	default void deleteCopypastaByName(String name) {
		if (deleteCopypastaByNameNaturalId(name) == 0) {
			throw new EntityNotFoundException("No Copypasta with name " + name + " exists.");
		}
	}

	@Transactional
	@Modifying
	@Query("UPDATE Copypasta c SET c.name = :newName WHERE c.name = :name")
	int updateCopypastaNameByNameNaturalId(@Param("name") String name, @Param("newName") String newName);


	@Transactional
	@Modifying
	@Query("UPDATE Copypasta c SET c.description = :newDescription WHERE c.name = :name")
	int updateCopypastaDescriptionByNameNaturalId(@Param("name") String name, @Param("newDescription") String newDescription);


	@Transactional
	@Modifying
	@Query("UPDATE Copypasta c SET c.message = :newMessage WHERE c.name = :name")
	int updateCopypastaMessageByNameNaturalId(@Param("name") String name, @Param("newMessage") String newMessage);

	@Transactional
	default void updateCopypastaNameByName(String name, String newName) {
		if (findCopypastaByName(name) == null) {
			throw new EntityNotFoundException("Copypasta with name: " + name + " does not exist");
		}
		Copypasta newPasta = findCopypastaByName(name);
		newPasta.setName(newName);
		save(newPasta);
	}

	@Transactional
	default void updateCopypastaDescriptionByName(String name, String newDescription) {
		if (findCopypastaByName(name) == null) {
			throw new EntityNotFoundException("Copypasta with name: " + name + " does not exist!");
		}
		Copypasta newPasta = findCopypastaByName(name);
		newPasta.setDescription(newDescription);
		save(newPasta);
	}

	//TODO DO WE WANT TO BE USING THE NEW WAY, OR THE OLD WAY? THE NEW WAY WORKS, BUT MEANS WHEN WE BREAK CONSTRAINTS WITH AN UPDATE
	// WE DON'T GET THE RIGHT EXCEPTION.

	//TODO THE OLD WAY WORKS, ONLY FOR NULL UPDATES, EMPTY AND WHITESPACES WILL FAIL.

	//TODO NEED TO SEE IF WE CAN GET THE RIGHT EXCEPTION TO COME OUT, IDEALLY DATAINTEGRITYVIOLATION!

	@Transactional
	default void updateCopypastaMessageByName(String name, String newMessage) {
		if (updateCopypastaMessageByNameNaturalId(name, newMessage) == 0) {
			throw new EntityNotFoundException("No Copypasta with name " + name + " exists.");
		}
	}
}
