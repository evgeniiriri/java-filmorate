package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {


    private final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class.getName());
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        validationDateRelease(film);
        film.setId(getNextId());
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        validationDateRelease(newFilm);
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            return oldFilm;
        }
        throw new NotFoundException("Фильм с id - " + newFilm.getId() + " не найден");
    }

    @Override
    public Film delete(Film film) {
        validationDateRelease(film);
        if (!films.containsKey(film.getId())) {
            log.warn("У фильма с ID - [{}], name - [{}], releaseDate - [{}] не найден.",
                    film.getId(), film.getName(), film.getReleaseDate());
            throw new ValidationException("Фильм с id - " + film.getId() + " не найден");
        }
        films.remove(film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> getById(Long id) {
        return films.values().stream()
                .filter(film -> Objects.equals(film.getId(), id))
                .findFirst();
    }

    @Override
    public Film setLikeByUserId(Long idUser, Long idFilm) {
        validationIdFilm(idFilm);
        films.get(idFilm).setLike(idUser);
        return films.get(idFilm);

    }

    @Override
    public Film deleteLikeByUserId(Long idUser, Long idFilm) {
        validationIdFilm(idFilm);
        films.get(idFilm).deleteLike(idUser);
        return films.get(idFilm);
    }

    private void validationDateRelease(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("У фильма с ID - [{}], name - [{}], releaseDate - [{}] некорректная дата релиза. Меньше 1895.12.28",
                    film.getId(), film.getName(), film.getReleaseDate());
            throw new ValidationException("Не верная дата релиза.");
        }
    }

    private void validationIdFilm(Long idFilm) {
        if (!films.containsKey(idFilm)) {
            throw new NotFoundException("Фильма с ID - " + idFilm + " не нашлось.");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
