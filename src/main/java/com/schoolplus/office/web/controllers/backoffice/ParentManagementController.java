package com.schoolplus.office.web.controllers.backoffice;

import com.schoolplus.office.services.ParentService;
import com.schoolplus.office.web.models.CreatingParentDto;
import com.schoolplus.office.web.models.EditingParentDto;
import com.schoolplus.office.web.models.ParentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(ParentManagementController.ENDPOINT)
@RestController
public class ParentManagementController {

    public final static String ENDPOINT = "/management/parents";

    private final ParentService parentService;

    @GetMapping(value = "/{parentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParentDto> getParent(@Valid @PathVariable UUID parentId) {
        return new ResponseEntity<>(parentService.getParent(parentId), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParentDto> handleCreatingParent(@Valid @RequestBody CreatingParentDto creatingParent) {
        return new ResponseEntity<>(parentService.createParent(creatingParent), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{parentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity handleEditingParent(@Valid @PathVariable UUID parentId,
                                              @Valid @RequestBody EditingParentDto editingParent) {
        parentService.updateParent(parentId, editingParent);

        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                .header(HttpHeaders.LOCATION, ENDPOINT + "/" + parentId.toString())
                .build();
    }

}
