package org.example.webscraping.repo;

import org.example.webscraping.domain.Yayin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface YayinRepo extends MongoRepository<Yayin, String> {
    @Query(value = "{}", sort = "{ 'yayimlanmaTarihi' : 1 }")
    List<Yayin> findAllOrderByYayimlanmaTarihiAsc();

    @Query("{ 'doiNumarasi' : ?0 }")
    Yayin findByDoiNumarasi(String doiNumarasi);

    @Query("{ 'yayinId' : ?0 }")
    Yayin findByYayinId(String yayinId);


    @Query("{ 'yayinAdi' : ?0, 'yazarAdi' : ?1 }")
    Yayin findByYayinAdiAndYazarAdi(String yayinAdi, String yazarAdi);

    @Query(value = "{}", fields = "{ 'yazarIsmi' : 1 }")
    List<Yayin> findAllYazarIsmi();
}



