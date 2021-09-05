package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.AnnouncementService;
import com.schoolplus.office.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


@Tag(name = "Announcements Management Controller", description = "Exposes announcements management endpoints")
@RequiredArgsConstructor
@RequestMapping(AnnouncementManagementController.ENDPOINT)
@RestController
public class AnnouncementManagementController {

    public final static String ENDPOINT = "/management/announcements";

    private final AnnouncementService announcementService;

    @Operation(summary = "Get Announcements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Announcements are listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
            }),
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnnouncementDto>> getAnnouncements(@RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "5") Integer size) {
        return new ResponseEntity<>(announcementService.getAnnouncements(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @Operation(summary = "Get Announcement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Announcement info is listed"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Announcement was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @GetMapping(value = "/{announcementId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnnouncementDto> getAnnouncement(@Valid @PathVariable Long announcementId) {
        return new ResponseEntity<>(announcementService.getAnnouncement(announcementId), HttpStatus.OK);
    }

    @Operation(summary = "Create Announcement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Announcement is created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnnouncementDto> createAnnouncement(@Valid @RequestBody CreatingAnnouncementDto creatingAnnouncementDto) {
        return new ResponseEntity<>(announcementService.createAnnouncement(creatingAnnouncementDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Upload Images To Announcement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image is uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Announcement was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @PostMapping(value = "/{announcementId}/upload/images",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnnouncementImageDto>> uploadImages(@Valid @PathVariable Long announcementId,
                                                                   @Valid @RequestParam("images") MultipartFile[] images) {
        return new ResponseEntity<>(announcementService.uploadImages(announcementId, images),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update Announcement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image is uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Announcement was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @PutMapping(value = "/{announcementId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateAnnouncement(@Valid @PathVariable Long announcementId,
                                                                         @Valid @RequestBody UpdatingAnnouncementDto updatingAnnouncement) {
        announcementService.updateAnnouncement(announcementId, updatingAnnouncement);

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + announcementId)
                .build();
    }

    @Operation(summary = "Delete Announcement Images")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Announcement was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @PutMapping(value = "/{announcementId}/delete/images", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImages(@Valid @PathVariable Long announcementId,
                             @Valid @RequestBody DeletingAnnouncementImageDto deletingAnnouncementImage) {
        announcementService.deleteImages(announcementId, deletingAnnouncementImage);
    }

    @Operation(summary = "Delete Announcement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Announcement is deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input or malformed data",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Announcement was not found",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "403", description = "Don't have permission"),
    })
    @DeleteMapping("/{announcementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAnnouncement(@Valid @PathVariable Long announcementId) {
        announcementService.deleteAnnouncement(announcementId);
    }

}
