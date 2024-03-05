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

    @GetMapping("/uniqueAnahtarKelimeListesi")
    public ResponseEntity<List<String>> getanahtarKelimeler() {
        List<String> anahtarKelimeler=yayinService.anahtarKelimeList();
        return ResponseEntity.ok(anahtarKelimeler);
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

    @GetMapping("/yayinDetayGetir")
    public ResponseEntity<Yayin> getYayinDetay(@RequestParam String yayinId) {
        Yayin yayinDetay = yayinService.yayinaAitDetaylariGetir(yayinId);
        return ResponseEntity.ok(yayinDetay);
    }


    @GetMapping("/yayinTuruListesi")
    public ResponseEntity<List<String>> getYayinTurleri() {
        List<String> yayinTurList = yayinService.yayinTurList();
        return ResponseEntity.ok(yayinTurList);
    }

    @GetMapping("/pdfIndir")
    public ResponseEntity<?> downloadPdf(@RequestParam String pdfLink) {
        String fullPdfUrl = "https://link.springer.com" + pdfLink;
        return yayinService.downloadPdf(fullPdfUrl);
    }

    @GetMapping("/yanlisKelimeyeGoreMakaleAra")
    public ResponseEntity<?> searchByKeyword(@RequestParam String anahtarKelime) {
        List<MakaleTerimleri> publications =yayinService.yanlisKelimeyeEnUygunMakaleler(anahtarKelime);
        return  ResponseEntity.ok(publications);
    }

    @GetMapping("/enUygunKelime")
    public ResponseEntity<?> benzerligiYuksekOlanAnahtarKelime() {
        String bulunanKelime =yayinService.enUygunAnahtarKelime();
        return  ResponseEntity.ok(bulunanKelime);
    }




}


