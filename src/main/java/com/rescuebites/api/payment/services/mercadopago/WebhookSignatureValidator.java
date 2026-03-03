package com.rescuebites.api.payment.services.mercadopago;

import com.rescuebites.api.commerce.data.projections.CommerceWebhookSecretProjection;
import com.rescuebites.api.commerce.repositories.ICommerceRepository;
import com.rescuebites.api.exceptions.custom_exceptions.IgnorableWebhookException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Valida la firma HMAC-SHA256 de las notificaciones webhook de Mercado Pago.
 * Verifica que la notificación sea auténtica usando el webhook secret del comercio.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookSignatureValidator {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Pattern TS_PATTERN = Pattern.compile("ts=(\\d+)");
    private static final Pattern V1_PATTERN = Pattern.compile("v1=([a-fA-F0-9]+)");

    private final ICommerceRepository commerceRepository;

    @Value("${mercadopago.sandbox:false}")
    private boolean sandbox;

    /**
     * Valida la firma del webhook. En modo sandbox, si los headers no están presentes
     * se ignora la validación. En producción, se rechaza si faltan los headers.
     */
    public void validateSignature(String xSignature, String xRequestId, String dataId) {
        if (xSignature == null || xSignature.isBlank()) {
            if (sandbox) {
                log.debug("Modo sandbox: se omite validación de firma (header x-signature ausente)");
                return;
            }
            throw new IgnorableWebhookException("Header x-signature requerido para validar webhook");
        }

        String ts = extractValue(xSignature, TS_PATTERN);
        String v1 = extractValue(xSignature, V1_PATTERN);

        if (ts == null || v1 == null) {
            throw new IgnorableWebhookException("Header x-signature con formato inválido");
        }

        // Construir el manifest según documentación de MP: "id:{dataId};request-id:{xRequestId};ts:{ts};"
        String manifest = buildManifest(dataId, xRequestId, ts);

        // Buscar todos los comercios con webhook secret configurado y validar contra cada uno
        List<CommerceWebhookSecretProjection> commerceSecrets = commerceRepository.findAllWithWebhookSecret();

        boolean signatureValid = commerceSecrets.stream()
                .anyMatch(projection -> verifyHmac(manifest, projection.getMercadoPagoWebhookSecret(), v1));

        if (!signatureValid) {
            if (sandbox) {
                log.warn("Modo sandbox: firma de webhook inválida, se permite igualmente. dataId={}", dataId);
                return;
            }
            log.warn("Firma de webhook inválida. dataId={}, xRequestId={}", dataId, xRequestId);
            throw new IgnorableWebhookException("Firma de webhook inválida");
        }
    }

    private String buildManifest(String dataId, String xRequestId, String ts) {
        return "id:" + (dataId != null ? dataId : "") + ";" +
                "request-id:" + (xRequestId != null ? xRequestId : "") + ";" +
                "ts:" + ts + ";";
    }

    private boolean verifyHmac(String manifest, String secret, String expectedSignature) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] computedHash = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            byte[] expectedBytes = HexFormat.of().parseHex(expectedSignature.toLowerCase(Locale.ROOT));

            return MessageDigest.isEqual(computedHash, expectedBytes);
        } catch (Exception e) {
            log.error("Error al calcular HMAC: {}", e.getMessage());
            return false;
        }
    }

    private String extractValue(String header, Pattern pattern) {
        Matcher matcher = pattern.matcher(header);
        return matcher.find() ? matcher.group(1) : null;
    }
}
