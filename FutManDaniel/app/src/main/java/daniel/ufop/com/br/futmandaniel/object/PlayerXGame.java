package daniel.ufop.com.br.futmandaniel.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by daniel on 06/07/17.
 */

public class PlayerXGame implements Parcelable, Serializable {

    private int playerXgame;    /* Identificador da ligação entre o jogador e o jogo */
    private int game;           /* Identificador do jogo */
    private int player;         /* Identificador do jogador */

    public PlayerXGame(int playerXgame, int game, int player) {
        this.playerXgame = playerXgame;
        this.game = game;
        this.player = player;
    }

    public int getPlayerXgame() {
        return playerXgame;
    }

    public void setPlayerXgame(int playerXgame) {
        this.playerXgame = playerXgame;
    }

    public int getGame() {
        return game;
    }

    public void setGame(int game) {
        this.game = game;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public static final Creator<PlayerXGame> CREATOR = new Creator<PlayerXGame>() {
        @Override
        public PlayerXGame createFromParcel(Parcel in) {
            return new PlayerXGame(
                    in.readInt(),
                    in.readInt(),
                    in.readInt()
            );
        }

        @Override
        public PlayerXGame[] newArray(int size) {
            return new PlayerXGame[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(playerXgame);
        dest.writeInt(game);
        dest.writeInt(player);
    }
}
