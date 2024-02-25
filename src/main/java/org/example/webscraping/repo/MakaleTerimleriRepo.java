package org.example.webscraping.repo;

import org.example.webscraping.domain.MakaleTerimleri;
import org.example.webscraping.domain.Yayin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MakaleTerimleriRepo extends MongoRepository<MakaleTerimleri, String> {
}
