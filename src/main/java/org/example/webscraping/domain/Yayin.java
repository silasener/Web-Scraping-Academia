package org.example.webscraping.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document("yayin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Yayin {
    @Id
    private String yayinId;

    private String yayinAdi;

    private String yazarIsmi;

    private String yayinTuru; //araştırma makalesi, derleme, konferans, kitap vb.

    private Integer yayimlanmaTarihi;

    private String yayinciAdi; //yayının yayımlandığı konferans ismi; dergi veya kitap yayınevi

    private String ozet;

    private Integer alintiSayisi;

    private String doiNumarasi;

    private String urlAdresi;

    private String pdfLink;

    private List<MakaleTerimleri> makaleTerimleriList;

    public Yayin(String yayinId, String yayinAdi) {
        this.yayinId = yayinId;
        this.yayinAdi = yayinAdi;
    }
}


