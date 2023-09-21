package telegrambot;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class ScheduledTasks {

    @Scheduled(cron = "0 45 21 * * MON-FRI")
    public void reportCurrentTime() {
        Bot bot = new Bot();
        bot.sendText(-4098921454L, "Через 15 минут начинается митинг, " + bot.receiveOrder().toLowerCase());
    }

}
