package th.mfu.config;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MyHandler extends TextWebSocketHandler {

    private final Random random = new Random();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Schedule a task to send random text every 5 seconds after connection is established
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sendRandomText(session);
            }
        };

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(task, 0, 5000);
    }

    private void sendRandomText(WebSocketSession session) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String randomText = "Random text: " + random.nextInt(100) + " at " + sdf.format(new Date());

        try {
            session.sendMessage(new TextMessage(randomText));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Handle incoming messages if needed
    }
}
