package com.example.TelegramBotForPandev.t.bot.command;



import com.example.TelegramBotForPandev.model.Category;
import com.example.TelegramBotForPandev.service.CategoryService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class DownloadCommand implements BotCommand {

    private final CategoryService categoryService;

    public DownloadCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void execute(DefaultAbsSender sender, Update update) {
        List<Category> rootCategories = categoryService.getRootCategories();

        // Генерация Exel файла
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Categories");

            int rowNum = 0;
            for (Category category : rootCategories) {
                rowNum = writeCategoryToSheet(sheet, category, rowNum, 0);
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            // ошибка при геенерации
            sendErrorMessage(sender, update, "Ошибка при генерации файла: " + e.getMessage());
            return;
        }

        // отправка файла юзеру
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        InputFile inputFile = new InputFile(inputStream, "categories.xlsx");

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(update.getMessage().getChatId().toString());
        sendDocument.setDocument(inputFile);
        sendDocument.setCaption("Дерево категорий");

        try {
            sender.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendErrorMessage(sender, update, "Ошибка при отправке файла: " + e.getMessage());
        }
    }

    private void sendErrorMessage(AbsSender sender, Update update, String text) {
        SendMessage errorMessage = new SendMessage();
        errorMessage.setChatId(update.getMessage().getChatId().toString());
        errorMessage.setText(text);
        try {
            sender.execute(errorMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private int writeCategoryToSheet(Sheet sheet, Category category, int rowNum, int level) {
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(level);
        cell.setCellValue(category.getName());

        for (Category child : category.getChildren()) {
            rowNum = writeCategoryToSheet(sheet, child, rowNum, level + 1);
        }

        return rowNum;
    }
}