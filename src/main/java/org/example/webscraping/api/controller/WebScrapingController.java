package org.example.webscraping.api.controller;

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


