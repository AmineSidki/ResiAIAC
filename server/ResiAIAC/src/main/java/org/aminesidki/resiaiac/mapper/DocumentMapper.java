package org.aminesidki.resiaiac.mapper;

import org.aminesidki.resiaiac.dto.DocumentDto;
import org.aminesidki.resiaiac.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "proprietaire.id", target = "proprietaireId")
    DocumentDto toDto(Document entity);

    @Mapping(   target = "proprietaire", ignore = true)
    Document toEntity(DocumentDto dto);
}
