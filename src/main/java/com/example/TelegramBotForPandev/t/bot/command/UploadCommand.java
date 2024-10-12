package com.example.TelegramBotForPandev.t.bot.command;

import com.example.TelegramBotForPandev.model.Category;
import com.example.TelegramBotForPandev.service.CategoryService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class UploadCommand implements BotCommand {

    private final CategoryService categoryService;

    public UploadCommand(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void execute(DefaultAbsSender sender, Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());

        if (update.getMessage().hasDocument()) {
            Document document = update.getMessage().getDocument();
            if (document.getFileName().endsWith(".xlsx")) {
                try {
                    // Получение файла
                    String fileId = document.getFileId();
                    GetFile getFileMethod = new GetFile();
                    getFileMethod.setFileId(fileId);
                    File file = sender.execute(getFileMethod);

                    InputStream inputStream;
                    if (sender instanceof DefaultAbsSender) {
                        DefaultAbsSender absSender = (DefaultAbsSender) sender;
                        inputStream = absSender.downloadFileAsStream(file.getFilePath());
                    } else {
                        message.setText("Ошибка: Не удалось загрузить файл.");
                        sender.execute(message);
                        return;
                    }

                    // Очистка существующих категорий
                    categoryService.deleteAllCategories();

                    // Чтение Excel-файла
                    Workbook workbook = new XSSFWorkbook(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    // Парсинг и сохранение категорий
                    Map<Integer, Category> levelMap = new HashMap<>();
                    for (Row row : sheet) {
                        Cell cell = row.getCell(0);
                        if (cell == null) continue;

                        int level = cell.getColumnIndex();
                        String name = cell.getStringCellValue();

                        if (name == null || name.isEmpty()) continue;

                        Category category = new Category();
                        category.setName(name);

                        if (level == 0) {
                            // Корневая категория
                            categoryService.createCategory(name, null);
                            levelMap.put(0, category);
                        } else {
                            // Дочерняя категория
                            Category parentCategory = levelMap.get(level - 1);
                            if (parentCategory != null) {
                                categoryService.createCategory(name, parentCategory.getName());
                                levelMap.put(level, category);
                            } else {
                                message.setText("Ошибка: Родительская категория для '" + name + "' не найдена.");
                                sender.execute(message);
                                return;
                            }
                        }
                    }

                    message.setText("Файл успешно загружен и данные обновлены.");
                } catch (Exception e) {
                    e.printStackTrace();
                    message.setText("Ошибка при обработке файла: " + e.getMessage());
                }
            } else {
                message.setText("Пожалуйста, загрузите файл в формате .xlsx");
            }
        } else {
            message.setText("Пожалуйста, отправьте файл Excel после команды /upload.");
        }

        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}