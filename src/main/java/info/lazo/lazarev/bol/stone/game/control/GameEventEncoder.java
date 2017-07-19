package info.lazo.lazarev.bol.stone.game.control;

import info.lazo.lazarev.bol.stone.game.model.GameEvent;
import info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.CAPTURE;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.GAME_OVER;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.INVITE;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.INVITE_REJECT;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.IVNITE_ACCEPT;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.MOVE;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.MOVE_AGAIN;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.REGISTERED;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.REGISTRATION_ACCEPTED;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.REGISTRATION_REJECTED;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.START;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.TRANSFER;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.USER_REGISTERED;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.USER_UNREGISTERED;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author lazo.lazarev
 */
public class GameEventEncoder implements Encoder.Text<GameEvent> {

    private List<GameEventType> gameEventTypes;

    /*
    private GameEventType eventType;
    private String player;
    private int pitPosition;
    private int initialPileSize;
    private int pileSizeAfterCapture;
    private String winner;
    private int winnerPoints;
    private String loser;
    private int loserPoints;
    private String message;
     */
    @Override
    public String encode(GameEvent ge) throws EncodeException {
        if (!gameEventTypes.contains(ge.getEventType())) {
            throw new EncodeException(ge, "Not appropriate response event type.");
        }
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("eventType", ge.getEventType().name());
        switch (ge.getEventType()) {
            case REGISTRATION_ACCEPTED:
            case REGISTRATION_REJECTED:
                builder.add("message", ge.getMessage());
                break;
            case USER_REGISTERED:
                builder.add("message", ge.getMessage());
                builder.add("newUser", ge.getNewUser());
                break;
            case USER_UNREGISTERED:
                builder.add("message", ge.getMessage());
                builder.add("player", ge.getPlayer());
                break;
            case INVITE:
                builder.add("message", ge.getMessage());
                builder.add("player", ge.getPlayer());
                builder.add("initiator", ge.getInitiator());
                break;
            case INVITE_REJECT:
            case IVNITE_ACCEPT:
                builder.add("message", ge.getMessage());
                builder.add("player", ge.getPlayer());
                builder.add("initiator", ge.getInitiator());
                break;
        }
        JsonObject jo = builder.build();
        try (Writer writer = new StringWriter()) {
            Json.createWriter(writer).writeObject(jo);
            return writer.toString();
        } catch (IOException ex) {
            throw new EncodeException(ge, "Error while writing JSON.", ex);
        }
    }

    @Override
    public void init(EndpointConfig config) {
        gameEventTypes = Arrays.
                asList(REGISTERED, USER_REGISTERED, REGISTRATION_ACCEPTED, REGISTRATION_REJECTED, INVITE, IVNITE_ACCEPT, INVITE_REJECT, START,
                        MOVE, TRANSFER, CAPTURE, GAME_OVER, MOVE_AGAIN, USER_UNREGISTERED);
    }

    @Override
    public void destroy() {
    }

}
