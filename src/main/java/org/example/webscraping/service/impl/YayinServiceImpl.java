package org.example.webscraping.service.impl;

import lombok.AllArgsConstructor;
import org.example.webscraping.domain.Yayin;
import org.example.webscraping.repo.YayinRepo;
import org.example.webscraping.service.YayinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Objects;


@AllArgsConstructor
@Service
public class YayinServiceImpl implements YayinService {

    @Autowired
    private YayinRepo yayinRepo;


    @Override
    public void yayinCek() {
        List<Yayin> cekilenYayinlar = new ArrayList<>();

        String keywords = "machine learning";
        String searchUrl = null;

        int currentPage = 1;
        int targetCount = 10; // Hedeflenen veri sayısı

        try {
            while (cekilenYayinlar.size() < targetCount) {
                // Google Akademik arama URL'si oluşturma
                if (currentPage == 1) {
                    searchUrl = "https://scholar.google.com.tr/scholar?q=" + keywords;
                } else {
                    searchUrl = "https://scholar.google.com.tr/scholar?q=" + keywords + "&start=" + ((currentPage - 1) * 10);
                }

                Document document = Jsoup.connect(searchUrl).get();
                Elements results = document.select("div.gs_ri");

                for (Element result : results) {
                    String fullInfo = result.select("div.gs_a").text();
                    if (fullInfo.contains("books.google.com")) {
                        String title = result.select("h3.gs_rt a").text(); //yayın adı
                        String[] infoArray = fullInfo.split(" - ");

                        String authors = infoArray[0].trim();
                        String publicationDate = infoArray[1].trim();


                        String publicationType = result.select("div.gs_a").text(); // Yayın türü
                        // String publicationType = result.select("div.gs_a:contains(Araştırma makalesi)").text();
                        String publisherName = result.select("div.gs_a").text(); // Yayıncı adı
                        String keywordsSearchEngine = result.select("div.gs_a").text(); // Anahtar kelimeler (Arama motorunda aratılan)
                        String keywordsArticle = result.select("div.gs_a").text(); // Anahtar kelimeler (Makaleye ait)
                        String abstractText = result.select("div.gs_a").text(); // Özet
                        String references = result.select("div.gs_a").text(); // Referanslar
                        // int citationCount = Integer.parseInt(result.select("div.gs_a").text()); // Alıntı sayısı
                        String doiNumber = result.select("div.gs_a").text(); // Doi numarası
                        String urlmain = result.select("h3.gs_rt a").attr("href");


// urlmain'i kullanarak başka bir URL'ye eriş
                        Document doc = Jsoup.connect(urlmain).get();
                        String urlSub = doc.select("a#sidebar-atb-link").attr("href");

                        System.out.println(urlSub);



                        // URL adresi


                        Yayin yeniYayin = new Yayin();
                        yeniYayin.setYayinAdi(title);
                        yeniYayin.setYazarIsmi(authors);
                        yeniYayin.setYayinTuru(publicationType);
                       // yeniYayin.setYayimlanmaTarihi(Integer.parseInt(publicationDate));
                        yeniYayin.setYayinciAdi(publisherName);
                        yeniYayin.setOzet(abstractText);
                        yeniYayin.setAlintiSayisi(10);
                        yeniYayin.setDoiNumarasi(doiNumber);
                        yeniYayin.setUrlAdresi(urlmain);
                        cekilenYayinlar.add(yeniYayin);
                        yayinRepo.save(yeniYayin);

                        if (cekilenYayinlar.size() >= targetCount) {
                            break; // Hedef veri sayısına ulaşıldıysa döngüden çık
                        }
                    }
                }

                // Sayfa sayısını kontrol et
                Element nextPage = document.select("span.gs_ico_nav_next").first();
                if (nextPage == null) {
                    System.out.printf("sayfa bitti"+currentPage);
                    // "Sonraki" bağlantısı yoksa çık
                    break;
                }

                currentPage++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}