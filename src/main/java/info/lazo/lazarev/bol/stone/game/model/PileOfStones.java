package info.lazo.lazarev.bol.stone.game.model;

/**
 * Represents the pile of stones in each pit. Each pit except for the big pit is initialized with six stones, except for the pile of the big pit,
 * which is initialized to zero stones.
 *
 * @author lazo.lazarev
 */
public class PileOfStones {

    private int stones;

    public PileOfStones(int stones) {
        this.stones = stones;
    }

    public boolean isEmpty() {
        return stones == 0;
    }

    public int countStones() {
        return stones;
    }

    public boolean didLastStoneLandedInAnEmptyPit(PileOfStones playingPile) {
        return playingPile.isEmpty() && stones == 1;
    }

    void transferStone(PileOfStones playingPile) {
        playingPile.stones--;
        stones++;
    }

    void transferAllStones(PileOfStones oppositePile) {
        oppositePile.stones += stones;
        stones = 0;
    }

}
