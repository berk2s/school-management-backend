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
public class UpdatingAnnouncementDto {

    @Size(min = 2, max = 299)
    @NotNull
    private String announcementTitle;

    @Size(max = 99999)
    private String announcementDescription;

    private List<String> removedChanels;

    private List<String> addedChannels;

}
