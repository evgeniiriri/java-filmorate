package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmService.class.getName());
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
       return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film setLikeByUser(Long idUser, Long idFilm) {
        return filmStorage.setLikeByUserId(idUser, idFilm);
    }

    public Film deleteLikeByUser(Long idUser, Long idFilm) {
        return filmStorage.deleteLikeByUserId(idUser, idFilm);
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Значение  count должно быть выше 0.");
        }
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getCountLikes).reversed())
                .limit(count)
                .collect(Collectors.toCollection(LinkedList::new));
    }

}
