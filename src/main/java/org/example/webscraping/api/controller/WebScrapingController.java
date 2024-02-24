package org.example.webscraping.api.controller;

import org.example.webscraping.domain.Yayin;
import org.example.webscraping.repo.YayinRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/yayinEkle/{yayinId}/{yayinAdi}")
    public ResponseEntity<String> yayinEkle(@PathVariable int yayinId,@PathVariable String yayinAdi) {
        try {
            Yayin yayin=new Yayin(yayinId,yayinAdi);
            yayinRepo.save(yayin);
            return ResponseEntity.ok("Yayın eklendi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Yayın eklenirken bir hata oluştu.");
        }
    }


}


