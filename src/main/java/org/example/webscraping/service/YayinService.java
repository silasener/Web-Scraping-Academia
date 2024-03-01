package org.example.webscraping.service;

import org.example.webscraping.domain.Yayin;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface YayinService {
    void yayinCek(String anahtarKelime);

    List<Yayin> yayinlarigoruntule();

    List<String> yazarlariGoruntule();

    List<String> eserAdlariniGoruntule();

    List<Yayin> yayinlariTariheGoreSirala(String siralamaTipi,List<Yayin> filtreliListe);

    List<String> yayinciAdlariniGoruntule();

}
