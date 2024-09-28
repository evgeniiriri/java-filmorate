package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Long> like = new HashSet<>();

    public void setLike(Long id) {
        like.add(id);
    }

    public void deleteLike(Long id) {
        like.remove(id);
    }

    public int getCountLikes() {
        return like.size();
    }
}
