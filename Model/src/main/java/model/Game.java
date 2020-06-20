package model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Game")
public class Game implements Serializable {

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ElementCollection
    private List<Integer> cards;

    @ElementCollection
    private List<String> users;

    @Column(name="Round")
    private Integer round;

    public Game() {
        this.cards=new ArrayList<>();
        this.users=new ArrayList<>();
        this.round=0;
    }

    public Game(Integer id,List<Integer> cards, List<String> users) {
        this.id = id;
        this.cards = cards;
        this.users=users;
        this.round=0;
    }

    public Game(List<Integer> cards, List<String> users) {
        this.cards = cards;
        this.users=users;
        this.round=0;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
