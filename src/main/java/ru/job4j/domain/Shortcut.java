package ru.job4j.domain;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "shortcuts")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Shortcut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    @NotNull(message = "Invalid url. URL can not be null")
    private String url;

    private String shortcut;

    private int counter;
}
