package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmService.class.getName());

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        validationDateRelease(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateIdFilm(film.getId(), "Невозможно обновить фильм с ID - [{}], так как его нет.");
        validationDateRelease(film);
        return filmStorage.update(film);
    }

    public Film delete(Film film) {
        validateIdFilm(film.getId(), "Невозможно удалить фильм с ID - [{}], так как его нет.");
        return filmStorage.delete(film);
    }

    public Film setLikeByUser(Long idUser, Long idFilm) {
        validateIdFilm(idFilm, "Невозможно поставить like фильму с ID - [{}], так как его нет.");
        validateIdUser(idUser, "Невозможно поставить like пользователя с ID - [{}], так как его нет.");
        return filmStorage.setLikeByUserId(idUser, idFilm);
    }

    public Film deleteLikeByUser(Long idUser, Long idFilm) {
        validateIdFilm(idFilm, "Невозможно удалить like фильму с ID - [{}], так как его нет.");
        validateIdUser(idUser, "Невозможно удалить like пользователя с ID - [{}], так как его нет.");
        return filmStorage.deleteLikeByUserId(idUser, idFilm);
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count <= 0) {
            log.warn("Значение count не может быть меньше 0. Передано - [{}].", count);
            throw new FilmorateValidationException("Значение  count должно быть выше 0.");
        }
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getCountLikes).reversed())
                .limit(count)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private void validateIdFilm(Long id, String logMessage) {
        if (filmStorage.getFilmById(id).isEmpty()) {
            log.warn(logMessage, id);
            throw new FilmorateNotFoundException("Пользователя с ID - [" + id + "] нет");
        }
    }

    private void validateIdUser(Long id, String logMessage) {
        if (userStorage.getUserById(id).isEmpty()) {
            log.warn(logMessage, id);
            throw new FilmorateNotFoundException("Пользователя с ID - [" + id + "] нет");
        }
    }

    private void validationDateRelease(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("У фильма с ID - [{}], name - [{}], releaseDate - [{}] некорректная дата релиза. Меньше 1895.12.28",
                    film.getId(), film.getName(), film.getReleaseDate());
            throw new FilmorateValidationException("Не верная дата релиза.");
        }
    }

}
