package com.net.ticketservice.web;

import com.net.ticketservice.services.QRCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@Tag(name = "QR Codes", description = "QR code management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @GetMapping("/validate")
    @PreAuthorize("hasAnyAuthority('USER', 'ORGANIZER', 'ADMIN')")
    @Operation(summary = "Validate a QR code (USER, ORGANIZER, ADMIN)")
    public ResponseEntity<Boolean> validateQRCode(@RequestParam String qrCode) {
        boolean isValid = qrCodeService.validateQRCode(qrCode);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/scan")
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Mark QR code as scanned (ORGANIZER, ADMIN)")
    public ResponseEntity<Void> scanQRCode(@RequestParam String qrCode) {
        qrCodeService.markQRCodeAsScanned(qrCode);
        return ResponseEntity.ok().build();
    }
}
