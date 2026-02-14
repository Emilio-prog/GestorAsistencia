package com.dam.gestorasistencia.service;

import com.dam.gestorasistencia.model.Usuario;
import com.dam.gestorasistencia.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PasswordRecoveryService passwordRecoveryService;

    @Test
    @DisplayName("Debe devolver null cuando el email no existe")
    void recoverPasswordEmailNoExiste() {
        when(usuarioRepository.findByEmail("inexistente@ies.edu")).thenReturn(Optional.empty());

        String tempPassword = passwordRecoveryService.recoverPassword("inexistente@ies.edu");

        assertNull(tempPassword);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe generar y guardar contraseña temporal cuando el email existe")
    void recoverPasswordEmailExiste() {
        Usuario usuario = new Usuario();
        usuario.setEmail("profe@ies.edu");
        usuario.setPassword("original123");

        when(usuarioRepository.findByEmail("profe@ies.edu")).thenReturn(Optional.of(usuario));

        String tempPassword = passwordRecoveryService.recoverPassword("profe@ies.edu");

        assertNotNull(tempPassword);
        assertEquals(10, tempPassword.length());
        assertEquals(tempPassword, usuario.getPassword());
        verify(usuarioRepository).save(usuario);
    }
}
