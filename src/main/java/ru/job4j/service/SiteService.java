package ru.job4j.service;

import ru.job4j.domain.Site;

import java.util.Optional;

public interface SiteService {
    Optional<Site> save(Site site);
    Optional<Site> findByLogin(String login);
    boolean update(Site site);
}
