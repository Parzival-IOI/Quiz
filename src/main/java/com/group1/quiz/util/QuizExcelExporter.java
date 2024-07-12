package com.group1.quiz.util;

import com.group1.quiz.model.PlayModel;
import com.group1.quiz.model.UserModel;
import com.group1.quiz.repository.UserRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class QuizExcelExporter {
    private final XSSFWorkbook workBook;
    private XSSFSheet sheet;
    private final List<PlayModel> plays;
    private final UserRepository userRepository;

    public QuizExcelExporter(List<PlayModel> plays, UserRepository userRepository) {
        this.plays = plays;
        this.userRepository = userRepository;
        workBook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workBook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workBook.createCellStyle();
        XSSFFont font = workBook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);

        createCell(row, 0, "Username", style);
        createCell(row, 1, "Email", style);
        createCell(row, 2, "Score", style);
        createCell(row, 3, "Created Date", style);
        createCell(row, 4, "Updated Date", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workBook.createCellStyle();
        XSSFFont font = workBook.createFont();
        font.setFontHeight(11);
        style.setFont(font);
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (PlayModel play : plays) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            Optional<UserModel> userModel = userRepository.findUserByUsername(play.getUsername());

            if(userModel.isPresent()) {
                createCell(row, columnCount++, play.getUsername(), style);
                createCell(row, columnCount++, userModel.get().getEmail(), style);
                createCell(row, columnCount++, Double.toString(play.getScore()), style);
                createCell(row, columnCount++, formatter.format(play.getCreatedAt()), style);
                createCell(row, columnCount, formatter.format(play.getUpdatedAt()), style);
            } else {
                createCell(row, columnCount++, play.getUsername(), style);
                createCell(row, columnCount++, "---", style);
                createCell(row, columnCount++, Double.toString(play.getScore()), style);
                createCell(row, columnCount++, formatter.format(play.getCreatedAt()), style);
                createCell(row, columnCount, formatter.format(play.getUpdatedAt()), style);
            }

        }
    }

    public void export(HttpServletResponse response) throws Exception {
        try {
            writeHeaderLine();
            writeDataLines();

            ServletOutputStream outputStream = response.getOutputStream();
            workBook.write(outputStream);
            workBook.close();

            outputStream.close();

        } catch (Exception e) {
            throw new ResponseStatusException("Cannot Generate Excel " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
