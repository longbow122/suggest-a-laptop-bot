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

	//TODO UPDATING IN THIS MANNER SHOULD REALLY BE DONE IN THE SERVICE LAYER!! NEED TO MOVE THINGS AROUND AND PUT IT THERE WHEN DONE!
	// THIS IS PERFORMING A GOOD AMOUNT OF LOGIC AND IS NOW STRAYING AWAY FROM THE USUAL CRUD-ONLY MANNER WE WANT TO MAINTAIN HERE.
	// AS SUCH, WE SHOULD MOVE THIS INTO THE SERVICE LAYER ASAP, ALONG WITH OTHER RELEVANT TESTS.

	//TODO THE WAY WE HAVE IMPLEMENTED THIS IS NOT GREAT (AND IS VERY SERVICE-LIKE). COULD WE RE-FACTOR TO JUST HAVE ONE UPDATE METHOD
	// HANDLING EVERYTHING SINCE WE ARE USING SETTERS HERE?

	@Transactional
	default void updateCopypastaNameByName(String name, String newName) {
		Optional<Copypasta> pasta = findCopypastaByName(name);
		if (pasta.isEmpty()) {
			throw new EntityNotFoundException("Copypasta with name: " + name + " does not exist");
		}
		Copypasta newPasta = pasta.get();
		newPasta.setName(newName);
		save(newPasta);
	}

	@Transactional
	default void updateCopypastaDescriptionByName(String name, String newDescription) {
		Optional<Copypasta> pasta = findCopypastaByName(name);
		if (pasta.isEmpty()) {
			throw new EntityNotFoundException("Copypasta with name: " + name + " does not exist!");
		}
		Copypasta newPasta = pasta.get();
		newPasta.setDescription(newDescription);
		save(newPasta);
	}

	//TODO NEED TO SEE IF WE CAN GET THE RIGHT EXCEPTION TO COME OUT, IDEALLY DATAINTEGRITYVIOLATION!

	//TODO THE WAY OF USING SAVES IS IDEAL, BUT IS NOT WHAT WE ARE MEANT TO BE DOING IN THE REPOSITORY LAYER! WE NEED TO MOVE THIS LOGIC
	// TO THE SERVICE LAYER, ALONG WITH THE TESTS

	@Transactional
	default void updateCopypastaMessageByName(String name, String newMessage) {
		Optional<Copypasta> pasta = findCopypastaByName(name);
		if (pasta.isEmpty()) {
			throw new EntityNotFoundException("Copypasta with name: " + name + " does not exist!");
		}
		Copypasta newPasta = pasta.get();
		newPasta.setMessage(newMessage);
		save(newPasta);
	}
}
