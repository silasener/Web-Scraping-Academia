package org.example.webscraping.api.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.example.webscraping.domain.Yayin;
import org.example.webscraping.repo.YayinRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

@RestController
public class WebScrapingController {

    private  YayinRepo yayinRepo;

    @Autowired
    public WebScrapingController(YayinRepo yayinRepo) {
        this.yayinRepo = yayinRepo;
    }


    @GetMapping("/yayin")
    public ResponseEntity<List<Yayin>> getAllYayin(){
        String keywords = "machine learning";

        // Google Akademik arama URL'si oluşturma
        String searchUrl = "https://scholar.google.com.tr/scholar?q=" + keywords;

        try {
            Document document = Jsoup.connect(searchUrl).get();

            Elements results = document.select("div.gs_ri");

            int count = 1;
            for (Element result : results) {
                String authors = result.select("div.gs_a").text();
                String title = result.select("h3.gs_rt a").text();

                System.out.println("Yayın " + count + ":");
                System.out.println("Yazarlar: " + authors);
                System.out.println("Başlık: " + title);

                try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
                    // Veritabanını seç
                    MongoDatabase database = mongoClient.getDatabase("WebScrapingAkademi");

                    // Veritabanında koleksiyon oluştur veya var olanı al
                    MongoCollection<org.bson.Document> collection = database.getCollection("yayinlar");

                    // Örnek bir yayın belgesi oluştur
                    org.bson.Document publication = new org.bson.Document()
                            .append("yayinAdi", "rnek Yayın")
                            .append("yazarlar", authors)
                            .append("yayinTuru", "Araştırma Makalesi")
                            .append("yayinTarihi", "01-01-2023")
                            .append("yayinciAdi", "Bir Dergi")
                            .append("anahtarKelimelerAra", "Web Scraping, MongoDB, Java")
                            .append("anahtarKelimelerMakale", "Veritabanı, Web Scraping, Akademik")
                            .append("ozet", "Bu bir örnek yayındır.")
                            .append("referanslar", "Referans1, Referans2")
                            .append("alintiSayisi", 10)
                            .append("doiNumarasi", "doi:12345")
                            .append("urlAdresi", "https://ornek-yayin.com");

                    // MongoDB'ye belge ekle
                    collection.insertOne(publication);

                    System.out.println("Yayın MongoDB'ye eklendi.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Element pdfLinkElement = result.select("div.gs_ggsd a:contains(PDF)").first();
                if (pdfLinkElement != null) {
                    String pdfLink = pdfLinkElement.attr("href");
                    System.out.println("PDF Link: " + pdfLink);
                }

                System.out.println("---------------");
                count++;

                if (count > 10) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(this.yayinRepo.findAll());
    }

    @PostMapping("/yayinEkle/{yayinId}/{yayinAdi}")
    public ResponseEntity<String> yayinEkle(@PathVariable int yayinId,@PathVariable String yayinAdi) {
        try {
            Yayin yayin=new Yayin(yayinId,yayinAdi);
            yayinRepo.save(yayin);
            return ResponseEntity.ok("Yayın eklendi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Yayın eklenirken bir hata oluştu.");
        }
    }



}


