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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class YayinServiceImpl implements YayinService {

    @Autowired
    private YayinRepo yayinRepo;

    @Autowired
    private MakaleTerimleriRepo makaleTerimleriRepo;

    private static Integer yayinSizeCheck;

    @Override
    public void yayinCek(String anahtarKelime) {
        yayinSizeCheck=0;
        int pageCount = 1;
        String mainurl = "https://link.springer.com/search?facet-content-type=Book&query=" + anahtarKelime.replace(" ", "+");

        try {
            for (int page = 1; page <= pageCount; page++) {
                String pageUrl = mainurl;
                if (page > 1) {
                    pageUrl = "https://link.springer.com/search/page/"+pageCount+"?facet-content-type=Book&query="+ anahtarKelime.replace(" ", "+");
                }

                Document doc = Jsoup.connect(pageUrl).get(); // siteye bağlan
                Elements links = doc.select("a.title"); // linkleri çek

                for (Element link : links) {
                    if(yayinSizeCheck==10){
                        break;
                    }
                    String url = link.attr("href"); // linki al
                    // Linkten bilgileri çek
                    getInfoFromUrl("https://link.springer.com" + url,anahtarKelime);
                }

                    pageCount++;

                if(yayinSizeCheck==10){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Verilen URL'den bilgileri çekme metodu
    public void getInfoFromUrl(String url,String anahtarKelime) {
        try {
            // Siteye bağlan
            Document doc = Jsoup.connect(url).get();

            // Book Title'ı seç
            Element bookTitleElement = doc.selectFirst(".c-bibliographic-information__value");
            String bookTitle = bookTitleElement.text();
           // System.out.println("Book Title: " + bookTitle);

            // Editors'ı seç
            Elements editorElements = doc.select(".c-bibliographic-information__value[data-component=book-contributor-list]");
            StringBuilder editors = new StringBuilder();
            for (Element editorElement : editorElements) {
                if (editors.length() > 0) {
                    editors.append(", ");
                }
                editors.append(editorElement.text());
            }
           // System.out.println("Editors: " + editors.toString());

            // DOI'ı seç
            Element doiElement = doc.selectFirst(".c-bibliographic-information__value:contains(DOI)");
            String doi = doiElement.text().replace("DOI: ", "");
           // System.out.println("DOI: " + doi);


            Element publisherElement = doc.select("span.u-text-bold:contains(Publisher)").first();

            Element publisherValue=null;
            if (publisherElement != null) {
                publisherValue = publisherElement.parent().select(".c-bibliographic-information__value").first();
                if (publisherValue != null) {
                   // System.out.println("Publisher: " + publisherValue.text());
                }
            }


            String citationCount = doc.select(".c-article-metrics-bar__count").text().trim();
            //System.out.println("bütün alıntı Sayısı: " + citationCount);
            String pattern = "(\\d+)\\s*Citations";

            // Deseni kullanarak eşleştirmeyi gerçekleştir
            Pattern r = Pattern.compile(pattern);
            Matcher matcher = r.matcher(citationCount);

            // Eğer eşleşme bulunduysa, sayıyı al
            String citationsCount=null;
            if (matcher.find()) {
                 citationsCount = matcher.group(1);
                //System.out.println("Citations Kısmı: " + citationsCount);
            }


            // Ul içindeki tüm li etiketlerini çek
            Elements liElements = doc.select("ul.c-article-subject-list li");
            for (Element liElement : liElements) {
                String liKeyword = liElement.selectFirst("a").text();
                //System.out.println("List Keyword: " + liKeyword);
            }

            Element yearElement = doc.selectFirst("li:containsOwn(©)");
            String yearText=null;
            String year=null;
            if(Objects.nonNull(yearElement)){
                yearText = yearElement.text();
                year = yearText.replaceAll("[^0-9]", "");
            }else{
                year=null;
            }


            // About this book içeriğini çek
            Element aboutBookElement = doc.selectFirst(".c-box__heading:contains(About this book)").parent();
            String aboutBookContent = aboutBookElement.select(".c-book-section").text();
           // System.out.println("About this book içeriği: " + aboutBookContent);

            Yayin yayiniVeritabanindaAra=yayinRepo.findByDoiNumarasi(doi);

            if(Objects.isNull(yayiniVeritabanindaAra)){
                Yayin yeniYayin = new Yayin();
                yeniYayin.setYayinAdi(bookTitle);
                yeniYayin.setYazarIsmi(editors.toString());
                yeniYayin.setYayinTuru("kitap");
                if(year!=null){
                    yeniYayin.setYayimlanmaTarihi(Integer.parseInt(year));
                }else{
                    yeniYayin.setYayimlanmaTarihi(null);
                }
                yeniYayin.setYayinciAdi(publisherValue.text());
                yeniYayin.setOzet(aboutBookContent);
                if(citationsCount!=null){
                    yeniYayin.setAlintiSayisi(Integer.parseInt(citationsCount));
                }else{
                    yeniYayin.setAlintiSayisi(0);
                }
                yeniYayin.setDoiNumarasi(doi);
                yeniYayin.setUrlAdresi(url);
                yayinRepo.save(yeniYayin);
                yayinSizeCheck++;
                System.out.println("sonraki kitap \n"+yayinSizeCheck);
                for (Element liElement : liElements) {
                    MakaleTerimleri makaleTerimleri=new MakaleTerimleri();
                    makaleTerimleri.setYayin(yeniYayin);
                    String liKeyword = liElement.selectFirst("a").text();
                    makaleTerimleri.setAnahtarKelime(liKeyword);
                    makaleTerimleriRepo.save(makaleTerimleri);
                }
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
                .flatMap(yazarlar -> Arrays.stream(yazarlar.split(", ")))
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

    @Override
    public List<MakaleTerimleri> anahtarKelimeyiBarindiranMakaleler(String anahtarKelime) {
        List<MakaleTerimleri> makaleTerimleriList = makaleTerimleriRepo.findByAnahtarKelime(anahtarKelime);
        //System.out.println(anahtarKelime);
        for (MakaleTerimleri makale : makaleTerimleriList) {
            //System.out.println(makale.getYayin().getYayinAdi());
        }
        return makaleTerimleriList;
    }

    @Override
    public List<String> makaleninAnahtarKelimeleri(String yayinId) {

        List<String> anahtarKelimeler=new ArrayList<>();
        Yayin arananYayin=yayinRepo.findByYayinId(yayinId);
        List<MakaleTerimleri> makaleninTerimReposu=makaleTerimleriRepo.findByYayin(arananYayin);
        for (MakaleTerimleri terimler: makaleninTerimReposu){
            anahtarKelimeler.add(terimler.getAnahtarKelime());
        }
        return anahtarKelimeler;
    }

    @Override
    public List<String> anahtarKelimeList() {
        List<MakaleTerimleri> makaleTerimLeriReposu =makaleTerimleriRepo.findAll();
        List<String> anahtarKelimeList= makaleTerimLeriReposu.stream()
                .map(MakaleTerimleri::getAnahtarKelime)
                .collect(Collectors.toList());

        Set<String> uniqueAnahtarKelimeler = new HashSet<>(anahtarKelimeList);
        List<String> uniqueAnahtarKelimeList= new ArrayList<>(uniqueAnahtarKelimeler);

        return uniqueAnahtarKelimeList;
    }


}