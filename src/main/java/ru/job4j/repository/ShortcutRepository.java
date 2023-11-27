package ru.job4j.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.domain.Shortcut;

import java.util.Optional;

@Transactional
public interface ShortcutRepository extends CrudRepository<Shortcut, Integer> {

    Optional<Shortcut> findByShortcut(String shortcut);

    @Modifying
    @Query("update Shortcut u set u.counter = u.counter + 1 where u.id = :id")
    int updateCounter(@Param("id") Integer id);
}
