package com.example.Catering.service;

import com.example.Catering.entity.Invoice;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class PdfInvoiceService {

    public byte[] generateInvoicePdf(Invoice invoice) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            // Шапка с цветом
            Paragraph title = new Paragraph("INVOICE")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.GREEN)
                    .setPadding(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));

            // Информация о счёте
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            infoTable.setWidth(UnitValue.createPercentValue(100));
            infoTable.addCell(createCell("INVOICE TO: " + invoice.getOrder().getUser().getFirstName() + " " + invoice.getOrder().getUser().getLastName(), false));
            infoTable.addCell(createCell("INVOICE #: " + invoice.getInvoiceNumber(), false));
            infoTable.addCell(createCell("Address: Main Road, City, Country", false));
            infoTable.addCell(createCell("DATE: " + invoice.getIssuedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), false));
            document.add(infoTable);

            document.add(new Paragraph("\n"));

            // Таблица с товарами
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Заголовки
            table.addHeaderCell(createHeaderCell("SL."));
            table.addHeaderCell(createHeaderCell("ITEM DESCRIPTION"));
            table.addHeaderCell(createHeaderCell("PRICE"));
            table.addHeaderCell(createHeaderCell("QTY."));
            table.addHeaderCell(createHeaderCell("TOTAL"));

            int count = 1;
            BigDecimal subTotal = BigDecimal.ZERO;

            for (var item : invoice.getOrder().getItems()) {
                BigDecimal lineTotal = item.getMenuItem().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                subTotal = subTotal.add(lineTotal);

                table.addCell(createCell(String.valueOf(count++), true));
                table.addCell(createCell(item.getMenuItem().getName(), true));
                table.addCell(createCell(item.getMenuItem().getPrice() + " €", true));
                table.addCell(createCell(String.valueOf(item.getQuantity()), true));
                table.addCell(createCell(lineTotal + " €", true));
            }

            document.add(table);

            document.add(new Paragraph("\n"));

            // Итоги
            Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{6, 2}));
            totalsTable.setWidth(UnitValue.createPercentValue(50));
            totalsTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);

            totalsTable.addCell(createCell("SUB TOTAL", true));
            totalsTable.addCell(createCell(subTotal + " €", true));

            BigDecimal tax = BigDecimal.ZERO; // если нужен налог
            totalsTable.addCell(createCell("TAX: 0%", true));
            totalsTable.addCell(createCell(tax + " €", true));

            BigDecimal total = subTotal.add(tax);
            totalsTable.addCell(createCell("TOTAL", true));
            totalsTable.addCell(createCell(total + " €", true));

            document.add(totalsTable);

            document.add(new Paragraph("\nThank you for your business!")
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\nAuthorized Sign").setTextAlignment(TextAlignment.RIGHT));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации PDF", e);
        }

        return outputStream.toByteArray();
    }

    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createCell(String text, boolean border) {
        Cell cell = new Cell().add(new Paragraph(text));
        if (!border) {
            cell.setBorder(Border.NO_BORDER);
        } else {
            cell.setTextAlignment(TextAlignment.CENTER);
        }
        return cell;
    }
}
