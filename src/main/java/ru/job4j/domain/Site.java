package ru.job4j.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sites")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @Size(min = 4, message = "Invalid site. Site must be at least 4 characters long")
    @NotNull(message = "Invalid site. Site must not be null")
    private String site;

    private String login;

    private String password;

    private boolean registration;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id")
    private List<Shortcut> shortcuts = new ArrayList<>();

    public void addShortCut(Shortcut shortcut) {
        shortcuts.add(shortcut);
    }
}
