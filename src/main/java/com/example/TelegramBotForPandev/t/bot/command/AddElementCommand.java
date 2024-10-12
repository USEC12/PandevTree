package com.example.TelegramBotForPandev.t.bot.command;

import com.example.TelegramBotForPandev.service.CategoryService;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AddElementCommand implements BotCommand {

    private final CategoryService categoryService;

    public AddElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void execute(DefaultAbsSender sender, Update update) {
        String messageText = update.getMessage().getText();
        String[] parts = messageText.trim().split("\\s+", 3);

        String response;
        try {
            if (parts.length == 2) {
                // Добавление корневого элемента
                String name = parts[1];
                categoryService.createCategory(name, null);
                response = "Корневая категория '" + name + "' успешно добавлена.";
            } else if (parts.length == 3) {
                // Добавление дочернего элемента
                String parentName = parts[1];
                String name = parts[2];
                categoryService.createCategory(name, parentName);
                response = "Категория '" + name + "' успешно добавлена в '" + parentName + "'.";
            } else {
                response = "Неверный формат команды. Используйте:\n" +
                        "/addElement <название>\n" +
                        "/addElement <родитель> <название>";
            }
        } catch (Exception e) {
            response = "Ошибка: " + e.getMessage();
        }

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText(response);

        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}