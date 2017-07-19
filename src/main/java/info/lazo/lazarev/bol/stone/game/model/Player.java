package info.lazo.lazarev.bol.stone.game.model;

/**
 *
 * @author lazo.lazarev
 */
public class Player {

    private String name;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
