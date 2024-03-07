package org.example.webscraping.repo;

import org.example.webscraping.domain.MakaleTerimleri;
import org.example.webscraping.domain.Yayin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MakaleTerimleriRepo extends MongoRepository<MakaleTerimleri, String> {

    @Query("{ 'anahtarKelime' : { $regex: ?0, $options: 'i' } }")
    List<MakaleTerimleri> findByAnahtarKelime(String anahtarKelime);

    @Query("{ 'yayin' : ?0 }")
    List<MakaleTerimleri> findByYayin(Yayin yayin);

    @Query("{ 'anahtarKelime' : { $regex: ?0, $options: 'i' } }")
    List<MakaleTerimleri> findByAnahtarKelimeContaining(String anahtarKelime);





}
