package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class.getName());
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("У пользователя с ID - [{}] для поля name используется поле login - [{}].", user.getId(), user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User newUser) {
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                log.info("У пользователя с ID - [{}] для поля name используется поле login - [{}].", newUser.getId(), newUser.getLogin());
                newUser.setName(newUser.getLogin());
            }

            oldUser.setName(newUser.getName());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());

            return oldUser;

        } else {
            log.warn("Пользователь с id - [{}], name - [{}], login - [{}], email - [{}] не найден",
                    newUser.getId(), newUser.getName(), newUser.getLogin(), newUser.getEmail());
            throw new FilmorateNotFoundException("Пользователь с id - " + newUser.getId() + " не найден");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь с id - [{}] не найден", id);
            throw new FilmorateNotFoundException("Пользователь с id - " + id + " не найден");
        }
        return users.values().stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst();

    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
