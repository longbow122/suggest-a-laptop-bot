package me.longbow122.datamodel.repository;

import jakarta.persistence.EntityNotFoundException;
import me.longbow122.datamodel.repository.entities.Copypasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CopypastaRepository extends JpaRepository<Copypasta, String> {

	//TODO OUR TEST SEEMS TO BE WORKING PROPERLY USING IN-MEMORY WITH PROPERTIES
	// NEED TO ENSURE WE CAN USE FILE-BASED FOR OURSELVES SOMEHOW, OR WORST-CASE, GCP SQL
	// NEED TO ENSURE THAT THIS IS USED IN A REAL USE-CASE IN PRODUCTION.
	// THIS NEEDS TO BE TESTED WITH THE INTRODUCTION OF THE BOT MODULE

	Optional<Copypasta> findCopypastaByName(String name);

	List<Copypasta> findCopypastaByNameStartingWith(String name);

	// ? JPA does not support delete query methods, need to write our own. JPA only supports find, read, query, count and get. Updates and Deletes will need to be handled ourselves.
	//* Transactional and Modifying annotations are required if we are modifying the database in any way.
	@Modifying
	@Query("DELETE FROM Copypasta WHERE name = ?1")
	int deleteCopypastaByNameNaturalId(String name);

	@Transactional
	default void deleteCopypastaByName(String name) {
		if (deleteCopypastaByNameNaturalId(name) == 0) {
			throw new EntityNotFoundException("No Copypasta with name " + name + " exists.");
		}
	}
}
