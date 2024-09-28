package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);
    Film update(Film film);
    Film delete(Film film);
    Collection<Film> getAll();
    Optional<Film> getById(Long id);
    Film setLikeByUserId(Long idUser, Long idFilm);
    Film deleteLikeByUserId(Long idUser, Long idFilm);
}
