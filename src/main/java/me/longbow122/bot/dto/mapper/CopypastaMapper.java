package me.longbow122.bot.dto.mapper;

import me.longbow122.bot.dto.CopypastaDTO;
import me.longbow122.bot.repository.entities.Copypasta;

public class CopypastaMapper {

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
