package org.example.webscraping.api.controller;

import org.example.webscraping.domain.MakaleTerimleri;
import org.example.webscraping.domain.Yayin;
import org.example.webscraping.repo.MakaleTerimleriRepo;
import org.example.webscraping.repo.YayinRepo;
import org.example.webscraping.service.YayinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WebScrapingController {
    @Autowired
    private YayinService yayinService;

    @Autowired
    private YayinRepo yayinRepo;

    @Autowired
    private MakaleTerimleriRepo makaleTerimleriRepo;


    @GetMapping("/yayin")
    public ResponseEntity<String> getAllYayin() {
        yayinService.yayinCek();
        return ResponseEntity.ok("Yayın çekildi");
    }


    @GetMapping("/yayinçek2")
    public ResponseEntity<String> getAllYayinCek2() {
        yayinService.yayinCek2();


        return ResponseEntity.ok("Yayın çekildi");
    }

    @PostMapping("/yayinEkle/{yayinId}/{yayinAdi}")
    public ResponseEntity<String> yayinEkle(@PathVariable String yayinId, @PathVariable String yayinAdi) {
        try {
            Yayin yayin = new Yayin(yayinId, yayinAdi);
            yayinRepo.save(yayin);

           MakaleTerimleri makaleTerimleri=new MakaleTerimleri();
           makaleTerimleri.setYayin(yayin);
           makaleTerimleri.setAnahtarKelime("uzay bilim eğlence");

            makaleTerimleriRepo.save(makaleTerimleri);
            return ResponseEntity.ok("Yayın eklendi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Yayın eklenirken bir hata oluştu.");
        }
    }




}


