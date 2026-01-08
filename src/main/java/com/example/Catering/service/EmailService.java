package com.example.Catering.service;

import com.example.Catering.entity.Invoice;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PdfInvoiceService pdfInvoiceService;

    public void sendInvoiceEmail(Invoice invoice) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = вложения разрешены

            helper.setTo(invoice.getOrder().getUser().getEmail());
            helper.setSubject("Your Invoice #" + invoice.getInvoiceNumber());
            helper.setText("Dear " + invoice.getOrder().getUser().getFirstName() + ",\n\n" +
                    "Thank you for your order! Please find your invoice attached below.");
            // Генерация PDF
            byte[] pdfBytes = pdfInvoiceService.generateInvoicePdf(invoice);

            // Прикрепление PDF
            helper.addAttachment("invoice_" + invoice.getInvoiceNumber() + ".pdf", new DataSource() {
                @Override
                public java.io.InputStream getInputStream() {
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
                public java.io.OutputStream getOutputStream() {
                    throw new UnsupportedOperationException("Read-only");
                }
            });

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace(); // Логируем ошибку
        }
    }
}