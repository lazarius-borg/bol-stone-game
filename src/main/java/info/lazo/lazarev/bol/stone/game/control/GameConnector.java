package info.lazo.lazarev.bol.stone.game.control;

import info.lazo.lazarev.bol.stone.game.model.GameEvent;
import info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.REGISTERED;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.REGISTRATION_ACCEPTED;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.REGISTRATION_REJECTED;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.USER_LEFT;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.USER_REGISTERED;
import static info.lazo.lazarev.bol.stone.game.model.GameEvent.GameEventType.USER_UNREGISTERED;
import info.lazo.lazarev.bol.stone.game.model.Player;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author lazo.lazarev
 */
@ApplicationScoped
@ServerEndpoint(value = "/play", decoders = {GameEventDecoder.class}, encoders = {GameEventEncoder.class})
public class GameConnector {

    private static ConcurrentMap<Session, Player> players = new ConcurrentHashMap<>();

    @OnOpen
    public void connect(Session session) {
        System.out.println(session.getId());
    }

    @OnClose
    public void disconnect(Session expiringSession) {
        GameEvent ge = new GameEvent();
        ge.setEventType(USER_UNREGISTERED);
        String playerName = players.get(expiringSession).getName();
        ge.setPlayer(playerName);
        ge.setMessage(String.format("User %s has left.", playerName));
        players.remove(expiringSession);
        expiringSession.getOpenSessions().forEach(session -> {
            if (!session.equals(expiringSession) && session.isOpen()) {
                try {
                    session.getBasicRemote().sendObject(ge);
                } catch (IOException | EncodeException ex) {
                    Logger.getLogger(GameConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    @OnMessage
    public void processCommand(Session session, GameEvent ge) throws IOException, EncodeException {
        System.out.println(session.getId() + ": " + ge.getEventType());
        switch (ge.getEventType()) {
            case REGISTER:
                if (players.containsKey(session)) {
                    GameEvent response = new GameEvent();
                    response.setEventType(REGISTERED);
                    response.setMessage("You are already registered, please invite another player to play the stones game.");
                    session.getBasicRemote().sendObject(response);
                } else if (canRegister(ge)) {
                    players.put(session, new Player(ge.getPlayer()));
                    GameEvent response = new GameEvent();
                    response.setEventType(REGISTRATION_ACCEPTED);
                    response.setMessage(String.format("Welcome %s, invite another player to play the stones game.", ge.getPlayer()));
                    session.getBasicRemote().sendObject(response);
                    notifyPlayers(session);
                    retrievePlayersInfo(session);
                } else {
                    GameEvent response = new GameEvent();
                    response.setEventType(REGISTRATION_REJECTED);
                    response.setMessage("Username already in use, please try a different one.");
                    session.getBasicRemote().sendObject(response);
                }
                break;
            case INVITE:
                final Optional<Entry<Session, Player>> invitee = players.entrySet().stream().filter(entry -> entry.getValue().getName().equals(
                        ge.getPlayer())).findFirst();
                if (invitee.isPresent()) {
                    Player initiator = players.get(session);
                    ge.setInitiator(initiator.getName());
                    ge.setMessage(String.format("User %s invited you to play.", initiator.getName()));
                    final Session inviteeSession = invitee.get().getKey();
                    if (inviteeSession.isOpen()) {
                        inviteeSession.getBasicRemote().sendObject(ge);
                    } else {
                        userLeft(session, ge.getPlayer());
                    }
                } else {
                    userLeft(session, ge.getPlayer());
                }
                break;
            case IVNITE_ACCEPT:
            case INVITE_REJECT:
                final Optional<Entry<Session, Player>> respond = players.entrySet().stream().filter(entry -> entry.getValue().getName().equals(
                        ge.getInitiator())).findFirst();
                if (respond.isPresent()) {
                    ge.setMessage(String.format("User %s %s your invitation to play.", ge.getPlayer(),
                            ge.getEventType().equals(GameEventType.IVNITE_ACCEPT) ? "accepted" : "rejected"));
                    final Session initiatorSession = respond.get().getKey();
                    if (initiatorSession.isOpen()) {
                        initiatorSession.getBasicRemote().sendObject(ge);
                    } else {
                        userLeft(session, ge.getPlayer());
                    }
                } else {
                    userLeft(session, ge.getPlayer());
                }
                break;
            case START:

                break;
            case MOVE:
        }
    }

    private boolean canRegister(GameEvent ge) {
        return !players.values().stream().map(player -> player.getName()).collect(Collectors.toList()).contains(ge.getPlayer());
    }

    private void notifyPlayers(Session newPlayerSession) {
        GameEvent ge = new GameEvent();
        ge.setEventType(USER_REGISTERED);
        String newUserName = players.get(newPlayerSession).getName();
        ge.setNewUser(newUserName);
        ge.setMessage(String.format("User %s has arrived.", newUserName));
        newPlayerSession.getOpenSessions().forEach(session -> {
            if (!session.equals(newPlayerSession) && session.isOpen()) {
                try {
                    session.getBasicRemote().sendObject(ge);
                } catch (IOException | EncodeException ex) {
                    Logger.getLogger(GameConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void retrievePlayersInfo(Session session) {
        final RemoteEndpoint.Basic remote = session.getBasicRemote();
        GameEvent ge = new GameEvent();
        ge.setEventType(USER_REGISTERED);
        players.forEach((s, p) -> {
            if (!session.equals(s)) {
                String userName = p.getName();
                ge.setNewUser(userName);
                ge.setMessage(String.format("User %s has registered.", userName));
                try {
                    remote.sendObject(ge);
                } catch (IOException | EncodeException ex) {
                    Logger.getLogger(GameConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void userLeft(Session session, String player) throws IOException, EncodeException {
        GameEvent ge = new GameEvent();
        ge.setEventType(USER_LEFT);
        ge.setMessage(String.format("User %s has left.", player));
        session.getBasicRemote().sendObject(ge);
    }
}
