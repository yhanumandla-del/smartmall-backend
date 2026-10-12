package com.smartmall.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Sends transactional email through the Brevo HTTPS API (no SMTP needed, so it also works on
 * hosts that block SMTP ports, such as Railway's trial plan).
 *
 * If BREVO_API_KEY / MAIL_FROM_EMAIL are not set, the email is not sent. Instead the reset link
 * is written to the server log so the flow can still be demonstrated locally.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final String apiKey;
    private final String fromEmail;
    private final String fromName;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public MailService(
        @Value("${app.mail.brevo-api-key:}") String apiKey,
        @Value("${app.mail.from-email:}") String fromEmail,
        @Value("${app.mail.from-name:SmartMall}") String fromName
    ) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.fromEmail = fromEmail == null ? "" : fromEmail.trim();
        this.fromName = fromName == null || fromName.isBlank() ? "SmartMall" : fromName.trim();
    }

    public void sendPasswordReset(String toEmail, String toName, String link, long expiryMinutes) {
        if (apiKey.isBlank() || fromEmail.isBlank()) {
            log.warn("Email is not configured (set BREVO_API_KEY and MAIL_FROM_EMAIL). Password reset link for {}: {}", toEmail, link);
            return;
        }

        String safeName = HtmlUtils.htmlEscape(toName == null ? "there" : toName);
        String safeLink = HtmlUtils.htmlEscape(link);
        String html = "<div style=\"font-family:Arial,sans-serif;max-width:480px;margin:auto\">"
            + "<h2>Reset your SmartMall password</h2>"
            + "<p>Hi " + safeName + ",</p>"
            + "<p>We received a request to reset your password. Click the button below to choose a new one. "
            + "This link expires in " + expiryMinutes + " minutes and can be used only once.</p>"
            + "<p><a href=\"" + safeLink + "\" style=\"display:inline-block;padding:12px 18px;background:#5148e8;color:#fff;"
            + "text-decoration:none;border-radius:8px;font-weight:bold\">Reset password</a></p>"
            + "<p style=\"color:#667085;font-size:13px\">If you did not request this, you can ignore this email. "
            + "Your password will not change.</p></div>";

        Map<String, Object> body = Map.of(
            "sender", Map.of("name", fromName, "email", fromEmail),
            "to", List.of(Map.of("email", toEmail, "name", toName == null ? toEmail : toName)),
            "subject", "Reset your SmartMall password",
            "htmlContent", html
        );

        // Send in the background so the API response does not wait on the email provider.
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder(URI.create(BREVO_URL))
                    .timeout(Duration.ofSeconds(15))
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() / 100 != 2) {
                    log.error("Password reset email to {} failed: HTTP {} {}", toEmail, response.statusCode(), response.body());
                } else {
                    log.info("Password reset email sent to {}", toEmail);
                }
            } catch (Exception e) {
                log.error("Password reset email to {} failed: {}", toEmail, e.toString());
            }
        });
    }
}
