package org.dava.mapper;

import org.dava.domain.File;
import org.dava.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper responsible for converting between {@link File} entity
 * and its corresponding DTO {@link FileResponse}.
 */
@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target="contentType", source = "contentType")
    FileResponse toFileResponse(File file);

    List<FileResponse> toFileResponseList(List<File> files);
}