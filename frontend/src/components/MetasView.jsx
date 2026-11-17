import React, { useEffect, useState, useCallback } from 'react';
import { Plus, Pencil, Trash2, Check, X } from 'lucide-react';
import { api } from '../services/api';
import { formatCurrency, formatDate, STATUS_META_LABELS } from '../utils/formatters';

export default function MetasView({ notify }) {
  const [metas, setMetas] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [modalAberto, setModalAberto] = useState(false);
  const [editandoId, setEditandoId] = useState(null);
  const [form, setForm] = useState({
    nome: '',
    valorObjetivo: '',
    valorAtual: '0.00',
    dataLimite: '',
    status: 'EM_ANDAMENTO',
  });

  const carregar = useCallback(async () => {
    setCarregando(true);
    try {
      setMetas(await api.metas.listar());
    } catch (err) {
      notify(err.message, 'error');
    } finally {
      setCarregando(false);
    }
  }, [notify]);

  useEffect(() => {
    carregar();
  }, [carregar]);

  const salvar = async (e) => {
    e.preventDefault();
    const payload = {
      nome: form.nome.trim(),
      valorObjetivo: Number(form.valorObjetivo),
      valorAtual: Number(form.valorAtual),
      dataLimite: form.dataLimite,
      status: form.status === 'CANCELADA' ? 'CANCELADA' : null,
    };
    try {
      if (editandoId) await api.metas.atualizar(editandoId, payload);
      else await api.metas.cadastrar(payload);
      notify('Meta salva.', 'success');
      setModalAberto(false);
      carregar();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  const excluir = async (m) => {
    if (!window.confirm(`Deseja excluir a meta "${m.nome}"?`)) return;
    try {
      await api.metas.excluir(m.id);
      notify('Meta excluída.', 'success');
      carregar();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  const raioAnel = 54;
  const circunferenciaAnel = 2 * Math.PI * raioAnel;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
      <div
        style={{
          display: 'flex',
          flexWrap: 'wrap',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: '16px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <h1 className="section-title">Metas</h1>
          <span className="badge-shares badge-neutral tabular">{metas.length}</span>
        </div>

        <button
          type="button"
          onClick={() => {
            setEditandoId(null);
            setForm({
              nome: '',
              valorObjetivo: '',
              valorAtual: '0.00',
              dataLimite: '',
              status: 'EM_ANDAMENTO',
            });
            setModalAberto(true);
          }}
          className="btn-primary"
        >
          <Plus size={18} />
          <span>Nova Meta</span>
        </button>
      </div>

      {carregando ? (
        <p style={{ color: 'var(--color-slate)' }}>Carregando metas...</p>
      ) : metas.length === 0 ? (
        <div className="card-cloud">
          <p style={{ color: 'var(--color-slate)' }}>Nenhuma meta cadastrada ainda.</p>
        </div>
      ) : (
        <div className="grid-3">
          {metas.map((m) => {
            const perc = Number(m.percentualConcluido ?? 0);
            const isConcluida = m.status === 'CONCLUIDA' || perc >= 100;
            const percLimitado = isConcluida ? 100 : Math.min(Math.max(perc, 0), 100);
            const dashOffset =
              circunferenciaAnel - (percLimitado / 100) * circunferenciaAnel;

            return (
              <div
                key={m.id}
                className={`goal-card ${isConcluida ? 'completed' : ''}`}
              >
                {/* Topo: Badge de Status + Ações Discretas */}
                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    gap: '12px',
                  }}
                >
                  <span
                    className="badge-shares"
                    style={{
                      backgroundColor: isConcluida
                        ? 'rgba(255, 255, 255, 0.18)'
                        : 'var(--surface-porcelain)',
                      color: isConcluida
                        ? 'var(--color-porcelain)'
                        : 'var(--color-inkstone)',
                      border: isConcluida
                        ? '1px solid rgba(255, 255, 255, 0.32)'
                        : '1px solid var(--color-mist)',
                    }}
                  >
                    {isConcluida && <Check size={13} strokeWidth={2.5} />}
                    {STATUS_META_LABELS[m.status] || m.status}
                  </span>

                  <div style={{ display: 'inline-flex', gap: '6px' }}>
                    <button
                      type="button"
                      onClick={() => {
                        setEditandoId(m.id);
                        setForm({
                          nome: m.nome,
                          valorObjetivo: String(m.valorObjetivo),
                          valorAtual: String(m.valorAtual),
                          dataLimite: m.dataLimite,
                          status: m.status,
                        });
                        setModalAberto(true);
                      }}
                      className="btn-icon"
                      title="Atualizar meta"
                      aria-label={`Atualizar meta ${m.nome}`}
                    >
                      <Pencil size={15} />
                    </button>
                    <button
                      type="button"
                      onClick={() => excluir(m)}
                      className="btn-icon"
                      title="Excluir meta"
                      aria-label={`Excluir meta ${m.nome}`}
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                </div>

                {/* Centro: Anel Circular Radial SVG */}
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    padding: '4px 0',
                  }}
                >
                  <div style={{ position: 'relative', width: '140px', height: '140px' }}>
                    <svg
                      width="140"
                      height="140"
                      viewBox="0 0 140 140"
                      style={{ transform: 'rotate(-90deg)' }}
                    >
                      <circle
                        cx="70"
                        cy="70"
                        r={raioAnel}
                        fill="none"
                        stroke={
                          isConcluida
                            ? 'rgba(255, 255, 255, 0.22)'
                            : 'var(--color-mist)'
                        }
                        strokeWidth="10"
                      />
                      <circle
                        cx="70"
                        cy="70"
                        r={raioAnel}
                        fill="none"
                        stroke={
                          isConcluida
                            ? 'var(--color-porcelain)'
                            : 'var(--color-signal-violet)'
                        }
                        strokeWidth="10"
                        strokeDasharray={circunferenciaAnel}
                        strokeDashoffset={dashOffset}
                        strokeLinecap="round"
                        className="svg-ring-progress"
                      />
                    </svg>

                    <div
                      style={{
                        position: 'absolute',
                        inset: 0,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      {isConcluida ? (
                        <div
                          className="goal-check-badge"
                          style={{
                            width: '52px',
                            height: '52px',
                            borderRadius: '99px',
                            backgroundColor: 'var(--color-porcelain)',
                            color: 'var(--color-signal-violet)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                          }}
                        >
                          <Check size={28} strokeWidth={2.75} />
                        </div>
                      ) : (
                        <span
                          className="tabular"
                          style={{
                            fontWeight: 700,
                            fontSize: '24px',
                            color: 'var(--color-inkstone)',
                          }}
                        >
                          {perc.toFixed(0)}%
                        </span>
                      )}
                    </div>
                  </div>
                </div>

                {/* Base: Título, Prazo e Valores Acumulados */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div style={{ textAlign: 'center' }}>
                    <h3 className="card-title">{m.nome}</h3>
                    <div
                      className="tabular"
                      style={{
                        fontSize: '13px',
                        marginTop: '4px',
                        color: isConcluida
                          ? 'rgba(255, 255, 255, 0.78)'
                          : 'var(--color-smoke)',
                      }}
                    >
                      {formatDate(m.dataLimite)}
                    </div>
                  </div>

                  <div
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'flex-end',
                      paddingTop: '14px',
                      borderTop: isConcluida
                        ? '1px solid rgba(255, 255, 255, 0.22)'
                        : '1px solid var(--color-mist)',
                    }}
                  >
                    <div>
                      <span
                        className="eyebrow"
                        style={{
                          color: isConcluida
                            ? 'rgba(255, 255, 255, 0.72)'
                            : 'var(--color-smoke)',
                        }}
                      >
                        ATUAL
                      </span>
                      <div className="tabular" style={{ fontWeight: 700, fontSize: '17px' }}>
                        {formatCurrency(m.valorAtual)}
                      </div>
                    </div>

                    <div style={{ textAlign: 'right' }}>
                      <span
                        className="eyebrow"
                        style={{
                          color: isConcluida
                            ? 'rgba(255, 255, 255, 0.72)'
                            : 'var(--color-smoke)',
                        }}
                      >
                        OBJETIVO
                      </span>
                      <div
                        className="tabular"
                        style={{
                          fontWeight: 700,
                          fontSize: '17px',
                          color: isConcluida
                            ? 'var(--color-porcelain)'
                            : 'var(--color-slate)',
                        }}
                      >
                        {formatCurrency(m.valorObjetivo)}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {modalAberto && (
        <div className="modal-backdrop">
          <form
            onSubmit={salvar}
            className="modal-floating"
            style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 className="card-title">{editandoId ? 'Atualizar Meta' : 'Nova Meta'}</h3>
              <button
                type="button"
                onClick={() => setModalAberto(false)}
                className="btn-icon"
                aria-label="Fechar"
              >
                <X size={18} />
              </button>
            </div>
            <div>
              <label className="label-shares">Nome da Meta</label>
              <input
                type="text"
                required
                className="input-shares"
                value={form.nome}
                onChange={(e) => setForm({ ...form, nome: e.target.value })}
              />
            </div>
            <div className="grid-2">
              <div>
                <label className="label-shares">Valor Objetivo (R$)</label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  required
                  className="input-shares tabular"
                  value={form.valorObjetivo}
                  onChange={(e) => setForm({ ...form, valorObjetivo: e.target.value })}
                />
              </div>
              <div>
                <label className="label-shares">Valor Atual (R$)</label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  className="input-shares tabular"
                  value={form.valorAtual}
                  onChange={(e) => setForm({ ...form, valorAtual: e.target.value })}
                />
              </div>
            </div>
            <div>
              <label className="label-shares">Data Limite</label>
              <input
                type="date"
                required
                className="input-shares tabular"
                value={form.dataLimite}
                onChange={(e) => setForm({ ...form, dataLimite: e.target.value })}
              />
            </div>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <button
                type="button"
                onClick={() => setModalAberto(false)}
                className="btn-secondary"
              >
                Cancelar
              </button>
              <button type="submit" className="btn-primary">
                Salvar
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
