package org.example.webscraping.api.controller;

import org.example.webscraping.domain.Yayin;
import org.example.webscraping.repo.YayinRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebScrapingController {

    private  YayinRepo yayinRepo;

    @Autowired
    public WebScrapingController(YayinRepo yayinRepo) {
        this.yayinRepo = yayinRepo;
    }


    @GetMapping("/yayin")
    public ResponseEntity<List<Yayin>> getAllYayin(){
        return ResponseEntity.ok(this.yayinRepo.findAll());
    }


}


