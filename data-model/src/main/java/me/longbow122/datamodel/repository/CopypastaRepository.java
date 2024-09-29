package me.longbow122.datamodel.repository;

import me.longbow122.datamodel.repository.entities.Copypasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CopypastaRepository extends JpaRepository<Copypasta, String> {
	//TODO ALL WELL AND FINE IF THIS WORKS AS INTENDED IN-MEMORY
	//TODO BUT WE NEED TO ENSURE THIS MAKES A DB SOMEWHERE THAT WE CAN ACCESS AND USE

	//TODO OUR TEST SEEMS TO BE WORKING PROPERLY USING IN-MEMORY WITH PROPERTIES
	// NEED TO ENSURE WE CAN USE FILE-BASED FOR OURSELVES SOMEHOW, OR WORST-CASE, GCP SQL

	Copypasta findCopypastaByName(String name);

	List<Copypasta> findCopypastaByNameStartingWith(String name);

	void deleteCopypastaByName(String name);
}
