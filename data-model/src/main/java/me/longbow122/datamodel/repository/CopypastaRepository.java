package me.longbow122.datamodel.repository;

import jakarta.persistence.EntityExistsException;
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

	Optional<Copypasta> findCopypastaByName(String name);

	List<Copypasta> findCopypastaByNameStartingWith(String name);

	//TODO IS THE IMPLEMENTATION OF THIS METHOD A GOOD IDEA? FEELS STUPID TO HAVE IN HERE AND OVERALL LOOKS LIKE A BAD IDEA TO HAVE
	// THIS FEELS LIKE IT IS BREAKING PATTERNS, AND LOOKS LIKE A METHOD THAT WOULD BE IN THE SERVICE LAYER RATHER THAN THE REPOSITORY LAYER.
	@Override
	default <S extends Copypasta> S save(S entity) {
		if (findCopypastaByName(entity.getName()).isPresent()) {
			throw new EntityExistsException(entity.getName() + " already exists!");
		}
		return saveAndFlush(entity);
	}

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
