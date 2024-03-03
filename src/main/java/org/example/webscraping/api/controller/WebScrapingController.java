package org.example.webscraping.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.webscraping.domain.MakaleTerimleri;
import org.example.webscraping.domain.Yayin;
import org.example.webscraping.repo.MakaleTerimleriRepo;
import org.example.webscraping.repo.YayinRepo;
import org.example.webscraping.service.YayinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@RestController
public class WebScrapingController {
    @Autowired
    private YayinService yayinService;

    @Autowired
    private YayinRepo yayinRepo;

    @Autowired
    private MakaleTerimleriRepo makaleTerimleriRepo;


    @GetMapping("/anahtarKelimeyeGoreYayinCek")
    public ResponseEntity<List<Yayin>> getyayin(@RequestParam String anahtarKelime) {
        yayinService.yayinCek(anahtarKelime);
        List<Yayin> yayinList=yayinService.yayinlarigoruntule();
        return ResponseEntity.ok(yayinList);
    }

    @GetMapping("/anahtarKelimeyeGoreMakaleAra")
    public ResponseEntity<List<MakaleTerimleri>> getanahtarKelimeyeUyanMakaleler(@RequestParam String anahtarKelime) {
        List<MakaleTerimleri> anahtarKelimeyiBarindiranMakaleler=yayinService.anahtarKelimeyiBarindiranMakaleler(anahtarKelime);
        return ResponseEntity.ok(anahtarKelimeyiBarindiranMakaleler);
    }


    @GetMapping("/yayinlariGetir")
    public ResponseEntity<List<Yayin>> getVeritabanindakiYayinlar() {
        List<Yayin> yayinlar=yayinService.yayinlarigoruntule();
        return ResponseEntity.ok(yayinlar);
    }


    @GetMapping("/makaleninAnahtarKelimeleri")
    public ResponseEntity<List<String>> getMakaleninAnahtarKelimeleri(@RequestParam String yayinId) {
        List<String> anahtarKelimeler=yayinService.makaleninAnahtarKelimeleri(yayinId);
        return ResponseEntity.ok(anahtarKelimeler);
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

    @GetMapping("/yazarlariGetir")
    public ResponseEntity<List<String>> getVeritabanindakiYazarlar() {
        List<String> yazarlar = yayinService.yazarlariGoruntule();
        return ResponseEntity.ok(yazarlar);
    }

    @GetMapping("/eserAdlariniGetir")
    public ResponseEntity<List<String>> getVeritabanindakiEserAdlari() {
        List<String> eserAdlari=yayinService.eserAdlariniGoruntule();
        return ResponseEntity.ok(eserAdlari);
    }
    

    @GetMapping("/yayinciAdlariniGetir")
    public ResponseEntity<List<String>> getYayinciAdlari() {
        List<String> yayinciAdiList = yayinService.yayinciAdlariniGoruntule();
        return ResponseEntity.ok(yayinciAdiList);
    }




}


