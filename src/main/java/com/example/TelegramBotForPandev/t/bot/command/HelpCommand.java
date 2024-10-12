package com.example.TelegramBotForPandev.t.bot.command;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelpCommand implements BotCommand {
    /**
     * Команда для отображения списка доступных команд.
     */
    @Override
    public void execute(DefaultAbsSender sender, Update update) {
        String helpMessage = "Доступные команды:\n" +
                "/viewTree - Показать дерево категорий\n" +
                "/addElement <название> - Добавить корневой элемент\n" +
                "/addElement <родитель> <название> - Добавить дочерний элемент\n" +
                "/removeElement <название> - Удалить элемент\n" +
                "/download - Скачать дерево категорий в Excel\n" +
                "/upload - Загрузить дерево категорий из Excel\n" +
                "/help - Показать эту справку";

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(helpMessage);

        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}