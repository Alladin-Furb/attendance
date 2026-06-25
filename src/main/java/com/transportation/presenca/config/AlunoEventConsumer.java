package com.transportation.presenca.config;

import com.transportation.presenca.model.Aluno;
import com.transportation.presenca.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlunoEventConsumer {

    private final AlunoRepository alunoRepository;

    @RabbitListener(queues = RabbitMQConfig.ALUNO_CADASTRADO_QUEUE)
    public void consumirAlunoCadastrado(Aluno aluno) {
        log.info("Aluno cadastrado recebido: {} (profileId={})", aluno.getNome(), aluno.getId());
        salvarOuAtualizar(aluno);
    }

    @RabbitListener(queues = RabbitMQConfig.ALUNO_ATUALIZADO_QUEUE)
    public void consumirAlunoAtualizado(Aluno aluno) {
        log.info("Aluno atualizado recebido: {} (profileId={})", aluno.getNome(), aluno.getId());
        salvarOuAtualizar(aluno);
    }

    private void salvarOuAtualizar(Aluno recebido) {
        UUID profileId = recebido.getId();
        Aluno destino = alunoRepository.findByExternalId(profileId)
                .or(() -> alunoRepository.findByMatricula(recebido.getMatricula()))
                .or(() -> recebido.getEmail() == null
                        ? java.util.Optional.empty()
                        : alunoRepository.findByEmail(recebido.getEmail()))
                .orElseGet(Aluno::new);

        destino.setExternalId(profileId);
        destino.setMatricula(recebido.getMatricula());
        destino.setCpf(recebido.getCpf());
        destino.setNome(recebido.getNome());
        destino.setEmail(recebido.getEmail());
        destino.setTelefone(recebido.getTelefone());
        destino.setRotaTransporte(recebido.getRotaTransporte());
        destino.setCursoId(recebido.getCursoId());
        destino.setNomeCurso(recebido.getNomeCurso());
        destino.setFaculdade(recebido.getFaculdade());
        destino.setAtivo(true);

        alunoRepository.save(destino);
        log.info("Aluno sincronizado no Attendance: profileId={}", profileId);
    }
}
