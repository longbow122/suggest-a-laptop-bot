package me.longbow122.bot.service;

import lombok.extern.slf4j.Slf4j;
import me.longbow122.bot.repository.CopypastaRepository;
import me.longbow122.bot.repository.entities.Copypasta;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
public class CopypastaService {

	private final CopypastaRepository copypastaRepository;

	public CopypastaService(CopypastaRepository copypastaRepository) {
		this.copypastaRepository = copypastaRepository;
	}

	public Copypasta createCopypasta(Copypasta copypasta) {
		if (copypasta == null) throw new IllegalArgumentException("Copypasta cannot be null");
		if (copypastaRepository.existsById(copypasta.getName())) {
			throw new IllegalStateException("Copypasta with name " + copypasta.getName() + " already exists");
		}
		return copypastaRepository.save(copypasta);
	}

	public void deleteCopypasta(String name) {
		if (copypastaRepository.deleteCopypastaByNameNaturalId(name) == 0) {
			throw new NoSuchElementException("Copypasta with name " + name + " does not exist");
		}
	}

	public List<Copypasta> findAllCopypasta() {
		return copypastaRepository.findAll();
	}

	public List<Copypasta> findAllCopypastaStartsWith(String name) {
		return copypastaRepository.findCopypastaByNameStartingWith(name);
	}

	public Optional<Copypasta> findCopypastaByName(String name) {
		return copypastaRepository.findCopypastaByName(name);
	}

	public void updateCopypasta(String currentName, CopypastaUpdateType updateType, String updatedValue) {
		if (updatedValue == null) throw new IllegalArgumentException("Copypasta updated value cannot be null!");
		Copypasta pasta = copypastaRepository.findCopypastaByName(currentName).orElseThrow(() ->
			new NoSuchElementException("Copypasta with name " + currentName + " does not exist"));
		switch (updateType) {
			case NAME -> {
				if (copypastaRepository.existsById(updatedValue))
					throw new IllegalStateException("This Copypasta already exists!");
				//* We are handling the updating of names in a more special way to ensure that we are updating the name as intended. Through this, we remove the old one, and insert a new
				//* Copypasta, with the right name, while removing the old one. We check if something exists before we do the insert, and if it does, we throw an exception.
				copypastaRepository.replaceCopypasta(pasta.getName(), new Copypasta(updatedValue, pasta.getDescription(), pasta.getMessage()));
				return;
			}
			case DESCRIPTION -> {
				pasta.setDescription(updatedValue);
			}
			case MESSAGE -> {
				pasta.setMessage(updatedValue);
			}
		}
		copypastaRepository.save(pasta);
	}

}
