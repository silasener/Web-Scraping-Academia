package org.example.webscraping.service;

import org.example.webscraping.domain.MakaleTerimleri;
import org.example.webscraping.domain.Yayin;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface YayinService {
    void yayinCek(String anahtarKelime);

    List<Yayin> yayinlarigoruntule();

    List<String> yazarlariGoruntule();

    List<String> eserAdlariniGoruntule();

    List<String> yayinciAdlariniGoruntule();

    List<MakaleTerimleri> anahtarKelimeyiBarindiranMakaleler(String anahtarKelime);

    List<String> makaleninAnahtarKelimeleri(String yayinId);

    List<Yayin>anahtarKelimeListesineGoreYayinlar(List<String> makaleTerimleriList);

}
