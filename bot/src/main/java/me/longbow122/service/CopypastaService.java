package me.longbow122.service;

import jakarta.persistence.EntityNotFoundException;
import me.longbow122.datamodel.repository.CopypastaRepository;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.dto.CopypastaDTO;
import me.longbow122.dto.mapper.CopypastaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CopypastaService {

	@Autowired
	private CopypastaRepository copypastaRepository;


	@Transactional
	public Copypasta createCopypasta(CopypastaDTO copypastaDTO) {
		return copypastaRepository.save(CopypastaMapper.toCopypasta(copypastaDTO));
	}

	@Transactional
	public void deleteCopypasta(String name) {
		copypastaRepository.deleteCopypastaByName(name);
	}

	//TODO NEED TO SEE IF WE CAN GET THE RIGHT EXCEPTION TO COME OUT, IDEALLY DATAINTEGRITYVIOLATION! THIS DOES NOT COME OUT IF A CONSTRAINT IS VIOLATED IN THE SAVES

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
