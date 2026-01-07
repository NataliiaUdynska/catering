package com.example.Catering.service;

import com.example.Catering.entity.Invoice;
import com.example.Catering.entity.Order;
import com.example.Catering.entity.OrderItem;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;


@Service
public class InvoiceService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PdfInvoiceService pdfInvoiceService;


    /** Генерация PDF */
    public byte[] generateInvoicePdf(Invoice invoice) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            // Заголовок
            document.add(new Paragraph("СЧЁТ-ФАКТУРА")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE));

            document.add(new Paragraph("Номер счёта: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Дата: " + invoice.getIssuedAt()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
            document.add(new Paragraph("Общая сумма: " + invoice.getTotalAmount() + " руб."));
            document.add(new Paragraph("\n"));

            // Клиент
            Order order = invoice.getOrder();
            document.add(new Paragraph("Клиент: " +
                    order.getUser().getFirstName() + " " + order.getUser().getLastName()));
            document.add(new Paragraph("Email: " + order.getUser().getEmail()));
            document.add(new Paragraph("Дата мероприятия: " +
                    order.getEventDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
            document.add(new Paragraph("Гостей: " + order.getNumberOfGuests()));
            document.add(new Paragraph("\n"));

            // Таблица
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1}))
                    .setWidth(100);

            table.addHeaderCell("Блюдо");
            table.addHeaderCell("Кол-во");
            table.addHeaderCell("Цена");

            for (OrderItem item : order.getItems()) {
                table.addCell(item.getMenuItem().getName());
                table.addCell(String.valueOf(item.getQuantity()));

                BigDecimal price = item.getMenuItem().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                table.addCell(price + " евро.");
            }

            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("С уважением,\nКоманда кейтеринга."));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации PDF", e);
        }

        return outputStream.toByteArray();
    }


    /** Отправка email со счётом */
    public void sendInvoiceEmail(Invoice invoice) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(invoice.getOrder().getUser().getEmail());
        helper.setSubject("Ваш счёт-фактура #" + invoice.getInvoiceNumber());
        helper.setText(
                "Уважаемый(ая) " + invoice.getOrder().getUser().getFirstName() + ",\n\n" +
                        "Спасибо за ваш заказ! Во вложении находится счёт-фактура."
        );

        byte[] pdfBytes = pdfInvoiceService.generateInvoicePdf(invoice);

        // Прикрепление PDF
        helper.addAttachment(
                "invoice_" + invoice.getInvoiceNumber() + ".pdf",
                new DataSource() {

                    @Override
                    public ByteArrayInputStream getInputStream() {
                        return new ByteArrayInputStream(pdfBytes);
                    }

                    @Override
                    public String getName() {
                        return "invoice_" + invoice.getInvoiceNumber() + ".pdf";
                    }

                    @Override
                    public String getContentType() {
                        return "application/pdf";
                    }

                    @Override
                    public java.io.OutputStream getOutputStream() throws IOException {
                        throw new IOException("Read-only");
                    }
                }
        );

        mailSender.send(message);
    }
}
