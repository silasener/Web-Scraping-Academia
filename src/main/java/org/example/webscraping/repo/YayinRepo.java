package org.example.webscraping.repo;

import org.example.webscraping.domain.Yayin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface YayinRepo extends MongoRepository<Yayin, String> {
}



