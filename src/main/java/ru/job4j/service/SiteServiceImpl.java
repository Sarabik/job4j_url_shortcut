package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Site;
import ru.job4j.repository.SiteRepository;
import ru.job4j.util.RandomGenerator;

import java.util.Collections;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SiteServiceImpl implements SiteService, UserDetailsService {

    private final SiteRepository siteRepository;

    @Override
    public Optional<Site> save(Site site) {
        site.setLogin(RandomGenerator.generateSequence(8));
        site.setRegistration(true);
        return Optional.of(siteRepository.save(site));
    }

    @Override
    public boolean update(Site site) {
        if (siteRepository.existsById(site.getId())) {
            siteRepository.save(site);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Site> findByLogin(String login) {
        return siteRepository.findByLogin(login);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Site> optional = siteRepository.findByLogin(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        Site site = optional.get();
        return new User(site.getLogin(), site.getPassword(), Collections.emptyList());
    }
}
