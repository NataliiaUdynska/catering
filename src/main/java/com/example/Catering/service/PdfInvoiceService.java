package com.example.Catering.service;

import com.example.Catering.entity.Invoice;
import com.example.Catering.entity.Order;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Service
public class PdfInvoiceService {

    // Данные компании
    private static final String COMPANY_NAME = "Catering GmbH";
    private static final String COMPANY_ADDRESS =
            "Neuhauser Str. 25\n" +
                    "80331 München\n" +
                    "Germany";

    private static final String BANK_DETAILS =
            "Bank: Commerzbank München\n" +
                    "IBAN: DE44 7006 0035 0506 0287 01\n" +
                    "BIC: COOAAAFFXXX\n" +
                    "Account holder: Catering GmbH";

    // Генерация инвойса
    public byte[] generateInvoicePdf(Invoice invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            Order order = invoice.getOrder();

            // Инфо компании
            Paragraph header = new Paragraph(COMPANY_NAME)
                    .setBold()
                    .setFontSize(20);
            doc.add(header);
            doc.add(new Paragraph(COMPANY_ADDRESS).setFontSize(10));
            doc.add(new Paragraph("\n"));

            // Инфо о клиенте
            Table topTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            String fullName =
                    (order.getUser().getFirstName() != null ? order.getUser().getFirstName() : "") + " " +
                            (order.getUser().getLastName() != null ? order.getUser().getLastName() : "");

            String address = order.getDeliveryAddress() != null
                    ? order.getDeliveryAddress()
                    : "—";

            // Левая колонка данные клиента
            topTable.addCell(noBorder(
                    fullName + "\n" +
                            address + "\n" +
                            "Germany"
            ));

            // Правая колонка инвойса
            topTable.addCell(noBorder(
                    "Invoice date: " +
                            invoice.getIssuedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n" +
                            "Invoice no.: " + invoice.getInvoiceNumber() + "\n" +
                            "Order ID: " + order.getId()
            ).setTextAlignment(TextAlignment.RIGHT));

            doc.add(topTable);

            // Инфо о мероприятии
            String eventDateTime = order.getEventDateTime() != null
                    ? order.getEventDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                    : "—";

            String guests = order.getNumberOfGuests() != null
                    ? String.valueOf(order.getNumberOfGuests())
                    : "—";

            String comment = (order.getComment() != null && !order.getComment().isBlank())
                    ? order.getComment()
                    : "—";

            // Заголовки жирные, а данные обычные
            Paragraph eventInfo = new Paragraph()
                    .add(new Text("Event Date & Time: ").setBold())
                    .add(new Text(eventDateTime + "\n"))
                    .add(new Text("Number of Guests: ").setBold())
                    .add(new Text(guests + "\n"))
                    .add(new Text("Comments: ").setBold())
                    .add(new Text(comment))
                    .setFontSize(11)
                    .setMarginTop(10)
                    .setMarginBottom(10);

            doc.add(eventInfo);

            //Invoice
            doc.add(new Paragraph("Invoice").setBold().setFontSize(14));
            doc.add(new Paragraph("\n"));

            // Таблица заказа
            Table items = new Table(UnitValue.createPercentArray(new float[]{1, 5, 2, 2.5f}))
                    .setWidth(UnitValue.createPercentValue(100));

            items.addHeaderCell(th("No."));
            items.addHeaderCell(th("Item"));
            items.addHeaderCell(th("Price"));
            items.addHeaderCell(th("Total"));

            int pos = 1;
            BigDecimal netTotal = BigDecimal.ZERO;

            for (var item : order.getItems()) {
                BigDecimal price = item.getMenuItem().getPrice();
                BigDecimal lineTotal =
                        price.multiply(BigDecimal.valueOf(item.getQuantity()));

                netTotal = netTotal.add(lineTotal);

                items.addCell(td(String.valueOf(pos++)));
                items.addCell(td(
                        item.getMenuItem().getName() +
                                " — " + item.getQuantity() + " pcs."
                ));
                items.addCell(td(price.setScale(2, RoundingMode.HALF_UP) + " €"));
                items.addCell(td(lineTotal.setScale(2, RoundingMode.HALF_UP) + " €"));
            }

            doc.add(items);
            doc.add(new Paragraph("\n"));

            // Итоги блок
            BigDecimal vat = netTotal.multiply(BigDecimal.valueOf(0.07)); // 7% VAT
            BigDecimal gross = netTotal.add(vat);

            Table totals = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .setWidth(UnitValue.createPercentValue(60))
                    .setMarginLeft(200);

            totals.addCell(totalLabel("Subtotal"));
            totals.addCell(totalValue(netTotal.setScale(2, RoundingMode.HALF_UP) + " €"));

            totals.addCell(totalLabel("VAT 7%"));
            totals.addCell(totalValue(vat.setScale(2, RoundingMode.HALF_UP) + " €"));

            totals.addCell(totalLabelBold("Total Amount Due"));
            totals.addCell(totalValueBold(gross.setScale(2, RoundingMode.HALF_UP) + " €"));

            doc.add(totals);
            doc.add(new Paragraph("\n"));

            // Нижняя часть страницы
            doc.add(new Paragraph(
                    "Thank you for your order.\n" +
                            "Please transfer the total amount within 7 days."
            ).setFontSize(10));

            doc.add(new Paragraph("\nBank details:")
                    .setBold()
                    .setFontSize(10));
            doc.add(new Paragraph(BANK_DETAILS).setFontSize(10));

            doc.add(new Paragraph("\n" + COMPANY_NAME)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30));

        } catch (Exception e) {
            throw new RuntimeException("Error during PDF generation", e);
        }

        return out.toByteArray();
    }


    // Заголовок таблицы
    private Cell th(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
    }

    // Обычная ячейка таблицы
    private Cell td(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setKeepTogether(true);
    }

    // Название строки итога
    private Cell totalLabel(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    // Значение итога
    private Cell totalValue(String text) {
        return new Cell()
                .add(new Paragraph(text).setKeepTogether(true))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    // Жирный заголовок итога
    private Cell totalLabelBold(String text) {
        return totalLabel(text).setBold();
    }

    // Жирное значение итога
    private Cell totalValueBold(String text) {
        return totalValue(text).setBold();
    }

    // Ячейка без рамок
    private Cell noBorder(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setBorder(Border.NO_BORDER);
    }
}