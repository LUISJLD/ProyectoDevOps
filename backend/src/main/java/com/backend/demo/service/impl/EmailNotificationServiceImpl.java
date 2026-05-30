package com.backend.demo.service.impl;

import com.backend.demo.model.entity.Event;
import com.backend.demo.model.entity.Inscripcion;
import com.backend.demo.service.IEmailNotificationService;
import com.backend.demo.util.QrCodeGenerator;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements IEmailNotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.from:no-reply@example.com}")
    private String mailFrom;

    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    @Override
    public void sendInscripcionConfirmation(@NotNull Inscripcion inscripcion) {
        log.info("Enviando confirmación de inscripción a {}", inscripcion.getUsuario().getEmail());
        try {
            byte[] qrBytes = QrCodeGenerator.generateQrCode(inscripcion.getQrToken(), 300, 300);

            Context context = new Context();
            context.setVariable("nombre", inscripcion.getUsuario().getNombre());
            context.setVariable("eventoNombre", inscripcion.getEvento().getNombre());
            context.setVariable("fecha", inscripcion.getEvento().getFecha().format(DateTimeFormatter.ISO_DATE));
            context.setVariable("hora", inscripcion.getEvento().getHora().format(DateTimeFormatter.ISO_TIME));
            context.setVariable("ubicacion", inscripcion.getEvento().getUbicacion());

            String body = templateEngine.process("email-inscripcion", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(mailFrom);
            helper.setTo(inscripcion.getUsuario().getEmail());
            helper.setSubject("Confirmación de inscripción al evento: " + inscripcion.getEvento().getNombre());
            helper.setText(body, true);
            helper.addInline("qrCode", new ByteArrayResource(qrBytes), "image/png");

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Error enviando email de confirmación a {}", inscripcion.getUsuario().getEmail(), ex);
            throw new RuntimeException("Error enviando correo, se reintentará", ex);
        }
    }

    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @Override
    public void sendCheckinConfirmation(@NotNull Inscripcion inscripcion) {
        log.info("Enviando confirmación de checkin a {}", inscripcion.getUsuario().getEmail());
        try {
            Context context = new Context();
            context.setVariable("nombre", inscripcion.getUsuario().getNombre());
            context.setVariable("eventoNombre", inscripcion.getEvento().getNombre());
            context.setVariable("fecha", inscripcion.getEvento().getFecha().format(DateTimeFormatter.ISO_DATE));
            context.setVariable("hora", inscripcion.getEvento().getHora().format(DateTimeFormatter.ISO_TIME));

            String body = templateEngine.process("email-checkin", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(mailFrom);
            helper.setTo(inscripcion.getUsuario().getEmail());
            helper.setSubject("Check-in confirmado para " + inscripcion.getEvento().getNombre());
            helper.setText(body, true);

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Error enviando email de checkin a {}", inscripcion.getUsuario().getEmail(), ex);
            throw new RuntimeException("Error enviando correo, se reintentará", ex);
        }
    }

    @Async
    @Override
    public void sendEventUpdateNotifications(@NotNull Event event, List<Inscripcion> inscripciones) {
        if (inscripciones == null || inscripciones.isEmpty()) return;
        
        for (Inscripcion inscripcion : inscripciones) {
            sendUpdateSingle(event, inscripcion);
        }
    }

    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void sendUpdateSingle(Event event, Inscripcion inscripcion) {
        try {
            Context context = new Context();
            context.setVariable("nombre", inscripcion.getUsuario().getNombre());
            context.setVariable("eventoNombre", event.getNombre());
            context.setVariable("fecha", event.getFecha().format(DateTimeFormatter.ISO_DATE));
            context.setVariable("hora", event.getHora().format(DateTimeFormatter.ISO_TIME));
            context.setVariable("ubicacion", event.getUbicacion());

            String body = templateEngine.process("email-update", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(mailFrom);
            helper.setTo(inscripcion.getUsuario().getEmail());
            helper.setSubject("Actualización importante del evento: " + event.getNombre());
            helper.setText(body, true);

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Error enviando email de update a {}", inscripcion.getUsuario().getEmail(), ex);
            throw new RuntimeException("Error enviando correo", ex);
        }
    }

    @Async
    @Override
    public void sendEventReminders(@NotNull List<Inscripcion> inscripciones) {
        if (inscripciones == null || inscripciones.isEmpty()) return;
        
        for (Inscripcion inscripcion : inscripciones) {
            sendReminderSingle(inscripcion);
        }
    }

    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void sendReminderSingle(Inscripcion inscripcion) {
        try {
            Context context = new Context();
            context.setVariable("nombre", inscripcion.getUsuario().getNombre());
            context.setVariable("eventoNombre", inscripcion.getEvento().getNombre());
            context.setVariable("fecha", inscripcion.getEvento().getFecha().format(DateTimeFormatter.ISO_DATE));
            context.setVariable("hora", inscripcion.getEvento().getHora().format(DateTimeFormatter.ISO_TIME));
            context.setVariable("ubicacion", inscripcion.getEvento().getUbicacion());

            String body = templateEngine.process("email-reminder", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(mailFrom);
            helper.setTo(inscripcion.getUsuario().getEmail());
            helper.setSubject("Recordatorio: tu evento es en 24 horas");
            helper.setText(body, true);

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Error enviando email de reminder a {}", inscripcion.getUsuario().getEmail(), ex);
            throw new RuntimeException("Error enviando correo", ex);
        }
    }
}
