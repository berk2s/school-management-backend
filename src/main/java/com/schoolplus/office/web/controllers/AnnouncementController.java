package com.schoolplus.office.web.controllers;

import com.schoolplus.office.services.AnnouncementService;
import com.schoolplus.office.utils.SortingUtils;
import com.schoolplus.office.web.models.AnnouncementChannel;
import com.schoolplus.office.web.models.AnnouncementDto;
import com.schoolplus.office.web.models.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Announcement Controller", description = "Exposes endpoint for clients")
@RequiredArgsConstructor
@RequestMapping(AnnouncementController.ENDPOINT)
@RestController
public class AnnouncementController {

    public static final String ENDPOINT = "/announcements";

    private final AnnouncementService announcementService;

    @Operation(summary = "Get Announcements By Organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Announcements are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Organization was not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AnnouncementDto>> getAnnouncementsByOrganization(@PathVariable Long organizationId,
                                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                                @RequestParam(defaultValue = "10") Integer size,
                                                                                @RequestParam(defaultValue = "createdAt") String sort,
                                                                                @RequestParam(defaultValue = "asc") String order,
                                                                                @RequestParam(defaultValue = "STUDENTS") AnnouncementChannel announcementChannel) {
        return new ResponseEntity<>(announcementService.getAnnouncementsByOrganizationAndChannel(organizationId,
                PageRequest.of(page, size, SortingUtils.generateSort(sort, order)), announcementChannel), HttpStatus.OK);
    }

}
