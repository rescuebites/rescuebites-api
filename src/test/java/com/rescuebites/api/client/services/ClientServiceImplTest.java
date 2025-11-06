package com.rescuebites.api.client.services;

import com.rescuebites.api.client.data.enums.PreferenceType;
import com.rescuebites.api.client.data.models.Client;
import com.rescuebites.api.client.repositories.IClientRepository;
import com.rescuebites.api.client.services.implementations.ClientServiceImpl;
import com.rescuebites.api.exceptions.custom_exceptions.ValidationException;
import com.rescuebites.api.shared.EmailBuilder;
import com.rescuebites.api.shared.Image;
import com.rescuebites.api.shared.facades.interfaces.IImageFacade;
import com.rescuebites.api.users.data.models.User;
import com.rescuebites.api.users.facades.interfaces.IUserFacade;
import com.rescuebites.api.users.services.interfaces.IEmailService;
import com.rescuebites.api.users.services.interfaces.ITokenService;
import com.rescuebites.api.users.services.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private IClientRepository clientRepository;

    @Mock
    private IUserService userService;

    @Mock
    private IUserFacade userFacade;

    @Mock
    private IImageFacade imageFacade;

    @Mock
    private ITokenService tokenService;

    @Mock
    private EmailBuilder emailBuilder;

    @Mock
    private IEmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private UUID clientId;
    private User user;
    private Image image;
    private Client client;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        user = User.builder()
                .userId(UUID.randomUUID())
                .email("user@example.com")
                .enabled(true)
                .build();

        image = Image.builder()
                .publicId("publicId")
                .url("https://image.example.com")
                .build();

        client = Client.builder()
                .clientId(clientId)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.now().minusYears(20))
                .address("123 Street")
                .user(user)
                .image(image)
                .preferences(List.of(PreferenceType.CELIAC))
                .build();
    }

    @Test
    void deleteClient_whenNotConfirmed_throwsValidationException() {
        assertThatThrownBy(() -> clientService.deleteClient(clientId, false))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Debes confirmar la eliminación de tu cuenta antes de continuar.");

        verifyNoInteractions(clientRepository);
        verifyNoInteractions(imageFacade);
        verifyNoInteractions(tokenService);
    }

    @Test
    void deleteClient_whenConfirmed_marksEntitiesAsDeletedAndCleansUp() {
        when(clientRepository.findByClientIdAndDeletedFalse(clientId)).thenReturn(Optional.of(client));

        clientService.deleteClient(clientId, true);

        verify(imageFacade).deleteImage("publicId");
        verify(tokenService).deleteTokensByUser(user);
        verify(clientRepository).save(client);

        assertThat(client.isDeleted()).isTrue();
        assertThat(client.getDeletedAt()).isNotNull();
        assertThat(client.getImage()).isNull();
        assertThat(client.getPreferences()).isEmpty();

        assertThat(user.isDeleted()).isTrue();
        assertThat(user.isEnabled()).isFalse();
        assertThat(user.getDeletedAt()).isNotNull();
    }
}
