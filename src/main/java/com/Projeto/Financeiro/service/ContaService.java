
package com.Projeto.Financeiro.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.Projeto.Financeiro.dto.ContaRequest;
import com.Projeto.Financeiro.dto.ContaResponse;
import com.Projeto.Financeiro.model.Conta;
import com.Projeto.Financeiro.model.Usuario;
import com.Projeto.Financeiro.repository.ContaRepository;

@Service
@Transactional
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    // Cadastrar conta
    public ContaResponse cadastrar(ContaRequest dados, Usuario usuario) {

        Conta conta = new Conta(
            dados.nome(),
            dados.tipo(),
            dados.saldoInicial(),
            usuario
        );

        Conta salva = contaRepository.save(conta);

        return converterParaResponse(salva);
    }

    // Listar contas de um usuário
    @Transactional(readOnly = true)
    public List<ContaResponse> listarPorUsuario(long usuarioId) {

        return contaRepository.findAllByUsuario_Id(usuarioId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    // Buscar conta por ID
    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id, long usuarioId) {

        Conta conta = buscarEntidadePorId(id, usuarioId);

        return converterParaResponse(conta);
    }

    // Atualizar conta
    public ContaResponse atualizar(
            Long id,
            ContaRequest dados,
            long usuarioId) {

        Conta conta = buscarEntidadePorId(id, usuarioId);

        conta.setNome(dados.nome());
        conta.setTipo(dados.tipo());
        conta.setSaldoInicial(dados.saldoInicial());

        Conta atualizada = contaRepository.save(conta);

        return converterParaResponse(atualizada);
    }

    // Buscar entidade verificando o proprietário
    private Conta buscarEntidadePorId(Long id, long usuarioId) {

        return contaRepository
                .findByIdAndUsuario_Id(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Conta não encontrada"
                ));
    }

    // Converter entidade para DTO
    private ContaResponse converterParaResponse(Conta conta) {

        return new ContaResponse(
            conta.getId(),
            conta.getNome(),
            conta.getTipo(),
            conta.getSaldoInicial(),
            conta.isAtiva()
        );
    }
}
