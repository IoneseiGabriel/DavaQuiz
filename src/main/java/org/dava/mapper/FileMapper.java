package org.dava.mapper;

import java.util.List;
import org.dava.domain.File;
import org.dava.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper responsible for converting between {@link File} entity and its corresponding DTO
 * {@link FileResponse}.
 */
@Mapper(componentModel = "spring")
public interface FileMapper {

  @Mapping(target = "contentType", source = "contentType")
  FileResponse toFileResponse(File file);

  List<FileResponse> toFileResponseList(List<File> files);
}
