package com.backend.demo.service;

import com.backend.demo.util.QrCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class QrService {

    public String generarUrlQr(String token) {
        try {
            byte[] qrBytes = QrCodeGenerator.generateQrCode(token, 300, 300);
            String base64Image = Base64.getEncoder().encodeToString(qrBytes);
            log.debug("QR generado localmente para token={}", token);
            return "data:image/png;base64," + base64Image;
        } catch (Exception e) {
            log.error("Error generando QR para token={}", token, e);
            return "";
        }
    }
}