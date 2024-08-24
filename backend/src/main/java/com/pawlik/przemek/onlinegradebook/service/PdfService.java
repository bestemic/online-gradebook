package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.model.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PdfService {

    public byte[] generateFileWithPasswords(Map<User, String> userPasswordMap) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("New Passwords", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            table.addCell(new PdfPCell(new Phrase("First Name", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Last Name", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Email", headerFont)));
            table.addCell(new PdfPCell(new Phrase("New Password", headerFont)));

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            for (Map.Entry<User, String> entry : userPasswordMap.entrySet()) {
                User user = entry.getKey();
                String password = entry.getValue();

                table.addCell(new PdfPCell(new Phrase(user.getFirstName(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(user.getLastName(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(user.getEmail(), cellFont)));
                table.addCell(new PdfPCell(new Phrase(password, cellFont)));
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
