package com.blog.alcoholblog.mapper;

import com.blog.alcoholblog.dto.WineResponseDTO;
import com.blog.alcoholblog.model.Wine;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface WineMapper {

    WineResponseDTO toWineResponseDTO(Wine wine);

    List<WineResponseDTO> toWineResponseDTOList(List<Wine> wines);
}
