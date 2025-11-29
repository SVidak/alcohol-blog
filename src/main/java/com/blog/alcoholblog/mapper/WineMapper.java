package com.blog.alcoholblog.mapper;

import com.blog.alcoholblog.dto.CreateWineRequestDTO;
import com.blog.alcoholblog.dto.UpdateWineRequestDTO;
import com.blog.alcoholblog.dto.WineResponseDTO;
import com.blog.alcoholblog.model.Wine;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring")
public interface WineMapper {

    WineResponseDTO toWineResponseDTO(Wine wine);

    @Mapping(target = "id", ignore = true)
    Wine toWine(CreateWineRequestDTO createWineRequestDTO);

    List<WineResponseDTO> toWineResponseDTOList(List<Wine> wines);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateWineFromDTO(UpdateWineRequestDTO updateWineRequestDTO, @MappingTarget Wine wine);

}
