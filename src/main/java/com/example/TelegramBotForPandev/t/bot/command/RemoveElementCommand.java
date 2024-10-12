package com.example.TelegramBotForPandev.t.bot.command;

import com.example.TelegramBotForPandev.service.CategoryService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class RemoveElementCommand implements BotCommand {

    private final CategoryService categoryService;

    public RemoveElementCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void execute(DefaultAbsSender sender, Update update) {
        String messageText = update.getMessage().getText();
        String[] parts = messageText.trim().split("\\s+", 2);

        String response;
        if (parts.length == 2) {
            String name = parts[1];
            try {
                categoryService.deleteCategory(name);
                response = "Категория '" + name + "' и все ее дочерние категории удалены.";
            } catch (Exception e) {
                response = "Ошибка: " + e.getMessage();
            }
        } else {
            response = "Неверный формат команды. Используйте /removeElement <название>";
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