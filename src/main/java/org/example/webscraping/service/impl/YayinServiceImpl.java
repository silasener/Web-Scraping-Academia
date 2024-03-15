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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
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

    private final RestTemplate restTemplate;

    private static String enUygunKelime;

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

            Element pdfLinkElement = doc.selectFirst(".c-card__body a[data-test=front-matter-pdf]");
            String pdfLink = pdfLinkElement.attr("href");
            //System.out.println("PDF Link: " + pdfLink);
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


            String bookTipText =null;
            Element bookElement = doc.selectFirst(".c-article-identifiers__item:contains(Book)");
            if(Objects.nonNull(bookElement)){
                bookTipText = bookElement.text();
            }

            String conferenceText=null;
            Element conferenceElement = doc.selectFirst(".c-article-identifiers__item:contains(Conference proceedings)");
            if(Objects.nonNull(conferenceElement)){
                conferenceText=conferenceElement.text();
            }

            String textbook=null;
            Element textBookElement= doc.selectFirst(".c-article-identifiers__item:contains(Textbook)");
            if(Objects.nonNull(textBookElement)){
                textbook=textBookElement.text();
            }

            String referenceWorkText=null;
            Element referenceWorkElement= doc.selectFirst(".c-article-identifiers__item:contains(Reference work)");
            if(Objects.nonNull(referenceWorkElement)){
               referenceWorkText =referenceWorkElement.text();
            }


            // System.out.println("About this book içeriği: " + aboutBookContent);

            Yayin yayiniVeritabanindaAra=yayinRepo.findByDoiNumarasi(doi);

            if(Objects.isNull(yayiniVeritabanindaAra)) {
                Yayin yeniYayin = new Yayin();

                // Yayin Adi
                if (Objects.nonNull(bookTitle)) {
                    yeniYayin.setYayinAdi(bookTitle);
                }

                // Yazar Ismi
                if (Objects.nonNull(editors)) {
                    yeniYayin.setYazarIsmi(editors.toString());
                }

                // Yayin Turu
                if (Objects.nonNull(bookTipText)) {
                    yeniYayin.setYayinTuru(bookTipText);
                } else if (Objects.nonNull(conferenceText)) {
                    yeniYayin.setYayinTuru(conferenceText);
                } else if(Objects.nonNull(textbook)){
                    yeniYayin.setYayinTuru(textbook);
                }else if(Objects.nonNull(referenceWorkText)){
                    yeniYayin.setYayinTuru(referenceWorkText);
                }else {
                    yeniYayin.setYayinTuru("Article");
                }

                // Yayimlanma Tarihi
                if (Objects.nonNull(year)) {
                    yeniYayin.setYayimlanmaTarihi(Integer.parseInt(year));
                } else {
                    yeniYayin.setYayimlanmaTarihi(null);
                }

                // Yayinci Adi
                if (Objects.nonNull(publisherValue)) {
                    yeniYayin.setYayinciAdi(publisherValue.text());
                }

                // Ozet
                if (Objects.nonNull(aboutBookContent)) {
                    yeniYayin.setOzet(aboutBookContent);
                }

                // Alinti Sayisi
                if (Objects.nonNull(citationsCount)) {
                    yeniYayin.setAlintiSayisi(Integer.parseInt(citationsCount));
                } else {
                    yeniYayin.setAlintiSayisi(0);
                }

                // Doi Numarasi
                if (Objects.nonNull(doi)) {
                    yeniYayin.setDoiNumarasi(doi);
                }

                // URL Adresi
                if (Objects.nonNull(url)) {
                    yeniYayin.setUrlAdresi(url);
                }

                // Img URL
                Elements imgElements = doc.select("img");

                // Her img etiketi için URL'yi al ve sadece http olanları yazdır
                for (Element imgElement : imgElements) {
                    String imgUrl = imgElement.attr("src");
                    if (imgUrl.startsWith("http")) {
                        //System.out.println("Resim URL'si: " + imgUrl);
                        yeniYayin.setImgUrl(imgUrl);
                    }
                }

                // PDF Link
                if (Objects.nonNull(pdfLink)) {
                    yeniYayin.setPdfLink(pdfLink);
                }

                // Yayin nesnesini kaydet
                yayinRepo.save(yeniYayin);
                yayinSizeCheck++;

                // Diğer işlemler
                for (Element liElement : liElements) {
                    MakaleTerimleri makaleTerimleri = new MakaleTerimleri();
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
                .filter(yayin -> uniqueYazarYayin.add(yayin.getUrlAdresi()))
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
        Collections.sort(uniqueYazarlarList);

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
        Collections.sort(uniqueYayinAdlariList);

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
        Collections.sort(uniqueYayinAdlariList);

        return uniqueYayinAdlariList;
    }

    @Override
    public Yayin yayinaAitDetaylariGetir(String yayinId) {
        return yayinRepo.findByYayinId(yayinId);
    }

    public List<MakaleTerimleri> anahtarKelimeyiBarindiranMakaleler(String anahtarKelime) {
        List<MakaleTerimleri> makaleTerimleriList = makaleTerimleriRepo.findByAnahtarKelime(anahtarKelime);

        // DOI numaralarını kontrol etmek için bir set oluştur
        Set<String> uniqueDOINumbers = new HashSet<>();

        // Filtrelenmiş makaleleri unique DOI numaralarına göre kontrol et
        List<MakaleTerimleri> uniqueMakaleList = new ArrayList<>();
        for (MakaleTerimleri makale : makaleTerimleriList) {
            String doiNumber = makale.getYayin().getDoiNumarasi();

            // DOI numarası daha önce eklenmediyse, listeye ekle ve sete ekle
            if (uniqueDOINumbers.add(doiNumber)) {
                uniqueMakaleList.add(makale);
            }
        }

        // Eğer ihtiyacınıza bağlı olarak DOI numarası kontrolü sonrasında başka bir işlem yapmak istiyorsanız burada yapabilirsiniz

        return uniqueMakaleList;
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
        List<MakaleTerimleri> makaleTerimLeriReposu = makaleTerimleriRepo.findAll();
        List<String> anahtarKelimeList = makaleTerimLeriReposu.stream()
                .map(MakaleTerimleri::getAnahtarKelime)
                .collect(Collectors.toList());

        Set<String> uniqueAnahtarKelimeler = new HashSet<>(anahtarKelimeList);
        List<String> uniqueAnahtarKelimeList = new ArrayList<>(uniqueAnahtarKelimeler);
        Collections.sort(uniqueAnahtarKelimeList);

        return uniqueAnahtarKelimeList;
    }


    @Override
    public List<String> yayinTurList() {
        List<Yayin> yayinList=yayinRepo.findAll();
        List<String> yayinTuruList = yayinList.stream()
                .map(Yayin::getYayinTuru)
                .collect(Collectors.toList());

        Set<String> uniqueYayinTuruAdlari = new HashSet<>(yayinTuruList);
        List<String> uniqueYayinTuruAdlariList= new ArrayList<>(uniqueYayinTuruAdlari);
        Collections.sort(uniqueYayinTuruAdlariList);

        return uniqueYayinTuruAdlariList;
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadPdf(String pdfUrl) {
        byte[] pdfContent = restTemplate.getForObject(pdfUrl, byte[].class);

        if (Objects.requireNonNull(pdfContent).length > 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=downloaded.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfContent.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new ByteArrayResource(pdfContent));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<MakaleTerimleri> yanlisKelimeyeEnUygunMakaleler(String benzerAnahtarKelime) {
        // Boşluklara göre parçalayarak kelime sayısını kontrol et
        int kelimeSayisi = benzerAnahtarKelime.split("\\s+").length;
        if (kelimeSayisi > 1) {
            // Birden çok kelime varsa
            List<String> anahtarKelimeListesi = anahtarKelimeList();
            Optional<String> uygunKelimeObegi = anahtarKelimeListesi.stream().max(Comparator.comparingInt(anahtarKelime ->
                    calculateSimilarityScore(benzerAnahtarKelime, anahtarKelime)));

            if (uygunKelimeObegi.isPresent()) {
                enUygunKelime = uygunKelimeObegi.get();
                //System.out.println("Çoklu kelime için en uygun kelime: " + enUygunKelime);
            }

            // Çoklu kelime öbeği için uygun kelime döndür
            if (enUygunKelime != null) {
                List<MakaleTerimleri> uygunMakaleler = makaleTerimleriRepo.findByAnahtarKelimeContaining(enUygunKelime);
                return uygunMakaleler;
            } else {
                //System.out.println("Çoklu kelime için uygun kelime bulunamadı");
                return Collections.emptyList();
            }
        } else {
            // Tek kelime varsa
            List<MakaleTerimleri> makaleTerimLeriReposu = makaleTerimleriRepo.findAll();
            List<String> anahtarKelimeList = makaleTerimLeriReposu.stream()
                    .map(MakaleTerimleri::getAnahtarKelime)
                    .flatMap(anahtarKelime -> Arrays.stream(anahtarKelime.split("\\s+")))
                    .collect(Collectors.toList());

            Set<String> uniqueAnahtarKelimeler = new HashSet<>(anahtarKelimeList);
            List<String> uniqueAnahtarKelimeList = new ArrayList<>(uniqueAnahtarKelimeler);

            Optional<String> enUyanKelime = uniqueAnahtarKelimeList.stream()
                    .max(Comparator.comparingInt(anahtarKelime ->
                            calculateSimilarityScore(benzerAnahtarKelime, anahtarKelime)));

            if (enUyanKelime.isPresent()) {
                enUygunKelime = enUyanKelime.get();
                //System.out.println("Tekli kelime için en uygun kelime: " + enUygunKelime);

                // Anahtar kelimeye göre makaleleri filtreleme
                List<MakaleTerimleri> uygunMakaleler = makaleTerimleriRepo.findByAnahtarKelimeContaining(enUygunKelime);

                return uygunMakaleler;
            } else {
                //System.out.println("Tekli kelime için uygun kelime bulunamadı");
                return Collections.emptyList();
            }
        }
    }


    @Override
    public String enUygunAnahtarKelime() {
        return enUygunKelime;
    }

    public List<MakaleTerimleri> uniqueEserler(List<MakaleTerimleri> makaleTerimleriList) {
        // YayinId'ye göre bir Map oluştur
        Map<String, MakaleTerimleri> uniqueMap = makaleTerimleriList.stream()
                .collect(Collectors.toMap(
                        makaleTerimleri -> makaleTerimleri.getYayin().getYayinId(),
                        Function.identity(),
                        (existing, replacement) -> existing // Aynı yayinId'ye sahipse mevcut elemanı kullan
                ));

        // Map'teki değerleri kullanarak bir List oluştur
        List<MakaleTerimleri> uniqueList = uniqueMap.values()
                .stream()
                .collect(Collectors.toList());

        return uniqueList;
    }


    private int calculateSimilarityScore(String kelime1, String kelime2) {
        int[][] distance = new int[kelime1.length() + 1][kelime2.length() + 1];

        for (int i = 0; i <= kelime1.length(); i++) {
            distance[i][0] = i;
        }

        for (int j = 0; j <= kelime2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= kelime1.length(); i++) {
            for (int j = 1; j <= kelime2.length(); j++) {
                int cost = (kelime1.charAt(i - 1) == kelime2.charAt(j - 1)) ? 0 : 1;
                distance[i][j] = Math.min(
                        Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + cost
                );
            }
        }

        return -distance[kelime1.length()][kelime2.length()];
    }




}