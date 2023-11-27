package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Shortcut;
import ru.job4j.domain.Site;
import ru.job4j.service.ShortcutService;
import ru.job4j.service.SiteService;
import ru.job4j.util.RandomGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.*;

@RestController
@AllArgsConstructor
public class ShortcutController {

    private final ShortcutService shortcutService;
    private final SiteService siteService;
    private PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper;

    @PostMapping("/convert")
    public ResponseEntity<?> addNewShortcut(@Valid @RequestBody Shortcut shortcut,
                                    Principal principal) {
        String login = principal.getName();
        Optional<Site> optSite = siteService.findByLogin(login);
        if (optSite.isEmpty()) {
            throw new UsernameNotFoundException("Login " + login + " not found");
        }
        Site site = optSite.get();
        String code = RandomGenerator.generateSequence(7);
        shortcut.setShortcut(code);
        site.addShortCut(shortcut);
        boolean isUpdated = siteService.update(site);
        if (isUpdated) {
            return new ResponseEntity<>(Map.of("shortcut", code), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not updated");
    }

    @GetMapping("/redirect/{shortcut}")
    public ResponseEntity<?> redirection(@PathVariable String shortcut) {
        if (shortcut == null || shortcut.length() != 7) {
            throw new IllegalArgumentException(
                    "Invalid shortcut. Shortcut must be 7 characters long"
            );
        }
        String url = shortcutService.getUrl(shortcut);
        if (url == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shortcut is not found");
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }

    @PostMapping("/registration")
    public ResponseEntity<?> create(@Valid @RequestBody Site site) {
        String password = RandomGenerator.generateSequence(10);
        site.setPassword(passwordEncoder.encode(password));
        Optional<Site> optional = siteService.save(site);
        Site current = optional.orElse(new Site());
        if (current.isRegistration()) {
            current.setPassword(password);
        }
        Object body = new HashMap<>() {{
            put("registration", current.isRegistration());
            put("login", current.getLogin());
            put("password", password);
        }};
        return new ResponseEntity<>(body,
                optional.isPresent() ? HttpStatus.CREATED : HttpStatus.CONFLICT
        );
    }

    @GetMapping("/statistic")
    public ResponseEntity<?> getStatistic(Principal principal) {
        String login = principal.getName();
        Optional<Site> optSite = siteService.findByLogin(login);
        if (optSite.isEmpty()) {
            throw new UsernameNotFoundException("Login " + login + " not found");
        }
        Site site = optSite.get();
        List<Map<String, Object>> body = new ArrayList<>();
        for (Shortcut shortcut : site.getShortcuts()) {
            Map<String, Object> url = new HashMap<>();
            url.put("url", shortcut.getUrl());
            url.put("total", shortcut.getCounter());
            body.add(url);
        }
        Comparator<Map<String, Object>> comparator = Comparator.comparing(h -> (int) h.get("total"));
        body.sort(comparator.reversed());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(
                new HashMap<>() {{
                    put("message", e.getMessage());
                    put("type", e.getClass());
                }}
        ));
    }

}
