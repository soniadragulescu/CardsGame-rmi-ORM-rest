package services;

import model.Game;
import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface IObserver extends Remote {
    void nextRound(HashMap<String, Integer> cards, List<User> users) throws RemoteException;
    void someoneStartedTheGame(List<User> users) throws  RemoteException;
}
