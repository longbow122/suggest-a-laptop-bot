package me.longbow122.bot.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import me.longbow122.bot.dto.CopypastaDTO;
import me.longbow122.bot.dto.mapper.CopypastaMapper;
import me.longbow122.datamodel.repository.CopypastaRepository;
import me.longbow122.datamodel.repository.entities.Copypasta;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
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

	@Transactional
	public void updateCopypasta(String currentName, CopypastaUpdateType updateType, String updatedValue) {
		if (updatedValue == null) throw new TransactionSystemException("Copypasta updated value cannot be null!");
		Copypasta pasta = copypastaRepository.findCopypastaByName(currentName).orElseThrow(() ->
			new EntityNotFoundException("Copypasta with name " + currentName + " does not exist"));
		switch (updateType) {
			case NAME -> {
				if (copypastaRepository.existsById(updatedValue))
					throw new EntityExistsException("This Copypasta already exists!");
				if (!(validateName(updatedValue))) throw new DataIntegrityViolationException("Invalid Name!");
				//* We are handling the updating of names in a more special way to ensure that we are updating the name as intended. Through this, we remove the old one, and insert a new
				//* Copypasta, with the right name, while removing the old one. We check if something exists before we do the insert, and if it does, we throw an exception.
				copypastaRepository.save(new Copypasta(updatedValue, pasta.getDescription(), pasta.getMessage()));
				copypastaRepository.deleteCopypastaByNameNaturalId(pasta.getName());
				return;
			}
			case DESCRIPTION -> {
				if (!(validateDescription(updatedValue)))
					throw new DataIntegrityViolationException("Invalid Description!");
				pasta.setDescription(updatedValue);
			}
			case MESSAGE -> {
				if (!(validateMessage(updatedValue))) throw new DataIntegrityViolationException("Invalid Message!");
				pasta.setMessage(updatedValue);
			}
		}
		copypastaRepository.save(pasta);
	}

	private boolean validateName(String name) {
		if (name.isBlank() || name.length() > 32) return false;
		for (char i : name.toCharArray()) {
			if (!(Character.isLowerCase(i)) || !(Character.isAlphabetic(i)) || i == ' ') return false;
		}
		return true;
	}

	private boolean validateDescription(String description) {
		return !description.isBlank() && description.length() <= 100;
	}

	private boolean validateMessage(String message) {
		return !message.isBlank() && message.length() <= 2000;
	}

}
