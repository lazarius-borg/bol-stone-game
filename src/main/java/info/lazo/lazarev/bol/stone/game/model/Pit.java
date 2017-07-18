package info.lazo.lazarev.bol.stone.game.model;

import info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventBuilder;
import java.util.Queue;

/**
 * Represents an individual pit on a board. Each player has six pits plus a big pit, that are organized in <code>HalfBoard</code>. Each
 * <code>Pit</code> on a <code>HalfBoard</code> is connected to the next one starting with the initial <code>Pit</code>, and ending with the
 * <code>big</code> <code>Pit</code>.
 *
 * @author lazo.lazarev
 */
public class Pit {

    private int position;

    private HalfBoard halfBoard;

    private Pit nextPit;

    private Pit oppositePit;

    private PileOfStones pile;

    /**
     * @return the bigPit
     */
    public boolean isBigPit() {
        return halfBoard != null;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @param halfBoard the halfBoard to set
     */
    public void setHalfBoard(HalfBoard halfBoard) {
        this.halfBoard = halfBoard;
    }

    /**
     * @return the nextPit
     */
    public Pit getNextPit() {
        return nextPit;
    }

    /**
     * @param nextPit the nextPit to set
     */
    public void setNextPit(Pit nextPit) {
        this.nextPit = nextPit;
    }

    /**
     * @return the oppositePit
     */
    public Pit getOppositePit() {
        return oppositePit;
    }

    /**
     * @param oppositePit the oppositePit to set
     */
    public void setOppositePit(Pit oppositePit) {
        this.oppositePit = oppositePit;
    }

    /**
     * @param pile the pile to set
     */
    public void setPile(PileOfStones pile) {
        this.pile = pile;
    }

    public void move(Queue<GameEvent> gameEvents, int pit) {
        if (pit != this.position) {
            nextPit.move(gameEvents, pit);
        } else {
            GameEvent ge = GameEventBuilder.create().startSawing(position).withPile(pile).build();
            gameEvents.add(ge);
            nextPit.sowStones(gameEvents, pile, true);
        }
    }

    private void sowStones(Queue<GameEvent> gameEvents, PileOfStones playingPile, boolean ownHalfSide) {
        if (isBigPit() && !ownHalfSide) {
            halfBoard.getOppositeBoard().getFirstPit().sowStones(gameEvents, playingPile, !ownHalfSide);
        } else {
            playingPile.transferStone(this.pile);
            GameEvent ge = GameEventBuilder.create().transfer().fromPit(position).build();
            gameEvents.add(ge);
            if (playingPile.isEmpty()) {
                if (this.pile.didLastStoneLandedInAnEmptyPit(playingPile) && !isBigPit() && ownHalfSide) {
                    oppositePit.captureStones(this.pile);
                    ge = GameEventBuilder.create().capture(position).pileAfterCapture(pile.countStones()).build();
                    gameEvents.add(ge);
                } else if (isBigPit() && ownHalfSide) {
                    ge = GameEventBuilder.create().moveAgain().build();
                    gameEvents.add(ge);
                }
            } else {
                nextPit.sowStones(gameEvents, playingPile, ownHalfSide);
            }
        }
    }

    private void captureStones(PileOfStones oppositePile) {
        this.pile.transferAllStones(oppositePile);
    }

    boolean arePitsEmpty() {
        if (isBigPit()) {
            return true;
        } else {
            return pile.isEmpty() && nextPit.arePitsEmpty();
        }
    }

    int collectToBigPit(Queue<GameEvent> gameEvents) {
        if (!isBigPit()) {
            this.pile.transferAllStones(nextPit.pile);
            return nextPit.collectToBigPit(gameEvents);
        } else {
            return pile.countStones();
        }
    }

}
