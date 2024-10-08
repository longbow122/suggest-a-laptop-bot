package me.longbow122.dto.mapper;

import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.dto.CopypastaDTO;

public class CopypastaMapper {

	//TODO SHOULD THIS MAPPER CLASS CONTAIN STATIC METHODS? SEEMS ODD TO INCLUDE AND USE STATIC METHODS HERE?
	// WE SHOULD LOOK INTO THIS?

	private CopypastaMapper() {
		throw new IllegalStateException("This is a mapper class!");
	}

	public static CopypastaDTO toDTO(Copypasta copypasta) {
		return new CopypastaDTO(copypasta.getName(), copypasta.getDescription(), copypasta.getMessage());
	}


	public static Copypasta toCopypasta(CopypastaDTO copypastaDTO) {
		return new Copypasta(copypastaDTO.name(), copypastaDTO.description(), copypastaDTO.message());
	}
}
