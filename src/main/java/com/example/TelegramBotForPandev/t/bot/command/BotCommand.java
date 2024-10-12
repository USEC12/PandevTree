package com.example.TelegramBotForPandev.t.bot.command;


import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;


public interface BotCommand {
    void execute(DefaultAbsSender sender, Update update);
}