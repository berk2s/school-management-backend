package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EditingAnnouncementDto {

    @Size(min = 2, max = 300)
    private String announcementTitle;

    @Size(max = 99999)
    private String announcementDescription;

    @Size(max = 99)
    private List<String> removedChannels;

    @Size(max = 99)
    private List<String> addedChannels;

    private Boolean announcementStatus;

    private Long organizationId;


}
