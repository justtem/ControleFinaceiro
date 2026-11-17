import React, { useState } from 'react';
import { Shield, Download, Trash2, UserCheck } from 'lucide-react';
import { api, authStorage } from '../services/api';
import { formatDateTime } from '../utils/formatters';

export default function PerfilLgpdView({ usuario, onUserUpdated, onAccountDeleted, notify }) {
  const [nome, setNome] = useState(usuario?.nome || '');
  const [email, setEmail] = useState(usuario?.email || '');
  const [senha, setSenha] = useState('');
  const [salvando, setSalvando] = useState(false);
  const [confirmacaoExclusao, setConfirmacaoExclusao] = useState('');

  const handleAtualizarPerfil = async (e) => {
    e.preventDefault();
    setSalvando(true);
    try {
      const atualizado = await api.usuarios.atualizar({
        nome: nome.trim(),
        email: email.trim(),
        senha,
        aceitouTermos: true,
      });
      setSenha('');
      notify('Dados atualizados.', 'success');
      onUserUpdated(atualizado);
    } catch (err) {
      notify(err.message, 'error');
    } finally {
      setSalvando(false);
    }
  };

  const handleExportarPortabilidade = async () => {
    try {
      const pacote = await api.usuarios.exportarLgpd();
      const blob = new Blob([JSON.stringify(pacote, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `shares-finance-dados-${usuario.id}.json`;
      a.click();
      URL.revokeObjectURL(url);
      notify('Arquivo JSON exportado.', 'success');
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  const handleExcluirContaDefinitivamente = async () => {
    if (confirmacaoExclusao !== 'EXCLUIR') {
      notify('Digite EXCLUIR para confirmar.', 'error');
      return;
    }
    try {
      await api.usuarios.excluirConta();
      authStorage.clearToken();
      notify('Conta encerrada permanentemente.', 'success');
      onAccountDeleted();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      <h1 className="section-title">Privacidade</h1>

      {/* Banner Superior Discreto de Cadastro e Consentimento */}
      <div
        className="card-cloud"
        style={{
          padding: '20px 28px',
          display: 'flex',
          flexWrap: 'wrap',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: '20px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div
            style={{
              width: '40px',
              height: '40px',
              borderRadius: '99px',
              backgroundColor: 'var(--surface-porcelain)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <Shield size={20} color="var(--color-signal-violet)" strokeWidth={1.85} />
          </div>
          <div>
            <div style={{ fontWeight: 700, fontSize: '15px' }}>Consentimento Ativo</div>
            <div style={{ fontSize: '13px', color: 'var(--color-smoke)' }}>
              Dados isolados por titular
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: '28px' }}>
          <div>
            <span className="eyebrow">CADASTRO</span>
            <div className="tabular" style={{ fontWeight: 700, fontSize: '15px', marginTop: '2px' }}>
              {formatDateTime(usuario?.dataCadastro)}
            </div>
          </div>

          <div>
            <span className="eyebrow">ACEITE DOS TERMOS</span>
            <div
              className="tabular"
              style={{
                fontWeight: 700,
                fontSize: '15px',
                marginTop: '2px',
                color: 'var(--color-signal-violet)',
              }}
            >
              {formatDateTime(usuario?.dataAceiteTermos)}
            </div>
          </div>

          <span className="badge-shares badge-active">
            Versão {usuario?.versaoTermos || '1.0'}
          </span>
        </div>
      </div>

      {/* Grade Limpa: Formulário à Esquerda + 2 Cards de Ação Rápida à Direita */}
      <div className="grid-2" style={{ alignItems: 'start' }}>
        <form
          onSubmit={handleAtualizarPerfil}
          className="card-cloud"
          style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <UserCheck size={20} color="var(--color-signal-violet)" strokeWidth={1.85} />
            <h2 className="card-title">Dados Pessoais</h2>
          </div>

          <div>
            <label className="label-shares">Nome</label>
            <input
              type="text"
              required
              className="input-shares"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
            />
          </div>

          <div>
            <label className="label-shares">E-mail</label>
            <input
              type="email"
              required
              className="input-shares"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div>
            <label className="label-shares">Senha (nova ou atual para confirmar)</label>
            <input
              type="password"
              required
              minLength={8}
              maxLength={100}
              className="input-shares"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              placeholder="Mínimo de 8 caracteres (letras e números)"
            />
          </div>

          <div style={{ paddingTop: '4px' }}>
            <button type="submit" disabled={salvando} className="btn-primary">
              {salvando ? 'Salvando...' : 'Salvar Alterações'}
            </button>
          </div>
        </form>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          {/* Card 1: Exportar Arquivo JSON */}
          <div
            className="card-cloud card-interactive"
            style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h2 className="card-title">Exportar Arquivo JSON</h2>
              <Download size={20} color="var(--color-signal-violet)" strokeWidth={1.85} />
            </div>
            <p style={{ color: 'var(--color-slate)', fontSize: '15px' }}>
              Baixe uma cópia completa do seu histórico financeiro (contas, categorias, lançamentos e metas) em formato estruturado.
            </p>
            <div>
              <button
                type="button"
                onClick={handleExportarPortabilidade}
                className="btn-secondary"
              >
                <Download size={16} />
                <span>Baixar Dados (.JSON)</span>
              </button>
            </div>
          </div>

          {/* Card 2: Encerrar Conta */}
          <div
            className="card-porcelain"
            style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h2 className="card-title">Encerrar Conta</h2>
              <Trash2 size={20} color="var(--color-inkstone)" strokeWidth={1.85} />
            </div>
            <p style={{ color: 'var(--color-slate)', fontSize: '15px' }}>
              Remove permanentemente seu perfil e todos os registros financeiros vinculados. Esta ação não pode ser desfeita.
            </p>
            <div
              style={{
                display: 'flex',
                flexWrap: 'wrap',
                gap: '12px',
                alignItems: 'center',
              }}
            >
              <input
                type="text"
                className="input-shares"
                style={{ flex: '1 1 180px' }}
                value={confirmacaoExclusao}
                onChange={(e) => setConfirmacaoExclusao(e.target.value)}
                placeholder="Digite EXCLUIR"
              />
              <button
                type="button"
                onClick={handleExcluirContaDefinitivamente}
                disabled={confirmacaoExclusao !== 'EXCLUIR'}
                className="btn-primary"
              >
                <Trash2 size={16} />
                <span>Encerrar Conta</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
