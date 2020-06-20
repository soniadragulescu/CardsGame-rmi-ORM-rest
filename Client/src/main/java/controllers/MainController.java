package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import services.IObserver;
import services.IService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController extends UnicastRemoteObject implements IObserver, Serializable {
    private IService service;
    private User user;
    private ObservableList<Integer> cards= FXCollections.observableArrayList();

    public MainController() throws RemoteException {
    }

    public void setUser(User user){
        this.user=user;
    }

    public void setService(IService service){
        this.service=service;
        init();
    }

    @FXML
    Label labelWait;

    @FXML
    Label labelAllCards;

    @FXML
    Button buttonStart;

    @FXML
    Button buttonLogout;

    @FXML
    Button buttonSendCard;

    @FXML
    ListView<Integer> cardsList;



    public void init(){
        buttonStart.setVisible(true);
        labelWait.setVisible(true);

        labelAllCards.setVisible(false);
        cardsList.setVisible(false);
        buttonSendCard.setVisible(false);

        cardsList.setItems(cards);
    }

    @FXML
    public void logout(){
        this.service.logout(this,this.user.getUsername());
        Platform.exit();
    }


    @Override
    public void someoneStartedTheGame(List<User> users){
        buttonStart.setVisible(false);
        labelWait.setVisible(false);

        cardsList.setVisible(true);
        buttonSendCard.setVisible(true);
        labelAllCards.setVisible(true);

        for(User u:users){
            if(u.getUsername().equals(this.user.getUsername())){
                this.user.setCards(u.getCards());
                cards.setAll(this.user.getCards());
                break;
            }
        }
    }

    @FXML
    public void buttonStartGamePressed(){
        buttonStart.setVisible(false);
        labelWait.setVisible(false);

        cardsList.setVisible(true);
        buttonSendCard.setVisible(true);
        labelAllCards.setVisible(true);

        service.startGame();
    }


    private static void showErrorMessage(String err){
        Alert message = new Alert(Alert.AlertType.ERROR);
        message.setTitle("Error message!");
        message.setContentText(err);
        message.showAndWait();
    }

    @FXML
    public void sendCard() {
        Integer card=cardsList.getSelectionModel().getSelectedItem();
        service.cardSent(this.user.getUsername(), card);
        buttonSendCard.setVisible(false);
    }

    @Override
    public void nextRound(HashMap<String, Integer> cardsrecived, List<User> users) throws RemoteException {
        User newuser=this.user;
//        String cardsSent="";
//        for(Integer c:cardsrecived.values()){
//            cardsSent+=c.toString();
//        }
//        String finalCardsSent = cardsSent;
        HashMap<String, Integer> cardsReceivedAux = new HashMap<>();
        for(Map.Entry<String, Integer> e : cardsrecived.entrySet()) {
            cardsReceivedAux.put(e.getKey(), e.getValue());
        }
        StringBuilder cardsSent= new StringBuilder();
        for(Integer c:cardsReceivedAux.values()){
            cardsSent.append(c.toString() + " ");
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                for(User u:users) {
                    if (u.getUsername().equals(newuser.getUsername())) {
                        newuser.setCards(u.getCards());
                    }
                }
                cards.setAll(newuser.getCards());
                labelAllCards.setText("Cards sent for the last round: "+ cardsSent.toString());

                buttonSendCard.setVisible(true);
                if(newuser.getCards().size()==0){
                    showErrorMessage("Ai gatat cartile, jocul s-a terminat! ");
                }
            }
        });

        this.user.setCards(newuser.getCards());
    }
}
