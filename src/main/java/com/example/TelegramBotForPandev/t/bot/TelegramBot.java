package com.example.TelegramBotForPandev.t.bot;

import com.example.TelegramBotForPandev.t.bot.command.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;
/**
 * Класс TelegramBot отвечает за обработку обновлений и выполнение соответствующих команд.
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final Map<String, BotCommand> commands = new HashMap<>();
    /**
     * Конструктор бота, инициализирует доступные команды.
     *
     * @param helpCommand          команда помощи
     * @param viewTreeCommand      команда отображения дерева
     * @param addElementCommand    команда добавления элемента
     * @param removeElementCommand команда удаления элемента
     * @param downloadCommand      команда скачивания дерева
     * @param uploadCommand        команда загрузки дерева
     */
    @Autowired
    public TelegramBot(HelpCommand helpCommand,
                       ViewTreeCommand viewTreeCommand,
                       AddElementCommand addElementCommand,
                       RemoveElementCommand removeElementCommand,
                       DownloadCommand downloadCommand,
                       UploadCommand uploadCommand) {
        commands.put("/help", helpCommand);
        commands.put("/viewTree", viewTreeCommand);
        commands.put("/addElement", addElementCommand);
        commands.put("/removeElement", removeElementCommand);
        commands.put("/download", downloadCommand);
        commands.put("/upload", uploadCommand);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                String commandKey = messageText.split("\\s+")[0];

                BotCommand command = commands.get(commandKey);

                if (command != null) {
                    command.execute(this, update);
                } else {
                    // Обработка неизвестной команды
                    SendMessage message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId().toString());
                    message.setText("Неизвестная команда. Введите /help для списка доступных команд.");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else if (update.getMessage().hasDocument()) {
                // Обработка загрузки файла для команды /upload
                BotCommand uploadCommand = commands.get("/upload");
                if (uploadCommand != null) {
                    uploadCommand.execute(this, update);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @PostConstruct
    public void start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("Telegram bot started successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}