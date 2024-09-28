package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class.getName());
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.getAllUsers();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Collection<User> getFriends(Long id) {
        if (userStorage.getUserById(id).isEmpty()) {
            log.warn("Невозможно добавить в друзья пользователя с ID - [{}], так как его нет.", id);
            throw new NotFoundException("Пользователя с ID - [" + id + "] нет");
        }
        Set<Long> friends = userStorage.getUserById(id).get().getFriends();
        return userStorage.getAllUsers().stream()
                .filter(user -> friends.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        if (userStorage.getUserById(id).isEmpty()) {
            log.warn("Невозможно добавить в друзья пользователя с ID - [{}], так как его нет.", id);
            throw new NotFoundException("Пользователя с ID - [" + id + "] нет");
        }
        if (userStorage.getUserById(otherId).isEmpty()) {
            log.warn("Невозможно добавить в друзья пользователя с ID - [{}], так как его нет.", otherId);
            throw new NotFoundException("Пользователя с ID - [" + otherId + "] нет");
        }

        Set<Long> listFriendsUser = userStorage.getUserById(id).get().getFriends();
        Set<Long> listFriendsOtherUser = userStorage.getUserById(otherId).get().getFriends();
        //Оставим только общих друзей методом rentainAll.
        listFriendsUser.retainAll(listFriendsOtherUser);

        return userStorage.getAllUsers().stream()
                .filter(user -> listFriendsUser.contains(user.getId()))
                .toList();
    }


    public User addFriend(Long idUser, Long idFriend) {

        if (userStorage.getUserById(idFriend).isEmpty()) {
            log.warn("Невозможно добавить в друзья пользователя с ID - [{}], так как его нет.", idFriend);
            throw new NotFoundException("Пользователя с ID - [" + idFriend + "] нет");
        }
        if (userStorage.getUserById(idUser).isEmpty()) {
            log.warn("Невозможно добавить в друзья пользователя с ID - [{}], так как его нет.", idUser);
            throw new NotFoundException("Пользователя с ID - [" + idUser + "] нет");
        }

        userStorage.getUserById(idUser).get().addFriend(idFriend);
        userStorage.getUserById(idFriend).get().addFriend(idUser);

        return userStorage.getUserById(idUser).get();
    }

    public User deletedFriends(Long idUser, Long idFriend) {
        
        if (userStorage.getUserById(idUser).isPresent()){
            userStorage.getUserById(idUser).get().deletedFriend(idFriend);
        } else {
            log.warn("Невозможно удалить из друзей пользователя с ID - [{}], так как его нет.", idFriend);
            throw new NotFoundException("Пользователя с ID - [" + idFriend + "] нет");
        }
        if (userStorage.getUserById(idFriend).isPresent()){
            userStorage.getUserById(idFriend).get().deletedFriend(idUser);
        } else {
            log.warn("Невозможно удалить из друзей пользователя с ID - [{}], так как его нет.", idUser);
            throw new NotFoundException("Пользователя с ID - [" + idUser + "] нет");
        }

        return userStorage.getUserById(idUser).get();
    }
}
