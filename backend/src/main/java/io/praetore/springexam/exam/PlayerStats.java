package io.praetore.springexam.exam;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "player_stats")
public class PlayerStats {

    public static final String DEFAULT_PLAYER_ID = "local";

    @Id
    private String id = DEFAULT_PLAYER_ID;

    @Column(nullable = false)
    private int elo = 1000;

    @Column(nullable = false)
    private int answered;

    @Column(nullable = false)
    private int correctCount;

    protected PlayerStats() {
    }

    public PlayerStats(String id, int elo, int answered, int correctCount) {
        this.id = id;
        this.elo = elo;
        this.answered = answered;
        this.correctCount = correctCount;
    }

    public static PlayerStats fresh() {
        return new PlayerStats(DEFAULT_PLAYER_ID, 1000, 0, 0);
    }

    public String getId() {
        return id;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getAnswered() {
        return answered;
    }

    public void setAnswered(int answered) {
        this.answered = answered;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public void reset() {
        this.elo = 1000;
        this.answered = 0;
        this.correctCount = 0;
    }
}
