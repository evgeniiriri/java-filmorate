package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
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
        validateIdUser(user.getId(), "Невозможно обновить пользователя с ID - [{}], так как его нет.");
        return userStorage.update(user);
    }

    public Collection<User> getFriends(Long id) {
        validateIdUser(id, "Невозможно показать друзей пользователя с ID - [{}], так как его нет.");

        Set<Long> friends = userStorage.getUserById(id).get().getFriends();
        return userStorage.getAllUsers().stream()
                .filter(user -> friends.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        validateIdUser(id, "Невозможно показать общих друзей пользователя с ID - [{}], так как его нет.");
        validateIdUser(otherId, "Невозможно показать общих друзей пользователя с ID - [{}], так как его нет.");

        Set<Long> listFriendsUser = userStorage.getUserById(id).get().getFriends();
        Set<Long> listFriendsOtherUser = userStorage.getUserById(otherId).get().getFriends();
        //Оставим только общих друзей методом rentainAll.
        listFriendsUser.retainAll(listFriendsOtherUser);

        return userStorage.getAllUsers().stream()
                .filter(user -> listFriendsUser.contains(user.getId()))
                .toList();
    }


    public User addFriend(Long idUser, Long idFriend) {
        validateIdUser(idUser, "Невозможно добавить в друзья пользователя с ID - [{}], так как его нет.");
        validateIdUser(idFriend, "Невозможно добавить в друзья пользователя с ID - [{}], так как его нет.");

        userStorage.getUserById(idUser).get().addFriend(idFriend);
        userStorage.getUserById(idFriend).get().addFriend(idUser);

        return userStorage.getUserById(idUser).get();
    }

    public User deletedFriends(Long idUser, Long idFriend) {
        validateIdUser(idUser, "Невозможно удалить из друзей пользователя с ID - [{}], так как его нет.");
        validateIdUser(idFriend, "Невозможно удалить из друзей пользователя с ID - [{}], так как его нет.");

        userStorage.getUserById(idUser).get().deletedFriend(idFriend);
        userStorage.getUserById(idFriend).get().deletedFriend(idUser);

        return userStorage.getUserById(idUser).get();
    }

    private void validateIdUser(Long id, String logMessage) {
        if (userStorage.getUserById(id).isEmpty()) {
            log.warn(logMessage, id);
            throw new FilmorateNotFoundException("Пользователя с ID - [" + id + "] нет");
        }
    }
}
