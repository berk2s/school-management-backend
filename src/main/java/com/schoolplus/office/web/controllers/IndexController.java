package com.schoolplus.office.web.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Index Controller", description = "Exposes index endpoints")
@RequestMapping("/")
@RestController
public class IndexController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void getIndex() {
    }

}
