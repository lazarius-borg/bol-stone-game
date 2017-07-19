package info.lazo.lazarev.bol.stone.game.control;

import info.lazo.lazarev.bol.stone.game.model.GameEvent;
import info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.INVITE;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.INVITE_REJECT;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.IVNITE_ACCEPT;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.MOVE;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.REGISTER;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.START;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author lazo.lazarev
 */
public class GameEventDecoder implements Decoder.Text<GameEvent> {

    private List<String> gameEventTypes;

    @Override
    public GameEvent decode(String message) throws DecodeException {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jo = reader.readObject();
            GameEvent ge = new GameEvent();
            if (jo.containsKey("eventType")) {
                ge.setEventType(GameEventType.valueOf(jo.getString("eventType")));
            }
            if (jo.containsKey("player")) {
                ge.setPlayer(jo.getString("player"));
            }
            if (jo.containsKey("pitPosition")) {
                ge.setPitPosition(jo.getInt("pitPosition"));
            }
            if (jo.containsKey("initiator")) {
                ge.setInitiator(jo.getString("initiator"));
            }
            return ge;
        }
    }

    @Override
    public boolean willDecode(String message) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jo = reader.readObject();
            if (jo.containsKey("eventType") && gameEventTypes.contains(jo.getString("eventType"))) {
                return true;
            }
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {
        gameEventTypes = Arrays.asList(REGISTER, INVITE, IVNITE_ACCEPT, INVITE_REJECT, START, MOVE).stream().map(e -> e.name()).collect(
                Collectors.toList());
    }

    @Override
    public void destroy() {
    }

}
