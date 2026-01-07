package com.example.Catering.service;

import com.example.Catering.entity.Invoice;
import com.example.Catering.entity.Order;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class PdfInvoiceService {

    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(46, 125, 50);       // темно-зеленый
    private static final DeviceRgb COMMENT_BG = new DeviceRgb(240, 248, 245);       // светлый зеленый
    private static final DeviceRgb ROW_BG = new DeviceRgb(245, 245, 245);           // светло-серый для чередования
    private static final Color TOTAL_BG = new DeviceRgb(200, 230, 201);             // светло-зеленый для итогов

    public byte[] generateInvoicePdf(Invoice invoice) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            Order order = invoice.getOrder();

            // ===== HEADER =====
            Paragraph title = new Paragraph("INVOICE")
                    .setFontSize(26)
                    .setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(HEADER_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(15);
            document.add(title);
            document.add(new Paragraph("\n"));

            // ===== ORDER INFO =====
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            infoTable.setWidth(UnitValue.createPercentValue(100));

            infoTable.addCell(noBorderCell("INVOICE TO:\n" + order.getUser().getFirstName() + " " + order.getUser().getLastName()));
            infoTable.addCell(noBorderCell("INVOICE #:\n" + invoice.getInvoiceNumber()));

            infoTable.addCell(noBorderCell("ADDRESS:\n" + (order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "—")));
            infoTable.addCell(noBorderCell("ORDER DATE:\n" + invoice.getIssuedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

            infoTable.addCell(noBorderCell("EVENT DATE:\n" + order.getEventDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            infoTable.addCell(noBorderCell("NUMBER OF GUESTS:\n" + order.getNumberOfGuests()));

            // COMMENT (FULL WIDTH)
            Cell commentCell = new Cell(1, 2)
                    .add(new Paragraph("COMMENT:\n" + (order.getComment() != null && !order.getComment().isBlank() ? order.getComment() : "—")))
                    .setBackgroundColor(COMMENT_BG)
                    .setPadding(8)
                    .setBorder(Border.NO_BORDER);
            infoTable.addCell(commentCell);

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // ===== ITEMS TABLE =====
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{1, 5, 2, 2, 2}));
            itemsTable.setWidth(UnitValue.createPercentValue(100));

            itemsTable.addHeaderCell(headerCell("№"));
            itemsTable.addHeaderCell(headerCell("ITEM DESCRIPTION"));
            itemsTable.addHeaderCell(headerCell("PRICE"));
            itemsTable.addHeaderCell(headerCell("QTY"));
            itemsTable.addHeaderCell(headerCell("TOTAL"));

            int index = 1;
            BigDecimal subTotal = BigDecimal.ZERO;
            boolean alternate = false;

            for (var item : order.getItems()) {
                BigDecimal lineTotal = item.getMenuItem().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                subTotal = subTotal.add(lineTotal);

                Color bgColor = alternate ? ROW_BG : ColorConstants.WHITE;

                itemsTable.addCell(dataCell(String.valueOf(index++), bgColor));
                itemsTable.addCell(dataCell(item.getMenuItem().getName(), bgColor));
                itemsTable.addCell(dataCell(item.getMenuItem().getPrice() + " €", bgColor));
                itemsTable.addCell(dataCell(String.valueOf(item.getQuantity()), bgColor));
                itemsTable.addCell(dataCell(lineTotal + " €", bgColor));

                alternate = !alternate;
            }

            document.add(itemsTable);
            document.add(new Paragraph("\n"));

            // ===== TOTALS =====
            Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{6, 2}))
                    .setWidth(UnitValue.createPercentValue(50))
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);

            totalsTable.addCell(totalLabelCell("SUBTOTAL", TOTAL_BG));
            totalsTable.addCell(totalValueCell(subTotal + " €", TOTAL_BG));

            BigDecimal tax = BigDecimal.ZERO;
            totalsTable.addCell(totalLabelCell("TAX (0%)", TOTAL_BG));
            totalsTable.addCell(totalValueCell(tax + " €", TOTAL_BG));

            BigDecimal total = subTotal.add(tax);
            totalsTable.addCell(totalLabelCell("TOTAL", TOTAL_BG));
            totalsTable.addCell(totalValueCell(total + " €", TOTAL_BG));

            document.add(totalsTable);
            document.add(new Paragraph("\nThank you for your business!").setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Authorized Signature").setTextAlignment(TextAlignment.RIGHT));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации PDF", e);
        }

        return outputStream.toByteArray();
    }

    // ===== CELL HELPERS =====
    private Cell headerCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell dataCell(String text, Color bgColor) {
        return new Cell()
                .add(new Paragraph(text))
                .setBackgroundColor(bgColor)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell totalLabelCell(String text, Color bgColor) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(bgColor)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);
    }

    private Cell totalValueCell(String text, Color bgColor) {
        return new Cell()
                .add(new Paragraph(text))
                .setBackgroundColor(bgColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER);
    }

    private Cell noBorderCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBorder(Border.NO_BORDER);
    }
}
