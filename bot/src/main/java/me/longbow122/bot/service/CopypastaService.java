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

	//TODO COULD WE CLEAN THESE UPDATE METHODS UP TO BE A SINGULAR METHOD?

	public void updateCopypastaName(String oldName, String newName) {
		Copypasta pasta = copypastaRepository.findCopypastaByName(oldName).orElseThrow(() ->
			new EntityNotFoundException("Copypasta with name: " + oldName + " does not exist!"));
		pasta.setName(newName);
		copypastaRepository.save(pasta);
	}

	public void updateCopypastaDescription(String oldName, String newDescription) {
		Copypasta pasta = copypastaRepository.findCopypastaByName(oldName).orElseThrow(() ->
			new EntityNotFoundException("Copypasta with name: " + oldName + " does not exist"));
		pasta.setDescription(newDescription);
		copypastaRepository.save(pasta);
	}

	public void updateCopypastaMessage(String oldName, String newMessage) {
		Copypasta pasta = copypastaRepository.findCopypastaByName(oldName).orElseThrow(() ->
			new EntityNotFoundException("Copypasta with name: " + oldName + " does not exist"));
		pasta.setMessage(newMessage);
		copypastaRepository.save(pasta);
	}

}
