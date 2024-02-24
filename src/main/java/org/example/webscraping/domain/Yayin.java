package org.example.webscraping.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("yayin")
@Getter
@Setter
public class Yayin {
    @Id
    private Integer yayinId; // MongoDB ObjectId formatında bir alan kullanılmalıdır

    private String yayinAdi;


}


