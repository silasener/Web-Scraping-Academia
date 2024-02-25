package org.example.webscraping.service.impl;

import lombok.AllArgsConstructor;
import org.example.webscraping.domain.Yayin;
import org.example.webscraping.repo.YayinRepo;
import org.example.webscraping.service.YayinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Map;


@AllArgsConstructor
@Service
public class YayinServiceImpl implements YayinService {

    @Autowired
    private YayinRepo yayinRepo;


    @Override
    public List<Yayin> yayinCek() {
        List<Yayin> cekilenYayinlar=new ArrayList<>();

        String keywords = "machine learning";

        // Google Akademik arama URL'si oluşturma
        String searchUrl = "https://scholar.google.com.tr/scholar?q=" + keywords;

        try {
            Document document = Jsoup.connect(searchUrl).get();

            Elements results = document.select("div.gs_ri");

            int count = 1;
            for (Element result : results) {
                String fullInfo = result.select("div.gs_a").text();
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
                String url = result.select("h3.gs_rt a").attr("href"); // URL adresi


                Yayin yeniYayin=new Yayin();
                yeniYayin.setYayinAdi(title);
                yeniYayin.setYazarIsmi(authors);
                yeniYayin.setYayinTuru(publicationType);
                yeniYayin.setYayimlanmaTarihi(Integer.parseInt(publicationDate));
                yeniYayin.setYayinciAdi(publisherName);
                yeniYayin.setOzet(abstractText);
                yeniYayin.setAlintiSayisi(10);
                yeniYayin.setDoiNumarasi(doiNumber);
                yeniYayin.setUrlAdresi(url);
                cekilenYayinlar.add(yeniYayin);
                yayinRepo.save(yeniYayin);
               // yayinlariVeritabaninaKaydet(yeniYayin);
                System.out.println(yeniYayin.getYayinId()+" ve "+yeniYayin.getYayinAdi());

                count++;

                if (count > 2) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //yayinlariVeritabaninaKaydet(cekilenYayinlar);

        return cekilenYayinlar;
    }




}
