package org.example.webscraping.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("makaleTerimleri")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MakaleTerimleri {
    @Id
    private String makaleninterimId;

    private Yayin yayin;

}
