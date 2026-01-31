package com.net.ticketservice.services;

import com.net.ticketservice.entities.Ticket;
import com.net.ticketservice.entities.TicketQR;
import com.net.ticketservice.repository.TicketQRRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final TicketQRRepository ticketQRRepository;

    public TicketQR generateQRCodeForTicket(Ticket ticket) {
        try {
            String qrContent = generateQRContent(ticket);
            String qrCodeImage = generateQRCodeImage(qrContent);

            TicketQR ticketQR = new TicketQR();
            ticketQR.setTicket(ticket);
            ticketQR.setQrCode(qrCodeImage);
            ticketQR.setGeneratedAt(LocalDateTime.now());

            return ticketQRRepository.save(ticketQR);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    private String generateQRContent(Ticket ticket) {
        return String.format("TICKET-%d-%s-%s",
                ticket.getId(),
                ticket.getEventId(),
                UUID.randomUUID().toString());
    }

    private String generateQRCodeImage(String content) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] qrCodeBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }

    public boolean validateQRCode(String qrCode) {
        return ticketQRRepository.findByQrCode(qrCode)
                .map(ticketQR -> !ticketQR.isScanned())
                .orElse(false);
    }

    public void markQRCodeAsScanned(String qrCode) {
        TicketQR ticketQR = ticketQRRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("QR Code not found"));

        if (ticketQR.isScanned()) {
            throw new RuntimeException("QR Code already scanned");
        }

        ticketQR.setScanned(true);
        ticketQR.setScannedAt(LocalDateTime.now());
        ticketQRRepository.save(ticketQR);
    }
}
