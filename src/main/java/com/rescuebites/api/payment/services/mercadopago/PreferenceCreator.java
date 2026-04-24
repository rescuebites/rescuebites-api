package com.rescuebites.api.payment.services.mercadopago;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.rescuebites.api.exceptions.custom_exceptions.PaymentException;
import com.rescuebites.api.order.data.models.Order;
import com.rescuebites.api.payment.builders.PreferenceBuilder;
import com.rescuebites.api.payment.controllers.responses.PaymentLinkResponse;
import com.rescuebites.api.payment.utils.MercadoPagoConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenceCreator {

    private final PreferenceBuilder preferenceBuilder;

    public PaymentLinkResponse createPaymentPreference(Order order, String frontendBaseUrl) {
        // Normalize frontendBaseUrl
        String normalizedFrontend = null;
        if (frontendBaseUrl != null) {
            normalizedFrontend = frontendBaseUrl.trim();
            if (normalizedFrontend.endsWith("/")) {
                normalizedFrontend = normalizedFrontend.substring(0, normalizedFrontend.length() - 1);
            }
        }

        log.debug("Creating MercadoPago preference for order={} with frontendBaseUrl={}", order.getOrderId(), normalizedFrontend);

        // Validar la URL del frontend. Permitimos orígenes locales (localhost/127.0.0.1)
        // porque son útiles en desarrollo: el navegador del cliente puede acceder
        // directamente al frontend y evitamos pasar por ngrok (que muestra una
        // interstitial en cuentas free). Para orígenes públicos, exigimos HTTPS y
        // comprobamos reachability desde el servidor.
        if (normalizedFrontend != null) {
            boolean frontendIsLocal = normalizedFrontend.startsWith("http://localhost") || normalizedFrontend.startsWith("http://127.0.0.1") || normalizedFrontend.startsWith("https://localhost") || normalizedFrontend.startsWith("https://127.0.0.1");
            boolean frontendIsHttps = normalizedFrontend.startsWith("https://");

            // Si no es local, exigir HTTPS público
            if (!frontendIsLocal && !frontendIsHttps) {
                log.warn("Ignoring frontendBaseUrl '{}' because it's not a public HTTPS URL", normalizedFrontend);
                normalizedFrontend = null;
            } else if (!frontendIsLocal && !isFrontendUrlReachable(normalizedFrontend)) {
                // Si no es local y no responde desde el servidor, descartarla
                log.warn("Ignoring frontendBaseUrl '{}' because it appears unreachable; falling back to server back_urls", normalizedFrontend);
                normalizedFrontend = null;
            } else {
                // Aceptamos orígenes locales sin comprobación remota
                log.debug("Using frontendBaseUrl '{}' for back_urls", normalizedFrontend);
            }
        }

        // Construir request fuera del lock (no depende del token global)
        PreferenceRequest preferenceRequest = preferenceBuilder.buildPreferenceRequest(order, normalizedFrontend);

        // Loguear backUrls para depuración de integraciones con Mercado Pago
        log.debug("Built PreferenceRequest backUrls={}, externalReference={}", preferenceRequest.getBackUrls(), preferenceRequest.getExternalReference());

        synchronized (MercadoPagoConfigUtil.class) {
            return MercadoPagoConfigUtil.executeWithCommerceToken(
                    order.getCommerce().getMercadoPagoAccessToken(),
                    () -> {
                        try {
                            PreferenceClient client = new PreferenceClient();
                            Preference preference = client.create(preferenceRequest);

                            return new PaymentLinkResponse(
                                    preference.getId(),
                                    preference.getInitPoint(),
                                    preference.getSandboxInitPoint()
                            );
                        } catch (MPApiException e) {
                            String errorMessage = e.getApiResponse() != null ?
                                    e.getApiResponse().getContent() : e.getMessage();
                            throw new PaymentException("Error al crear preferencia de pago: " + errorMessage);
                        } catch (MPException e) {
                            throw new PaymentException("Error al crear preferencia de pago: " + e.getMessage());
                        }
                    }
            );
        }
    }

    private boolean isFrontendUrlReachable(String urlString) {
        try {
            URL url = new URL(urlString);
            // Try HEAD first
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setInstanceFollowRedirects(true);
            int code = conn.getResponseCode();
            if (code >= 200 && code < 400) {
                return true;
            }
        } catch (IOException e) {
            log.debug("HEAD check failed for frontend URL {}: {}", urlString, e.getMessage());
            // fallback to GET
            try {
                URL url2 = new URL(urlString);
                HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
                conn2.setRequestMethod("GET");
                conn2.setConnectTimeout(3000);
                conn2.setReadTimeout(3000);
                conn2.setInstanceFollowRedirects(true);
                int code2 = conn2.getResponseCode();
                return code2 >= 200 && code2 < 400;
            } catch (IOException ex) {
                log.debug("GET fallback failed for frontend URL {}: {}", urlString, ex.getMessage());
                return false;
            }
        }

        return false;
    }
}
