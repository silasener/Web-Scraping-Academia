package org.example.webscraping.service;

import org.example.webscraping.domain.Yayin;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface YayinService {
    void yayinCek( String keyword);

    List<Yayin>yayinlariTariheGoreSirala();



}
