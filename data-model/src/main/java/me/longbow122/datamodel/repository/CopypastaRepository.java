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

	//TODO UPDATING IN THIS MANNER SHOULD REALLY BE DONE IN THE SERVICE LAYER!! NEED TO MOVE THINGS AROUND AND PUT IT THERE WHEN DONE!
	// THIS IS PERFORMING A GOOD AMOUNT OF LOGIC AND IS NOW STRAYING AWAY FROM THE USUAL CRUD-ONLY MANNER WE WANT TO MAINTAIN HERE.
	// AS SUCH, WE SHOULD MOVE THIS INTO THE SERVICE LAYER ASAP, ALONG WITH OTHER RELEVANT TESTS.

	//TODO THE WAY WE HAVE IMPLEMENTED THIS IS NOT GREAT (AND IS VERY SERVICE-LIKE). COULD WE RE-FACTOR TO JUST HAVE ONE UPDATE METHOD
	// HANDLING EVERYTHING SINCE WE ARE USING SETTERS HERE?

	@Transactional
	default void updateCopypastaNameByName(String name, String newName) {
		Copypasta pasta = findCopypastaByName(name).orElseThrow(() ->
			new EntityNotFoundException("Copypasta with name: " + name + " does not exist"));
		pasta.setName(newName);
		save(pasta);
	}

	@Transactional
	default void updateCopypastaDescriptionByName(String name, String newDescription) {
		Copypasta pasta = findCopypastaByName(name).orElseThrow(() ->
			new EntityNotFoundException("Copypasta with name: " + name + " does not exist"));
		pasta.setDescription(newDescription);
		save(pasta);
	}

	//TODO NEED TO SEE IF WE CAN GET THE RIGHT EXCEPTION TO COME OUT, IDEALLY DATAINTEGRITYVIOLATION! THIS DOES NOT COME OUT IF A CONSTRAINT IS VIOLATED IN THE SAVES

	@Transactional
	default void updateCopypastaMessageByName(String name, String newMessage) {
		Copypasta pasta = findCopypastaByName(name).orElseThrow(() ->
			new EntityNotFoundException("Copypasta with name: " + name + " does not exist"));
		pasta.setMessage(newMessage);
		save(pasta);
	}
}
