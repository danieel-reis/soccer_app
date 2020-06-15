package daniel.ufop.com.br.futmandaniel.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by daniel on 06/07/17.
 */

public class Game implements Parcelable, Serializable {

    private int game;               /* Identificador do Jogo */
    private int size_team;          /* Tamanho do time */
    private int duration_match;     /* Duração de cada partida */
    private String description;     /* Descrição do jogo */
    private String date;            /* Data do jogo */
    private String time;            /* Hora do jogo */
    private String local;           /* Local do jogo */

    public Game(int game, int size_team, int duration_match, String description, String date, String time, String local) {
        this.game = game;
        this.size_team = size_team;
        this.duration_match = duration_match;
        this.description = description;
        this.date = date;
        this.time = time;
        this.local = local;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    public int getGame() {
        return game;
    }

    public void setGame(int game) {
        this.game = game;
    }

    public int getSize_team() {
        return size_team;
    }

    public void setSize_team(int size_team) {
        this.size_team = size_team;
    }

    public int getDuration_match() {
        return duration_match;
    }

    public void setDuration_match(int duration_match) {
        this.duration_match = duration_match;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(
                    in.readInt(),
                    in.readInt(),
                    in.readInt(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString());
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(game);
        dest.writeInt(size_team);
        dest.writeInt(duration_match);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(local);
    }
}
