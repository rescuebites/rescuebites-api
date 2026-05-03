package com.rescuebites.api.shared;

import com.rescuebites.api.users.data.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmailBuilder {

    @Value("${frontend.url}")
    private String frontendUrl;

    public String buildConfirmAccount(User user, UUID confirmationToken) {
        String confirmationLink = frontendUrl + "/auth/activate?userId=" + user.getUserId()
                + "&token=" + confirmationToken;

        return """
            <html>
                <head>
                    <style>
                        a.confirm-button {
                            display: inline-block;
                            margin-top: 25px;
                            padding: 14px 28px;
                            background-color: #77A787;
                            color: white !important;
                            text-decoration: none;
                            border-radius: 6px;
                            font-weight: bold;
                            transition: background-color 0.3s ease;
                        }
                        a.confirm-button:hover {
                            background-color: #7fbf7f\s
                        }
                    </style>
                </head>
                <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #77A787; padding: 40px; text-align: center;">
                    <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
                        <h2 style="color: #77A787; margin-bottom: 20px;">¡Bienvenido/a a RescueBites!</h2>
                        <p style="font-size: 16px; color: #333;"><strong>¡Hola! Es un placer tenerte aquí</strong>,</p>
                        <p style="font-size: 16px; color: #555;">
                            Gracias por registrarte. Para completar tu registro, por favor confirmá tu correo electrónico haciendo clic en el botón de abajo:
                        </p>
                        <a href="%s" class="confirm-button">Confirmar cuenta</a>
                        <p style="margin-top: 25px; font-size: 14px; color: #888;">
                            Si no creaste esta cuenta, podés ignorar este correo de forma segura.
                        </p>
                    </div>
                </body>
            </html>
       \s""".formatted(confirmationLink);
    }

    public String buildResetPassword(String userEmail, UUID newToken) {
        String confirmationLink = frontendUrl + "/api/users/reset-password?token=" + newToken;

        return """
          <html>
            <head>
                <style>
                    a.confirm-button {
                        display: inline-block;
                        margin-top: 25px;
                        padding: 14px 28px;
                        background-color: #77A787;
                        color: white !important;
                        text-decoration: none;
                        border-radius: 6px;
                        font-weight: bold;
                        transition: background-color 0.3s ease;
                    }
                    a.confirm-button:hover {
                        background-color: #7fbf7f\s
                    }
                </style>
            </head>
            <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #77A787; padding: 40px; text-align: center;">
                <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
                    <h2 style="color: #77A787; margin-bottom: 20px;">Restablecer contraseña</h2>
                    <p style="font-size: 16px; color: #333;"><strong>Hola %s</strong>,</p>
                    <p style="font-size: 16px; color: #555;">
                        Recibimos una solicitud para restablecer la contraseña de tu cuenta.\s
                                  Hacé clic en el botón de abajo para continuar:
                    </p>
                    <a href="%s" class="confirm-button">Restablecer contraseña</a>
                    <p style="margin-top: 25px; font-size: 14px; color: #888;">
                        Si no solicitaste esto, podés ignorar este correo de forma segura.
                    </p>
                </div>
            </body>
          </html>
       \s""".formatted(userEmail, confirmationLink);
    }

    public String buildResendConfirmAccount(User user, UUID confirmationToken) {
        String confirmationLink = frontendUrl + "/auth/activate?userId=" + user.getUserId()
                + "&token=" + confirmationToken;

        return """
          <html>
            <head>
                <style>
                    a.confirm-button {
                        display: inline-block;
                        margin-top: 25px;
                        padding: 14px 28px;
                        background-color: #77A787;
                        color: white !important;
                        text-decoration: none;
                        border-radius: 6px;
                        font-weight: bold;
                        transition: background-color 0.3s ease;
                    }
                    a.confirm-button:hover {
                        background-color: #7fbf7f\s
                    }
                </style>
            </head>
            <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #77A787; padding: 40px; text-align: center;">
                <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
                    <h2 style="color: #77A787; margin-bottom: 20px;">Confirmación de cuenta - Nuevo enlace</h2>
                    <p style="font-size: 16px; color: #333;"><strong>Hola %s</strong>,</p>
                    <p style="font-size: 16px; color: #555;">
                        Solicitaste un nuevo enlace de confirmación para tu cuenta.\s
                                  Por favor confirmá tu dirección de correo electrónico haciendo clic en el botón de abajo:
                    </p>
                    <a href="%s" class="confirm-button">Confirmar cuenta</a>
                    <p style="margin-top: 25px; font-size: 14px; color: #888;">
                        Si no creaste esta cuenta, podés ignorar este correo de forma segura.
                    </p>
                </div>
            </body>
          </html>
       \s""".formatted(user.getEmail(), confirmationLink);
    }

    public String buildEmailUpdatedConfirmation(User user, UUID confirmationToken) {
        String profileUrl = frontendUrl + "/auth/activate?userId=" + user.getUserId()
                + "&token=" + confirmationToken;

        // Mostramos el nuevo email al que se está migrando
        String newEmail = (user.getPendingEmail() != null && !user.getPendingEmail().isBlank())
                ? user.getPendingEmail()
                : user.getEmail();

        return """
            <html>
              <head>
                  <style>
                      a.confirm-button {
                          display: inline-block;
                          margin-top: 25px;
                          padding: 14px 28px;
                          background-color: #77A787;
                          color: white !important;
                          text-decoration: none;
                          border-radius: 6px;
                          font-weight: bold;
                          transition: background-color 0.3s ease;
                      }
                      a.confirm-button:hover {
                          background-color: #7fbf7f\s
                      }
                  </style>
              </head>
              <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #77A787; padding: 40px; text-align: center;">
                  <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
                      <h2 style="color: #77A787; margin-bottom: 20px;">Confirmá tu nuevo email</h2>
                      <p style="font-size: 16px; color: #333;"><strong>¡Hola!</strong>,</p>
                      <p style="font-size: 16px; color: #555;">
                          Recibimos una solicitud para actualizar el correo electrónico asociado a tu cuenta de RescueBites.
                      </p>
                      <p style="font-size: 16px; color: #555;">
                          <strong>Nuevo email:</strong> %s
                      </p>
                      <p style="font-size: 15px; color: #555;">
                          Hacé clic en el botón para confirmar el cambio. Una vez confirmado, deberás iniciar sesión con tu nuevo email.
                      </p>
                      <a href="%s" class="confirm-button">Confirmar nuevo email</a>
                      <p style="margin-top: 25px; font-size: 14px; color: #888;">
                          Si no realizaste este cambio, por favor restablece tu contraseña de inmediato desde la aplicación o contáctanos.
                      </p>
                  </div>
              </body>
            </html>
        \s""".formatted(newEmail, profileUrl);
    }
}
