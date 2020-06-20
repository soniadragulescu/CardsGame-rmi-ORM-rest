package services;

import model.Game;
import model.User;

import java.util.List;

public interface IService {
    User login(IObserver client, String username, String password);
    void logout(IObserver client, String username);
    void updateUser(String username, List<Integer> cards);
    Game getLastGame();
    void startGame();
    void cardSent(String user, Integer card);
}
