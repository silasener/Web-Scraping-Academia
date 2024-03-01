package org.example.webscraping.repo;

import org.example.webscraping.domain.Yayin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface YayinRepo extends MongoRepository<Yayin, String> {
    @Query(value = "{}", sort = "{ 'yayimlanmaTarihi' : 1 }")
    List<Yayin> findAllOrderByYayimlanmaTarihiAsc();

    @Query("{ 'urlAdresi' : ?0 }")
    Yayin findByUrlAdresi(String urlAdresi);

    @Query(value = "{}", fields = "{ 'yazarIsmi' : 1 }")
    List<Yayin> findAllYazarIsmi();
}



