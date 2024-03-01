package org.example.webscraping.service.impl;

import lombok.AllArgsConstructor;
import org.example.webscraping.domain.MakaleTerimleri;
import org.example.webscraping.domain.Yayin;
import org.example.webscraping.repo.MakaleTerimleriRepo;
import org.example.webscraping.repo.YayinRepo;
import org.example.webscraping.service.YayinService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class YayinServiceImpl implements YayinService {

    @Autowired
    private YayinRepo yayinRepo;

    @Autowired
    private MakaleTerimleriRepo makaleTerimleriRepo;


    @Override
    public void yayinCek(String anahtarKelime) {
        List<Yayin> cekilenYayinlar = new ArrayList<>();
        String searchUrl = null;

        int currentPage = 1;
        int targetCount = 10; // Hedeflenen veri sayısı

        try {
            while (cekilenYayinlar.size() < targetCount) {
                // Google Akademik arama URL'si oluşturma
                if (currentPage == 1) {
                    searchUrl = "https://scholar.google.com.tr/scholar?q=" + anahtarKelime;
                } else {
                    searchUrl = "https://scholar.google.com.tr/scholar?q=" + anahtarKelime + "&start=" + ((currentPage - 1) * 10);
                }

                Document document = Jsoup.connect(searchUrl).get();
                Elements results = document.select("div.gs_ri");

                for (Element result : results) {
                    String fullInfo = result.select("div.gs_a").text();
                    if (fullInfo.contains("books.google.com")) {
                        String urlmain = result.select("h3.gs_rt a").attr("href");


                        // urlmain'i kullanarak başka bir URL'ye eriş
                        Document doc = Jsoup.connect(urlmain).get();
                        String urlSub = doc.select("a#sidebar-atb-link").attr("href");

                        System.out.println("URL Sub: " + urlSub);

                        // urlSub'deki sayfadaki tüm öğeleri çekip yazdır
                        Document subDoc = Jsoup.connect(urlSub).get();

                        Elements allElements = subDoc.getAllElements();

                        // Tüm öğeleri yazdır
                      /* for (Element element : allElements) {
                            System.out.println(element);
                        }

                       */

                        String baslik = subDoc.select("td.metadata_label:contains(Başlık) + td.metadata_value span").text();
                        String yazar = subDoc.select("td.metadata_label:contains(Yazar) + td.metadata_value span").text();

                        String editorler= subDoc.select("td.metadata_label:contains(Editörler) + td.metadata_value span").text();
                        String editor= subDoc.select("td.metadata_label:contains(Editör) + td.metadata_value span").text();
                        System.out.println("yazar yazdırma: "+yazar);

                        if(yazar.equals("") && Objects.nonNull(editorler)){
                           // System.out.println("editorleri yazdırdı"+editorler);
                            yazar=editorler;
                        }else if(yazar.equals("") && Objects.nonNull(editor)){
                            //System.out.println("editoru yazdırdı"+editor);
                            yazar=editor;
                        }

                        String yayinciTarih = subDoc.select("td.metadata_label:contains(Yayıncı) + td.metadata_value span").text();
                        String[] yayinciTarihArray = yayinciTarih.split(",\\s+");

                        Yayin yayinBulundu=yayinRepo.findByYayinAdiAndYazarAdi(baslik,yazar);

                        if(Objects.nonNull(yayinBulundu)){

                        }else{
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

                            Elements cloudElements = subDoc.select("a[class^=cloud] span[dir=ltr]");

                            for (Element cloudElement : cloudElements) {
                                MakaleTerimleri yeniMakaleTerimleri = new MakaleTerimleri();
                                yeniMakaleTerimleri.setYayin(yeniYayin);
                                yeniMakaleTerimleri.setAnahtarKelime(cloudElement.text());
                                makaleTerimleriRepo.save(yeniMakaleTerimleri);
                            }
                        }

                        if (cekilenYayinlar.size() >= targetCount) {
                            break; // Hedef veri sayısına ulaşıldıysa döngüden çık
                        }
                    }

                }

                // Sayfa sayısını kontrol et
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

    @Override
    public List<Yayin> yayinlarigoruntule() {
        List<Yayin> yayinList = yayinRepo.findAll();

        Set<String> uniqueYazarYayin = new HashSet<>();
        List<Yayin> uniqueAndSortedYayinlar = yayinList.stream()
                .filter(yayin -> uniqueYazarYayin.add(yayin.getYazarIsmi() + yayin.getYayinAdi()))
                .sorted(Comparator.comparing(Yayin::getYayinAdi))
                .collect(Collectors.toList());

        return uniqueAndSortedYayinlar;
    }

    @Override
    public List<String> yazarlariGoruntule() {
        List<Yayin> yayinList = yayinRepo.findAllYazarIsmi();
        List<String> yazarIsmiList = yayinList.stream()
                .map(Yayin::getYazarIsmi)
                .collect(Collectors.toList());

        Set<String> uniqueYazarlar = new HashSet<>(yazarIsmiList);
        List<String> uniqueYazarlarList = new ArrayList<>(uniqueYazarlar);
        return uniqueYazarlarList;
    }

    @Override
    public List<String> eserAdlariniGoruntule() {
        List<Yayin> yayinList=yayinRepo.findAll();
        List<String> yayinAdiList = yayinList.stream()
                .map(Yayin::getYayinAdi)
                .collect(Collectors.toList());

        Set<String> uniqueYayinAdlari = new HashSet<>(yayinAdiList);
        List<String> uniqueYayinAdlariList= new ArrayList<>(uniqueYayinAdlari);
        return uniqueYayinAdlariList;
    }


    @Override
    public List<String> yayinciAdlariniGoruntule() {
        List<Yayin> yayinList=yayinRepo.findAll();
        List<String> yayinciAdiList = yayinList.stream()
                .map(Yayin::getYayinciAdi)
                .collect(Collectors.toList());

        Set<String> uniqueYayinciAdlari = new HashSet<>(yayinciAdiList);
        List<String> uniqueYayinAdlariList= new ArrayList<>(uniqueYayinciAdlari);
        return uniqueYayinAdlariList;
    }


}