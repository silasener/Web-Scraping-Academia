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
        int targetCount = 1; // Hedeflenen veri sayısı

        try {
            while (cekilenYayinlar.size() < targetCount) {
                System.out.println("deneme1 ");
                // Google Akademik arama URL'si oluşturma
                if (currentPage == 1) {
                    searchUrl = "https://scholar.google.com.tr/scholar?q=" + keywords;
                } else {
                    searchUrl = "https://scholar.google.com.tr/scholar?q=" + keywords + "&start=" + ((currentPage - 1) * 10);
                }

                System.out.println("deneme2 ");
                Document document = Jsoup.connect(searchUrl).get();
                Elements results = document.select("div.gs_ri");

                for (Element result : results) {
                    System.out.println("deneme3 ");
                    String fullInfo = result.select("div.gs_a").text();
                    if (fullInfo.contains("books.google.com")) {
                        String urlmain = result.select("h3.gs_rt a").attr("href");


// urlmain'i kullanarak başka bir URL'ye eriş
                        Document doc = Jsoup.connect(urlmain).get();
                        String urlSub = doc.select("a#sidebar-atb-link").attr("href");

                        System.out.println("URL Sub: " + urlSub);

                        // urlSub'deki sayfadaki tüm öğeleri çekip yazdır
                        Document subDoc = Jsoup.connect(urlSub).get();

                        System.out.println("deneme4 ");
                        String baslik = subDoc.select("td.metadata_label:contains(Başlık) + td.metadata_value span").text();
                        String yazar = subDoc.select("td.metadata_label:contains(Yazar) + td.metadata_value span").text();
                        String yayinciTarih = subDoc.select("td.metadata_label:contains(Yayıncı) + td.metadata_value span").text();
                        String[] yayinciTarihArray = yayinciTarih.split(",\\s+");


                        Yayin yeniYayin = new Yayin();
                        yeniYayin.setYayinAdi(baslik);
                        yeniYayin.setYazarIsmi(yazar);
                        yeniYayin.setYayinTuru("tür");

                            String yayinci = yayinciTarihArray[0];
                            String yayinlanmaTarihi = yayinciTarihArray[1];
                            yeniYayin.setYayimlanmaTarihi(Integer.parseInt(yayinlanmaTarihi));
                            yeniYayin.setYayinciAdi(yayinci);
                        yeniYayin.setOzet("özet");
                        yeniYayin.setAlintiSayisi(10);
                        yeniYayin.setDoiNumarasi("doi");
                        yeniYayin.setUrlAdresi(urlmain);
                        cekilenYayinlar.add(yeniYayin);
                        yayinRepo.save(yeniYayin);
                        System.out.println("deneme5 ");

                        if (cekilenYayinlar.size() >= targetCount) {
                            System.out.println("deneme6 ");
                            break; // Hedef veri sayısına ulaşıldıysa döngüden çık
                        }
                    }
                    System.out.println("deneme7 ");
                }

                // Sayfa sayısını kontrol et
                System.out.println("deneme8 ");
                Element nextPage = document.select("span.gs_ico_nav_next").first();
                if (nextPage == null) {
                    System.out.printf("sayfa bitti"+currentPage);
                    // "Sonraki" bağlantısı yoksa çık
                    break;
                }

                System.out.println("deneme9 ");
                currentPage++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void yayinCek2() {
        List<Yayin> cekilenYayinlar = new ArrayList<>();
        String keywords = "machine learning";
        int targetCount = 5; // Hedeflenen veri sayısı
        int currentPage = 1;

        try {
            while (cekilenYayinlar.size() < targetCount) {
                String searchUrl = currentPage == 1 ? "https://scholar.google.com.tr/scholar?q=" + keywords :
                        "https://scholar.google.com.tr/scholar?q=" + keywords + "&start=" + ((currentPage - 1) * 10);

                Document document = Jsoup.connect(searchUrl).get();
                Elements results = document.select("div.gs_ri");

                for (Element result : results) {
                    String fullInfo = result.select("div.gs_a").text();
                    if (fullInfo.contains("books.google.com")) {
                        String urlmain = result.select("h3.gs_rt a").attr("href");
                        Document doc = Jsoup.connect(urlmain).get();
                        String urlSub = doc.select("a#sidebar-atb-link").attr("href");
                        Document subDoc = Jsoup.connect(urlSub).get();

                        String baslik = subDoc.select("td.metadata_label:contains(Başlık) + td.metadata_value span").text();
                        String yazar = subDoc.select("td.metadata_label:contains(Yazar) + td.metadata_value span").text();
                        String yayinciTarih = subDoc.select("td.metadata_label:contains(Yayıncı) + td.metadata_value span").text();
                        String[] yayinciTarihArray = yayinciTarih.split(",\\s+");

                        Yayin yeniYayin = new Yayin();
                        yeniYayin.setYayinAdi(baslik);
                        yeniYayin.setYazarIsmi(yazar);
                        yeniYayin.setYayinTuru("tür");

                        String yayinci = yayinciTarihArray[0];
                        String yayinlanmaTarihi = yayinciTarihArray[1];
                        yeniYayin.setYayimlanmaTarihi(Integer.parseInt(yayinlanmaTarihi));
                        yeniYayin.setYayinciAdi(yayinci);
                        yeniYayin.setOzet("özet");
                        yeniYayin.setAlintiSayisi(10);
                        yeniYayin.setDoiNumarasi("doi");
                        yeniYayin.setUrlAdresi(urlmain);
                        cekilenYayinlar.add(yeniYayin);
                        yayinRepo.save(yeniYayin);

                        if (cekilenYayinlar.size() >= targetCount) {
                            break; // Hedef veri sayısına ulaşıldıysa döngüden çık
                        }
                    }
                }

                Element nextPage = document.select("span.gs_ico_nav_next").first();
                if (nextPage == null) {
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