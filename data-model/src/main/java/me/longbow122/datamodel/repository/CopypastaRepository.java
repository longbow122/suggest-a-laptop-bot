package me.longbow122.datamodel.repository;

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

	// ? JPA does not support delete query methods, need to write our own. JPA only supports find, read, query, count and get. Updates and Deletes will need to be handled ourselves.
	//* Transactional and Modifying annotations are required if we are modifying the database in any way.
	@Transactional
	@Modifying
	@Query("DELETE FROM Copypasta WHERE name = ?1")
	int deleteCopypastaByNameNaturalId(String name);
}
