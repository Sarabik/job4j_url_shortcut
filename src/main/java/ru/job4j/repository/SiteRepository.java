package ru.job4j.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.domain.Site;

import java.util.Optional;

public interface SiteRepository extends CrudRepository<Site, Integer> {

    Optional<Site> findByLogin(String username);
}
