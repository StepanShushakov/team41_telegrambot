package telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "team41info_bot";
    }

    @Override
    public String getBotToken() {
        return "6671123290:AAEoAWFtq2uASaP4TsCu0OQ-xQ3mTVLF9As";
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();

        saveMessage("INCOMING", message.toString());
        if (message.getText().equalsIgnoreCase("/order")) {
            String outgoingText = receiveOrder();
            sendText(message.getFrom().getId(), outgoingText);
        }

    }

    public String receiveOrder() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy г.");
        Calendar calendar = new GregorianCalendar();
        return "Сегодня, "
                + formatter.format(calendar.getTime())
                + ", порядок выступления: <b><u>"
                + ((calendar.WEEK_OF_YEAR) % 2 == 0 ? "с конца" : "с начала")
                + "</u></b>";
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        sm.setParseMode(ParseMode.HTML);
        try {
            execute(sm);                        //Actually sending the message
            saveMessage("OUTGOING", what);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private void saveMessage(String direction, String body) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:54321/telegrambot",
                    "root",
                    "root");
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute("insert into messages (direction, body, time) values ('"
                    + direction +"' , '"
                    + body + "', '"
                    + formatter.format(new GregorianCalendar().getTime()) + "');");
            connection.commit();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
