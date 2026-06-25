package com.transportation.presenca.service;

import com.transportation.presenca.dto.PresencaDTO;
import com.transportation.presenca.model.Presenca;
import com.transportation.presenca.model.StatusPresenca;
import com.transportation.presenca.repository.AlunoRepository;
import com.transportation.presenca.repository.PresencaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PresencaServiceTest {
    
    @Mock
    private PresencaRepository presencaRepository;
    
    @Mock
    private AlunoRepository alunoRepository;
    
    @InjectMocks
    private PresencaService presencaService;

    private static final UUID PRESENCA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ALUNO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID PRESENCA_INEXISTENTE_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
    
    @BeforeEach
    public void setUp() {
        // Inicialização se necessário
    }
    
    @Test
    public void testObterPresenca() {
        // Arrange
        Presenca presenca = new Presenca();
        presenca.setId(PRESENCA_ID);
        presenca.setAlunoId(ALUNO_ID); 
        presenca.setStatus(StatusPresenca.PRESENTE);
        
        when(presencaRepository.findById(PRESENCA_ID)).thenReturn(Optional.of(presenca));
        
        // Act
        PresencaDTO dto = presencaService.obterPresenca(PRESENCA_ID);
        
        // Assert
        assertEquals(PRESENCA_ID, dto.getId());
        assertEquals(StatusPresenca.PRESENTE, dto.getStatus());
    }
    
    @Test
    public void testObterPresencaNotFound() {
        // Arrange
        when(presencaRepository.findById(PRESENCA_INEXISTENTE_ID)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            presencaService.obterPresenca(PRESENCA_INEXISTENTE_ID);
        });
    }
}
