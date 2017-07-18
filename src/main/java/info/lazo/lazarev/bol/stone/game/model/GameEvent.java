package info.lazo.lazarev.bol.stone.game.model;

/**
 *
 * @author lazo.lazarev
 */
public class GameEvent {

    private GameEventType eventType;
    private String player;
    private int pitPosition;
    private int initialPileSize;
    private int pileSizeAfterCapture;
    private String winner;
    private int winnerPoints;
    private String loser;
    private int loserPoints;

    boolean isMoveAgain() {
        return GameEventType.MOVE_AGAIN.equals(eventType);
    }

    void setPlayer(Player player) {
        this.setPlayer(player.getName());
    }

    private void setEventType(GameEventType eventType) {
        this.eventType = eventType;
    }

    private void setPitPosition(int pitPosition) {
        this.pitPosition = pitPosition;
    }

    private void setInitialPileSize(int initialPileSize) {
        this.initialPileSize = initialPileSize;
    }

    private void setPileSizeAfterCapture(int pileSizeAfterCapture) {
        this.pileSizeAfterCapture = pileSizeAfterCapture;
    }

    private void setWinner(String winner) {
        this.winner = winner;
    }

    private void setWinnerPoints(int winnerPoints) {
        this.winnerPoints = winnerPoints;
    }

    private void setLoser(String loser) {
        this.loser = loser;
    }

    private void setLoserPoints(int loserPoints) {
        this.loserPoints = loserPoints;
    }

    /**
     * @return the eventType
     */
    public GameEventType getEventType() {
        return eventType;
    }

    /**
     * @return the player
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @param player the player to set
     */
    public void setPlayer(String player) {
        this.player = player;
    }

    /**
     * @return the pitPosition
     */
    public int getPitPosition() {
        return pitPosition;
    }

    /**
     * @return the initialPileSize
     */
    public int getInitialPileSize() {
        return initialPileSize;
    }

    /**
     * @return the pileSizeAfterCapture
     */
    public int getPileSizeAfterCapture() {
        return pileSizeAfterCapture;
    }

    /**
     * @return the winner
     */
    public String getWinner() {
        return winner;
    }

    /**
     * @return the winnerPoints
     */
    public int getWinnerPoints() {
        return winnerPoints;
    }

    /**
     * @return the loser
     */
    public String getLoser() {
        return loser;
    }

    /**
     * @return the loserPoints
     */
    public int getLoserPoints() {
        return loserPoints;
    }

    public static enum GameEventType {
        START, TRANSFER, CAPTURE, GAME_OVER, MOVE_AGAIN;

    }

    public static class GameEventBuilder {

        private GameEventType eventType;
        private int initialPileSize;
        private int pitPosition;
        private String winner;
        private String loser;
        private int winnerPoints;
        private int loserPoints;
        private int pileSizeAfterCapture;

        private GameEventBuilder() {
        }

        public static GameEventBuilder create() {
            return new GameEventBuilder();
        }

        public GameEventBuilder startSawing(int position) {
            eventType = GameEventType.START;
            return this;
        }

        public GameEventBuilder transfer() {
            eventType = GameEventType.TRANSFER;
            return this;
        }

        public GameEventBuilder fromPit(int position) {
            this.pitPosition = position;
            return this;
        }

        public GameEventBuilder capture(int position) {
            eventType = GameEventType.CAPTURE;
            return this;
        }

        public GameEventBuilder pileAfterCapture(int pileSize) {
            this.pileSizeAfterCapture = pileSize;
            return this;
        }

        public GameEventBuilder withPile(PileOfStones pile) {
            this.initialPileSize = pile.countStones();
            return this;
        }

        public GameEventBuilder gameOver() {
            eventType = GameEventType.GAME_OVER;
            return this;
        }

        public GameEventBuilder winner(Player player, Integer total) {
            this.winner = player.getName();
            this.winnerPoints = total;
            return this;
        }

        public GameEventBuilder loser(Player player, Integer total) {
            this.loser = player.getName();
            this.loserPoints = total;
            return this;
        }

        public GameEventBuilder moveAgain() {
            eventType = GameEventType.MOVE_AGAIN;
            return this;
        }

        public GameEvent build() {

            GameEvent ge = new GameEvent();
            ge.setEventType(eventType);

            switch (eventType) {
                case START:
                    ge.setPitPosition(pitPosition);
                    ge.setInitialPileSize(initialPileSize);
                    break;
                case TRANSFER:
                    ge.setPitPosition(pitPosition);
                    break;
                case CAPTURE:
                    ge.setPitPosition(pitPosition);
                    ge.setPileSizeAfterCapture(pileSizeAfterCapture);
                    break;
                case MOVE_AGAIN:

                    break;
                case GAME_OVER:
                    ge.setWinner(winner);
                    ge.setWinnerPoints(winnerPoints);
                    ge.setLoser(loser);
                    ge.setLoserPoints(loserPoints);
            }
            return ge;
        }

    }

}
