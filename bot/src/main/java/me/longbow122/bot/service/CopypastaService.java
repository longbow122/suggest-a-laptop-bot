package me.longbow122.bot.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import me.longbow122.datamodel.repository.CopypastaRepository;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.bot.dto.CopypastaDTO;
import me.longbow122.bot.dto.mapper.CopypastaMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@ComponentScan(basePackages = "me.longbow122.datamodel")
public class CopypastaService {

	private final CopypastaRepository copypastaRepository;

	public CopypastaService(CopypastaRepository copypastaRepository) {
		this.copypastaRepository = copypastaRepository;
	}

	@Transactional
	public Copypasta createCopypasta(CopypastaDTO copypastaDTO) {
		Copypasta created = CopypastaMapper.toCopypasta(copypastaDTO);
		if (copypastaRepository.existsById(created.getName())) {
			throw new EntityExistsException("Copypasta with name " + created.getName() + " already exists");
		}
		return copypastaRepository.save(created);
	}

	@Transactional
	public void deleteCopypasta(String name) {
		if (copypastaRepository.deleteCopypastaByNameNaturalId(name) == 0) {
			throw new EntityNotFoundException("Copypasta with name " + name + " does not exist");
		}
	}

	@Transactional
	public List<Copypasta> findAllCopypasta() {
		return copypastaRepository.findAll();
	}

	@Transactional
	public List<Copypasta> findAllCopypastaStartsWith(String name) {
		return copypastaRepository.findCopypastaByNameStartingWith(name);
	}

	@Transactional
	public Optional<Copypasta> findCopypastaByName(String name) {
		return copypastaRepository.findCopypastaByName(name);
	}

	//TODO CAN WE IMPROVE THIS TO THROW THE RIGHT EXCEPTION, REMOVING SOME MORE LOGIC FROM THE COMMAND LISTENER?
	// NEEDS THE RIGHT TESTING TO BE DONE ON IT BEFORE WE CAN ENSURE THAT THE RIGHT EXCEPTION IS THROWN

	public void updateCopypasta(String currentName, CopypastaUpdateType updateType, String updatedValue) {
		Copypasta pasta = copypastaRepository.findCopypastaByName(currentName).orElseThrow(() ->
			new EntityNotFoundException("Copypasta with name " + currentName + " does not exist"));
		switch (updateType) {
			case NAME -> pasta.setName(updatedValue);
			case DESCRIPTION -> pasta.setDescription(updatedValue);
			case MESSAGE -> pasta.setMessage(updatedValue);
		}
		copypastaRepository.save(pasta);
	}

}
