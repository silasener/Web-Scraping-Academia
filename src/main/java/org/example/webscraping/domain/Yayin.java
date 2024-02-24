package org.example.webscraping.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("yayin")
@Getter
@Setter
public class Yayin {
    @Id
    private String id; // MongoDB ObjectId formatında bir alan kullanılmalıdır

    private String yayinAdi;


}


