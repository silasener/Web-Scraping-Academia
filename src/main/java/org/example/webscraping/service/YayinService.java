package org.example.webscraping.service;

import org.example.webscraping.domain.MakaleTerimleri;
import org.example.webscraping.domain.Yayin;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface YayinService {
    void yayinCek(String anahtarKelime);

    List<Yayin> yayinlarigoruntule();

    List<String> yazarlariGoruntule();

    List<String> eserAdlariniGoruntule();

    List<String> yayinciAdlariniGoruntule();

    Yayin yayinaAitDetaylariGetir(String yayinId);

    List<MakaleTerimleri> anahtarKelimeyiBarindiranMakaleler(String anahtarKelime);

    List<String> makaleninAnahtarKelimeleri(String yayinId);

    List<String> anahtarKelimeList();

    List<String> yayinTurList();

    ResponseEntity<ByteArrayResource> downloadPdf(String pdfUrl);

    List<MakaleTerimleri> yanlisKelimeyeEnUygunMakaleler(String benzerAnahtarKelime);

    String enUygunAnahtarKelime();

    List<MakaleTerimleri> uniqueEserler(List<MakaleTerimleri> makaleTerimleriList);

}
