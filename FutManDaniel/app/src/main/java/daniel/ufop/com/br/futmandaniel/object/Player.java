package daniel.ufop.com.br.futmandaniel.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by daniel on 06/07/17.
 */

public class Player implements Parcelable, Serializable{

    private int player;         /* Identificador do Jogador */
    private String name;        /* Nome */
    private String phone;       /* Telefone */
    private int qtd_V;          /* Quantidade de jogos em que participou e obteve vitória */
    private int qtd_D;          /* Quantidade de jogos em que participou e obteve derrota */
    private int qtd_E;          /* Quantidade de jogos em que participou e obteve empate */
    private int size_games;     /* Quantidade de jogos em que participou */
    private int is_checked;     /* Testa se um jogador irá ou não participar de um jogo */

    public Player(int player, String name, String phone, int qtd_V, int qtd_D, int qtd_E, int size_games, int is_checked) {
        this.player = player;
        this.name = name;
        this.phone = phone;
        this.qtd_V = qtd_V;
        this.qtd_D = qtd_D;
        this.qtd_E = qtd_E;
        this.size_games = size_games;
        this.is_checked = is_checked;
    }

    public static Comparator<Player> ComparatorPlayerNome = new Comparator<Player>() {

        @Override
        public int compare(Player o1, Player o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public static Comparator<Player> ComparatorPlayerQtd = new Comparator<Player>() {

        @Override
        public int compare(Player o1, Player o2) {
            if (o2.getQtd_V() - o1.getQtd_V() != 0)
                return o2.getQtd_V() - o1.getQtd_V();
            else
                return o1.getSize_games() - o2.getSize_games();
        }
    };

    @Override
    public String toString() {
        return getName();
    }

    public int getIs_checked() {
        return is_checked;
    }

    public void setIs_checked(int is_checked) {
        this.is_checked = is_checked;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getQtd_V() {
        return qtd_V;
    }

    public void setQtd_V(int qtd_V) {
        this.qtd_V = qtd_V;
    }

    public int getQtd_D() {
        return qtd_D;
    }

    public void setQtd_D(int qtd_D) {
        this.qtd_D = qtd_D;
    }

    public int getQtd_E() {
        return qtd_E;
    }

    public void setQtd_E(int qtd_E) {
        this.qtd_E = qtd_E;
    }

    public int getSize_games() {
        return size_games;
    }

    public void setSize_games(int size_games) {
        this.size_games = size_games;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(
                    in.readInt(),
                    in.readString(),
                    in.readString(),
                    in.readInt(),
                    in.readInt(),
                    in.readInt(),
                    in.readInt(),
                    in.readInt()
                    );
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(player);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeInt(qtd_V);
        dest.writeInt(qtd_D);
        dest.writeInt(qtd_E);
        dest.writeInt(size_games);
        dest.writeInt(is_checked);
    }
}
