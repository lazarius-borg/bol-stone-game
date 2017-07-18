package info.lazo.lazarev.bol.stone.game.model;

import java.util.Queue;

/**
 *
 * @author lazo.lazarev
 */
public class HalfBoard {

    private Pit firstPit;

    private HalfBoard oppositeBoard;

    private int total;

    /**
     * @return the firstPit
     */
    public Pit getFirstPit() {
        return firstPit;
    }

    /**
     * @param firstPit the firstPit to set
     */
    public void setFirstPit(Pit firstPit) {
        this.firstPit = firstPit;
    }

    /**
     * @return the oppositeBoard
     */
    public HalfBoard getOppositeBoard() {
        return oppositeBoard;
    }

    /**
     * @param oppositeBoard the oppositeBoard to set
     */
    public void setOppositeBoard(HalfBoard oppositeBoard) {
        this.oppositeBoard = oppositeBoard;
    }

    /**
     * @return the total
     */
    public Integer getTotal() {
        return total;
    }

    public void move(Queue<GameEvent> gameEvents, int pit) {
        firstPit.move(gameEvents, pit);
    }

    public boolean areAllPitsEmpty() {
        return firstPit.arePitsEmpty();
    }

    void collectToBigPit(Queue<GameEvent> gameEvents) {
        total = firstPit.collectToBigPit(gameEvents);
    }

}
