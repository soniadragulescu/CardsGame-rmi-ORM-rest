package services;

import model.Game;
import model.User;
import repos.GameRepo;
import repos.UserRepo;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements IService{
    private UserRepo userRepo;
    private GameRepo gameRepo;
    private List<IObserver> observers;
    private boolean gameStarted;
    List<Integer> cards= Arrays.asList(6,7,8,9,10,11,12,13);
    HashMap<String, Integer> currentCards;
    List<String> users;
    private final int defaultThreadsNo=5;

    public Service(UserRepo userRepo, GameRepo gameRepo) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        observers=new ArrayList<>();
        users=new ArrayList<>();
        currentCards=new HashMap<>();
        this.gameStarted=false;
    }

    @Override
    public User login(IObserver client, String username, String password) {
        if(gameStarted==true){
            return null;
        }
        else{
            User user=userRepo.findOne(username, password);
            if(user!=null){
                observers.add(client);
                users.add(username);
            }
            return user;
        }
    }

    @Override
    public void logout(IObserver client, String username) {
        observers.remove(client);
    }

    @Override
    public void updateUser(String username, List<Integer> cards) {
        User user=userRepo.findOneByUsername(username);
        user.setCards(cards);
        userRepo.update(user);
    }

    @Override
    public Game getLastGame() {
        return gameRepo.getLast();
    }

    @Override
    public void startGame() {
        List<Integer> cardsForUser=new ArrayList<>();
        List<Integer> initialCards=new ArrayList<>();
        for(int i=0; i< this.users.size(); i++){
            if(i%2==0){
                cardsForUser=Arrays.asList(6,8,10,12);
            }
            else{
                cardsForUser=Arrays.asList(7,9,11,13);
            }

            String username=this.users.get(i);
            User user=userRepo.findOneByUsername(username);
            user.setCards(cardsForUser);
            userRepo.update(user);
            initialCards.addAll(cardsForUser);
        }
        Game game=new Game(initialCards, this.users);
        gameRepo.save(game);

        List<User> allusers=new ArrayList<>();
        for(User u:userRepo.getAll()){
            List<Integer> cards2=new ArrayList<>();
            for(Integer c:u.getCards())
                cards2.add(c);
            u.setCards(cards2);
            allusers.add(u);
        }

        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer:observers) {
            executor.execute(() -> {
                try {
                    System.out.println("notifying users that the game started... ");
                    observer.someoneStartedTheGame(allusers);
                } catch (Exception e) {
                    System.out.println("error notifying users...");
                }
            });
        }
        executor.shutdown();
        this.gameStarted=true;
    }

    @Override
    public void cardSent(String user, Integer card) {
        this.currentCards.put(user,card);
        if(currentCards.size()==this.users.size()){
            //all participants have sent a card
            //we select a winner who has sent the biggest card
            String winner=getWinner();
            for (Map.Entry<String,Integer> e:currentCards.entrySet()
            ) {
                User oldUser=userRepo.findOneByUsername(e.getKey());
                List<Integer> oldCards=oldUser.getCards();
                //we delete the card each user has sent
                oldCards.remove(e.getValue());
                oldUser.setCards(oldCards);
                if(oldUser.getUsername().equals(winner)){
                    //if this user is the winner
                    //he gets all the other cards
                    //except the one he has already sent
                    List<Integer> cards=oldUser.getCards();
                    List<Integer> allCards=new ArrayList<>();
                    for(Integer c:currentCards.values()){
                        allCards.add(c);
                    }
                    allCards.remove(currentCards.get(oldUser));
                    cards.addAll(allCards);
                    oldUser.setCards(cards);
                }
                userRepo.update(oldUser);
            }
            //we notify all the users that a new round begins
            HashMap<String, Integer> cardstosend=new HashMap<>();
            for(Map.Entry<String,Integer> e :this.currentCards.entrySet()){
                cardstosend.put(e.getKey(), e.getValue());
            }
            notifyUsers(cardstosend);
            //and we clean the cards sent for this round
            this.currentCards.clear();
        }

    }

    private String getWinner(){
        Integer max=-1;
        for(Integer card:currentCards.values()){
            if(Collections.frequency(currentCards.values(), card)>1)
                //if a card is present more than once
                //than there is no winner
                return null;
            if(card>max)
                max=card;
        }
        for (Map.Entry<String,Integer> e:currentCards.entrySet()
             ) {
            if(e.getValue().equals(max))
                //the player who has sent the biggest card wins
                return e.getKey();
        }
        return null;
    }

    private void notifyUsers(HashMap<String, Integer> cardstosend){
        List<User> users=new ArrayList<>();
        for(User u: userRepo.getAll()) {
            List<Integer> cards2=new ArrayList<>();
            for(Integer c:u.getCards())
                cards2.add(c);
            u.setCards(cards2);
            users.add(u);
        }
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver observer:observers) {
            executor.execute(() -> {
                try {
                    System.out.println("notifying users for the next round...");
                    observer.nextRound(cardstosend,users);
                } catch (Exception e) {
                    System.out.println("error notifying users...");
                }
            });
        }
        executor.shutdown();
    }
}
