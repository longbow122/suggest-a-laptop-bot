package me.longbow122.service;

import jakarta.persistence.EntityNotFoundException;
import me.longbow122.datamodel.repository.CopypastaRepository;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.dto.CopypastaDTO;
import me.longbow122.dto.mapper.CopypastaMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@ComponentScan(basePackages = "me.longbow122.datamodel")
public class CopypastaService {

	private final CopypastaRepository copypastaRepository;

	//@Autowired
	public CopypastaService(CopypastaRepository copypastaRepository) {
		this.copypastaRepository = copypastaRepository;
	}

	//TODO DO WE NEED TO BE TESTING THESE METHODS? I THINK WE SHOULD, BUT IF WE DID, WE WOULD BE MOCKING THE CORE OF IT
	// MAKING THE TEST EFFECTIVELY USELESS, SINCE THE SERVICE IS JUST MIMICKING REPOSITORY BEHAVIOUR ELSEWHERE.
	// UNSURE IF THERE IS A NEED TO TEST, CAN JUDGE THROUGH OTHER CHECKS FOR CODE COVERAGE.


	@Transactional
	public Copypasta createCopypasta(CopypastaDTO copypastaDTO) {
		return copypastaRepository.save(CopypastaMapper.toCopypasta(copypastaDTO));
	}

	@Transactional
	public void deleteCopypasta(String name) {
		copypastaRepository.deleteCopypastaByName(name);
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
