package com.schoolplus.office.web.mappers;

import com.schoolplus.office.domain.Continuity;
import com.schoolplus.office.web.models.ContinuityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {StudentMapper.class, ClassroomMapper.class, SyllabusMapper.class, OrganizationMapper.class})
public interface ContinuityMapper {

    @Mappings({
            @Mapping(target = "continuityId", expression = "java( continuity.getId().toString() )"),
    })
    ContinuityDto continuityToContinuityDto(Continuity continuity);

    @Mappings({
            @Mapping(target = "continuityId", expression = "java( continuity.getId().toString() )"),
    })
    List<ContinuityDto> continuityToContinuityDto(List<Continuity> continuity);

}
