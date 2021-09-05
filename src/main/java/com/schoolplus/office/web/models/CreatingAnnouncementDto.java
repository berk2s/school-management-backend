package com.schoolplus.office.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatingAnnouncementDto {

    @Size(min = 2, max = 299)
    @NotNull
    private String announcementTitle;

    @Size(max = 99999)
    private String announcementDescription;

    @Size(min = 1)
    private List<String> announcementChannels;

}
