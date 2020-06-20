package model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="User")
public class User implements Serializable {
    @Id
    @Column(name="Username")
    private String username;

    @Column(name="Password")
    private String password;

    @ElementCollection
    private List<Integer> cards;

    public User(){
        this.cards=new ArrayList<>();
    }

    public User(String username, String password, List<Integer> cards) {
        this.username = username;
        this.password = password;
        this.cards = cards;
    }



    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.cards=new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public void setCards(List<Integer> cards) {
        this.cards = cards;
    }
}
