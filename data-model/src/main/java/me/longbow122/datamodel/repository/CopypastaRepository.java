package me.longbow122.datamodel.repository;

import me.longbow122.datamodel.repository.entities.Copypasta;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CopypastaRepository extends JpaRepository<Copypasta, String> {
	//TODO ALL WELL AND FINE IF THIS WORKS AS INTENDED IN-MEMORY
	//TODO BUT WE NEED TO ENSURE THIS MAKES A DB SOMEWHERE THAT WE CAN ACCESS AND USE

	Copypasta findCopypastaByName(String name);

	List<Copypasta> findCopypastaByNameStartingWith(String name);

	void deleteCopypastaByName(String name);

	//* I don't like this. Shouldn't have to be overriding the default interface impl to get this thrown. I'm not sure if this is the best way to do this.
	@Override
	default <S extends Copypasta> S save(S entity) {
		if (existsById(entity.getName())) {
			throw new DataIntegrityViolationException("Such an entity already exists.");
		}
		return saveAndFlush(entity);
	}
}
