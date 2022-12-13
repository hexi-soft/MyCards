package hexi.web.servlet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

//import util.HTMLFilter;

/**
 * Example web socket servlet for chat.
 */
public class Chat extends WebSocketServlet {

    private static final long serialVersionUID = 1L;

    private static final String GUEST_PREFIX = "Student";

    private final AtomicInteger connectionIds = new AtomicInteger(0);
    private final Set<ChatMessageInbound> connections =
            new CopyOnWriteArraySet<ChatMessageInbound>();

    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,
            HttpServletRequest request) {
    	String uid = request.getParameter("uid");
        //return new ChatMessageInbound(connectionIds.incrementAndGet());
    	return new ChatMessageInbound(uid);
    }

    private final class ChatMessageInbound extends MessageInbound {

        private final String nickname;
        
        private ChatMessageInbound(String uid) {
            this.nickname = uid;
        }

        @Override
        protected void onOpen(WsOutbound outbound) {
            connections.add(this);
            String message = String.format("* %s %s %s",
            		GUEST_PREFIX, nickname, "has joined.");
            broadcast(message);
        }

        @Override
        protected void onClose(int status) {
            connections.remove(this);
            String message = String.format("* %s %s",
                    nickname, "has left.");
            broadcast(message);
        }

        @Override
        protected void onBinaryMessage(ByteBuffer message) throws IOException {
            throw new UnsupportedOperationException(
                    "Binary message not supported.");
        }

        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {
            // Never trust the client
            //String filteredMessage = String.format("%s: %s",
           //         nickname, HTMLFilter.filter(message.toString()));
        	String filteredMessage = nickname+": "+message.toString();
        	broadcast(filteredMessage);
        }

        private void broadcast(String message) {
            for (ChatMessageInbound connection : connections) {
                try {
                    CharBuffer buffer = CharBuffer.wrap(message);
                    connection.getWsOutbound().writeTextMessage(buffer);
                } catch (IOException ignore) {
                    // Ignore
                }
            }
        }
    }
}