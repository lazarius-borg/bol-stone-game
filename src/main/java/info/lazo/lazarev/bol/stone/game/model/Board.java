package info.lazo.lazarev.bol.stone.game.model;

import info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventBuilder;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 *
 * @author lazo.lazarev
 */
public class Board {

    public static class BoardBuilder {

        private HalfBoard first;

        private List<Pit> firstHalfBoardPits;

        private HalfBoard second;

        private List<Pit> secondHalfBoardPits;

        public Board build(Player one, Player two) {

            Map<Player, HalfBoard> palyerBoard = new HashMap<>();

            first = new HalfBoard();
            second = new HalfBoard();
            associateHalfBoards(first, second);

            firstHalfBoardPits = new ArrayList<>();
            initializeHalfBoard(first, firstHalfBoardPits);

            secondHalfBoardPits = new ArrayList<>();
            initializeHalfBoard(second, secondHalfBoardPits);

            entangleOppositePits(firstHalfBoardPits, secondHalfBoardPits);

            return new Board(palyerBoard);
        }

        private void associateHalfBoards(HalfBoard first, HalfBoard second) {
            first.setOppositeBoard(second);
            second.setOppositeBoard(first);
        }

        private void initializeHalfBoard(HalfBoard halfBoard, List<Pit> pits) {

            for (int i = 0; i < 6; i++) {
                final Pit pit = new Pit();
                pit.setPosition(i + 1);
                pit.setPile(new PileOfStones(6));
                pits.add(pit);
                if (i > 0) {
                    pits.get(i - 1).setNextPit(pit);
                }
            }

            Pit bigPit = new Pit();
            bigPit.setPosition(7);
            bigPit.setPile(new PileOfStones(0));
            pits.get(5).setNextPit(bigPit);
            bigPit.setHalfBoard(halfBoard);
            pits.add(bigPit);

            halfBoard.setFirstPit(pits.get(0));
        }

        private void entangleOppositePits(List<Pit> firstHalfBoardPits, List<Pit> secondHalfBoardPits) {
            for (int i = 0; i < 6; i++) {
                Pit pit = firstHalfBoardPits.get(i);
                Pit oppositePit = secondHalfBoardPits.get(5 - i);
                pit.setOppositePit(oppositePit);
                oppositePit.setOppositePit(pit);
            }
        }
    }

    private Map<Player, HalfBoard> halfBoards;

    public Board(Map<Player, HalfBoard> halfBoards) {
        this.halfBoards = halfBoards;
    }

    public Queue<GameEvent> move(Player player, int pit) {

        Deque<GameEvent> gameEvents = new LinkedList<>();

        HalfBoard halfBoard = halfBoards.get(player);

        halfBoard.move(gameEvents, pit);

        GameEvent tailEvent = gameEvents.getLast();
        boolean isGameOver = halfBoards.values().stream().map(hb -> hb.areAllPitsEmpty()).reduce((a, b) -> a || b).get();

        if (isGameOver) {

            if (tailEvent.isMoveAgain()) {
                gameEvents.removeLast();
            }

            for (HalfBoard hb : halfBoards.values()) {
                hb.collectToBigPit(gameEvents);
            }
            Entry<Player, HalfBoard> winner = halfBoards.entrySet().stream().max((x, y) -> x.getValue().getTotal().compareTo(y.getValue().
                    getTotal())).get();
            Entry<Player, HalfBoard> loser = halfBoards.entrySet().stream().min((x, y) -> x.getValue().getTotal().compareTo(y.getValue().
                    getTotal())).get();

            GameEvent ge = GameEventBuilder.create().gameOver().winner(winner.getKey(), winner.getValue().getTotal()).loser(loser.getKey(),
                    loser.getValue().getTotal()).build();
            gameEvents.add(ge);

        }

        gameEvents.stream().forEach(ge -> ge.setPlayer(player));

        return gameEvents;
    }

}
