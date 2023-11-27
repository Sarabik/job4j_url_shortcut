package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Shortcut;
import ru.job4j.repository.ShortcutRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ShortcutServiceImpl implements ShortcutService {

    private final ShortcutRepository shortcutRepository;

    @Override
    public String getUrl(String shortcut) {
        String url = null;
        Optional<Shortcut> optional = shortcutRepository.findByShortcut(shortcut);
        if (optional.isPresent()) {
            Shortcut current = optional.get();
            url = current.getUrl();
            int change = shortcutRepository.updateCounter(current.getId());
            if (change > 0) {
                return url;
            }
        }
        return url;
    }
}
