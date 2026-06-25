package com.transportation.presenca.service;

import com.transportation.presenca.dto.AlunoDTO;
import com.transportation.presenca.model.Aluno;
import com.transportation.presenca.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {
    
    @Mock
    private AlunoRepository alunoRepository;
    
    @InjectMocks
    private AlunoService alunoService;

    private static final UUID ALUNO_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ALUNO_INEXISTENTE_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
    
    @Test
    public void testObterAluno() {
        // Arrange
        Aluno aluno = new Aluno();
        aluno.setId(ALUNO_ID);
        aluno.setMatricula("2024001");
        aluno.setNome("João Silva");
        
        when(alunoRepository.findById(ALUNO_ID)).thenReturn(Optional.of(aluno));
        
        // Act
        AlunoDTO dto = alunoService.obterAluno(ALUNO_ID);
        
        // Assert
        assertEquals(ALUNO_ID, dto.getId());
        assertEquals("2024001", dto.getMatricula());
        assertEquals("João Silva", dto.getNome());
    }
    
    @Test
    public void testObterAlunoNotFound() {
        // Arrange
        when(alunoRepository.findById(ALUNO_INEXISTENTE_ID)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            alunoService.obterAluno(ALUNO_INEXISTENTE_ID);
        });
    }
}
