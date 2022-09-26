package com.baghdadfocusit.webshop3d.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class VersionController {

    @Value("${3d.version}")
    private String appVersion;

    @GetMapping("version")
    public ResponseEntity<String> getVersion() {
        return new ResponseEntity<>(appVersion, HttpStatus.OK);
    }
}
