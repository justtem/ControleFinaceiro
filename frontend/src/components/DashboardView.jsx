import React, { useEffect, useState, useCallback } from 'react';
import {
  Wallet,
  ArrowUpRight,
  ArrowDownRight,
  Clock,
  RefreshCw,
  PieChart,
  BarChart3,
  ArrowRight,
} from 'lucide-react';
import { api } from '../services/api';
import { formatCurrency, formatDate, STATUS_LANCAMENTO_LABELS } from '../utils/formatters';

export default function DashboardView({ onNavigate, notify }) {
  const hoje = new Date();
  const [modoFiltro, setModoFiltro] = useState('mensal');
  const [ano, setAno] = useState(hoje.getFullYear());
  const [mes, setMes] = useState(hoje.getMonth() + 1);
  const [dataInicio, setDataInicio] = useState('');
  const [dataFim, setDataFim] = useState('');
  const [dados, setDados] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [focoGrafico, setFocoGrafico] = useState(null);

  const carregarDashboard = useCallback(async () => {
    setCarregando(true);
    try {
      const res =
        modoFiltro === 'mensal'
          ? await api.dashboard.mensal(ano, mes)
          : await api.dashboard.resumo(dataInicio, dataFim);
      setDados(res);
    } catch (err) {
      notify(err.message || 'Erro ao carregar resumo financeiro.', 'error');
    } finally {
      setCarregando(false);
    }
  }, [modoFiltro, ano, mes, dataInicio, dataFim, notify]);

  useEffect(() => {
    carregarDashboard();
  }, [carregarDashboard]);

  const receitas = Number(dados?.receitasPeriodo ?? 0);
  const despesasPagas = Number(dados?.despesasPeriodo ?? 0);
  const despesasPendentes = Number(dados?.despesasPendentes ?? 0);
  const resultadoLiquido = Number(dados?.resultadoFinanceiro ?? 0);

  const totalCompromissos = despesasPagas + despesasPendentes;
  const percentualQuitado =
    totalCompromissos > 0
      ? Math.round((despesasPagas / totalCompromissos) * 100)
      : despesasPagas === 0 && despesasPendentes === 0
      ? 100
      : 0;

  const maiorVolumeComparativo = Math.max(receitas, despesasPagas, despesasPendentes, 1);

  const totalCategorias =
    dados?.despesasPorCategoria?.reduce((acc, i) => acc + Number(i.total), 0) || 0;

  // Geometria do Anel SVG de Liquidação
  const raioExterno = 74;
  const circunferenciaExterna = 2 * Math.PI * raioExterno;
  const arcoPago =
    totalCompromissos > 0
      ? (despesasPagas / totalCompromissos) * circunferenciaExterna
      : 0;
  const arcoPendente =
    totalCompromissos > 0
      ? (despesasPendentes / totalCompromissos) * circunferenciaExterna
      : 0;

  const raioInterno = 54;
  const circunferenciaInterna = 2 * Math.PI * raioInterno;
  const tetoReceitaReferencia = Math.max(receitas, totalCompromissos, 1);
  const arcoReceita =
    receitas > 0 ? Math.min(receitas / tetoReceitaReferencia, 1) * circunferenciaInterna : 0;

  const seriesComparativas = [
    {
      id: 'receitas',
      label: 'Receitas Recebidas',
      valor: receitas,
      cor: 'var(--color-functional-green)',
      tracejado: false,
      percBarra: receitas > 0 ? Math.max(Math.round((receitas / maiorVolumeComparativo) * 100), 6) : 0,
    },
    {
      id: 'pagas',
      label: 'Despesas Pagas',
      valor: despesasPagas,
      cor: 'var(--color-signal-violet)',
      tracejado: false,
      percBarra:
        despesasPagas > 0
          ? Math.max(Math.round((despesasPagas / maiorVolumeComparativo) * 100), 6)
          : 0,
    },
    {
      id: 'pendentes',
      label: 'Pagamentos Pendentes',
      valor: despesasPendentes,
      cor: 'var(--color-inkstone)',
      tracejado: true,
      percBarra:
        despesasPendentes > 0
          ? Math.max(Math.round((despesasPendentes / maiorVolumeComparativo) * 100), 6)
          : 0,
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
      {/* 1. Cabeçalho Compacto Widescreen */}
      <div
        style={{
          display: 'flex',
          flexWrap: 'wrap',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: '16px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px', flexWrap: 'wrap' }}>
          <h1 className="section-title">Visão Geral</h1>
          {dados && (
            <span className="badge-shares badge-neutral tabular">
              {formatDate(dados.dataInicio)} — {formatDate(dados.dataFim)}
            </span>
          )}
        </div>

        <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: '10px' }}>
          <div
            style={{
              display: 'inline-flex',
              backgroundColor: 'var(--surface-cloud)',
              padding: '4px',
              borderRadius: '99px',
              border: '1px solid var(--color-mist)',
            }}
          >
            <button
              type="button"
              onClick={() => setModoFiltro('mensal')}
              style={{
                border: 'none',
                borderRadius: '99px',
                padding: '7px 16px',
                fontSize: '14px',
                fontWeight: 500,
                cursor: 'pointer',
                fontFamily: 'var(--font-aeonik)',
                backgroundColor:
                  modoFiltro === 'mensal' ? 'var(--color-signal-violet)' : 'transparent',
                color:
                  modoFiltro === 'mensal' ? 'var(--color-porcelain)' : 'var(--color-graphite)',
                transition: 'all 0.2s var(--ease-shares)',
              }}
            >
              Mensal
            </button>
            <button
              type="button"
              onClick={() => setModoFiltro('periodo')}
              style={{
                border: 'none',
                borderRadius: '99px',
                padding: '7px 16px',
                fontSize: '14px',
                fontWeight: 500,
                cursor: 'pointer',
                fontFamily: 'var(--font-aeonik)',
                backgroundColor:
                  modoFiltro === 'periodo' ? 'var(--color-signal-violet)' : 'transparent',
                color:
                  modoFiltro === 'periodo' ? 'var(--color-porcelain)' : 'var(--color-graphite)',
                transition: 'all 0.2s var(--ease-shares)',
              }}
            >
              Período Livre
            </button>
          </div>

          {modoFiltro === 'mensal' ? (
            <div style={{ display: 'flex', gap: '8px' }}>
              <select
                value={mes}
                onChange={(e) => setMes(Number(e.target.value))}
                className="input-shares"
                style={{ width: 'auto', padding: '8px 14px', fontSize: '14px' }}
                aria-label="Mês"
              >
                {['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'].map(
                  (m, idx) => (
                    <option key={idx + 1} value={idx + 1}>
                      {m}
                    </option>
                  )
                )}
              </select>
              <input
                type="number"
                value={ano}
                onChange={(e) => setAno(Number(e.target.value))}
                className="input-shares tabular"
                style={{ width: '94px', padding: '8px 12px', fontSize: '14px' }}
                aria-label="Ano"
              />
            </div>
          ) : (
            <div style={{ display: 'flex', gap: '8px' }}>
              <input
                type="date"
                value={dataInicio}
                onChange={(e) => setDataInicio(e.target.value)}
                className="input-shares tabular"
                style={{ width: 'auto', padding: '8px 12px', fontSize: '14px' }}
                aria-label="Data inicial"
              />
              <input
                type="date"
                value={dataFim}
                onChange={(e) => setDataFim(e.target.value)}
                className="input-shares tabular"
                style={{ width: 'auto', padding: '8px 12px', fontSize: '14px' }}
                aria-label="Data final"
              />
            </div>
          )}

          <button
            type="button"
            onClick={carregarDashboard}
            className="btn-icon"
            title="Atualizar"
            aria-label="Atualizar dados"
          >
            <RefreshCw size={16} />
          </button>
        </div>
      </div>

      {/* 2. 4 KPIs Principais (Limpos, Tabular-Nums, Sem Textos Redundantes) */}
      <div className="grid-4">
        <div className="card-cloud card-interactive" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span className="eyebrow">SALDO TOTAL</span>
            <Wallet size={20} color="var(--color-signal-violet)" strokeWidth={1.8} />
          </div>
          <div className="stat-value">{formatCurrency(dados?.saldoTotal)}</div>
          <div>
            <span
              className={`badge-shares tabular ${
                resultadoLiquido >= 0 ? 'badge-green' : 'badge-dark'
              }`}
            >
              {resultadoLiquido >= 0 ? '+' : ''}
              {formatCurrency(resultadoLiquido)}
            </span>
          </div>
        </div>

        <div className="card-cloud card-interactive" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span className="eyebrow">RECEITAS RECEBIDAS</span>
            <ArrowUpRight size={20} color="var(--color-functional-green)" strokeWidth={2} />
          </div>
          <div className="stat-value" style={{ color: 'var(--color-functional-green)' }}>
            {formatCurrency(receitas)}
          </div>
        </div>

        <div className="card-cloud card-interactive" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span className="eyebrow">DESPESAS PAGAS</span>
            <ArrowDownRight size={20} color="var(--color-signal-violet)" strokeWidth={2} />
          </div>
          <div className="stat-value">{formatCurrency(despesasPagas)}</div>
        </div>

        <div className="card-cloud card-interactive" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span className="eyebrow">PAGAMENTOS PENDENTES</span>
            <Clock size={20} color="var(--color-inkstone)" strokeWidth={1.8} />
          </div>
          <div className="stat-value">{formatCurrency(despesasPendentes)}</div>
        </div>
      </div>

      {/* 3. Bento Grid Widescreen (3 Colunas: Liquidação + Categorias + Últimas Movimentações) */}
      <div className="bento-dashboard">
        {/* NOVO GRÁFICO 1 — Balanço de Liquidação (Pagamentos Feitos vs. Pendentes) */}
        <div
          className="card-cloud"
          style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', gap: '24px' }}
        >
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <PieChart size={20} color="var(--color-signal-violet)" strokeWidth={1.85} />
                <h2 className="card-title">Balanço de Liquidação</h2>
              </div>
              <span className="badge-shares badge-active tabular">{percentualQuitado}% Quitado</span>
            </div>

            {/* Anel SVG Interativo */}
            <div
              style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                padding: '8px 0 16px',
              }}
            >
              <div style={{ position: 'relative', width: '192px', height: '192px' }}>
                <svg width="192" height="192" viewBox="0 0 192 192" style={{ transform: 'rotate(-90deg)' }}>
                  {/* Trilha Externa (Compromissos) */}
                  <circle
                    cx="96"
                    cy="96"
                    r={raioExterno}
                    fill="none"
                    stroke="var(--color-mist)"
                    strokeWidth="14"
                  />

                  {/* Segmento de Pagamentos Pendentes (Inkstone tracejado/sólido) */}
                  {despesasPendentes > 0 && (
                    <circle
                      cx="96"
                      cy="96"
                      r={raioExterno}
                      fill="none"
                      stroke="var(--color-inkstone)"
                      strokeWidth={focoGrafico === 'pendentes' ? '17' : '14'}
                      strokeDasharray={`${arcoPendente} ${circunferenciaExterna}`}
                      strokeDashoffset={-arcoPago}
                      strokeLinecap="round"
                      className="svg-ring-progress"
                      style={{ cursor: 'pointer' }}
                      onMouseEnter={() => setFocoGrafico('pendentes')}
                      onMouseLeave={() => setFocoGrafico(null)}
                    />
                  )}

                  {/* Segmento de Despesas Pagas (Signal Violet) */}
                  {despesasPagas > 0 && (
                    <circle
                      cx="96"
                      cy="96"
                      r={raioExterno}
                      fill="none"
                      stroke="var(--color-signal-violet)"
                      strokeWidth={focoGrafico === 'pagas' ? '17' : '14'}
                      strokeDasharray={`${arcoPago} ${circunferenciaExterna}`}
                      strokeDashoffset="0"
                      strokeLinecap="round"
                      className="svg-ring-progress"
                      style={{ cursor: 'pointer' }}
                      onMouseEnter={() => setFocoGrafico('pagas')}
                      onMouseLeave={() => setFocoGrafico(null)}
                    />
                  )}

                  {/* Trilha Interna (Receitas Recebidas) */}
                  <circle
                    cx="96"
                    cy="96"
                    r={raioInterno}
                    fill="none"
                    stroke="var(--color-mist)"
                    strokeWidth="10"
                    opacity="0.65"
                  />

                  {/* Arco Interno de Receitas Recebidas (Functional Green) */}
                  {receitas > 0 && (
                    <circle
                      cx="96"
                      cy="96"
                      r={raioInterno}
                      fill="none"
                      stroke="var(--color-functional-green)"
                      strokeWidth={focoGrafico === 'receitas' ? '13' : '10'}
                      strokeDasharray={`${arcoReceita} ${circunferenciaInterna}`}
                      strokeDashoffset="0"
                      strokeLinecap="round"
                      className="svg-ring-progress"
                      style={{ cursor: 'pointer' }}
                      onMouseEnter={() => setFocoGrafico('receitas')}
                      onMouseLeave={() => setFocoGrafico(null)}
                    />
                  )}
                </svg>

                {/* Centro do Anel SVG */}
                <div
                  style={{
                    position: 'absolute',
                    inset: 0,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    textAlign: 'center',
                    pointerEvents: 'none',
                    padding: '24px',
                  }}
                >
                  {focoGrafico ? (
                    <>
                      <span className="eyebrow" style={{ fontSize: '10px' }}>
                        {focoGrafico === 'receitas'
                          ? 'RECEITAS'
                          : focoGrafico === 'pagas'
                          ? 'PAGAS'
                          : 'PENDENTES'}
                      </span>
                      <span
                        className="tabular"
                        style={{
                          fontWeight: 700,
                          fontSize: '15px',
                          color: 'var(--color-inkstone)',
                          marginTop: '2px',
                        }}
                      >
                        {formatCurrency(
                          focoGrafico === 'receitas'
                            ? receitas
                            : focoGrafico === 'pagas'
                            ? despesasPagas
                            : despesasPendentes
                        )}
                      </span>
                    </>
                  ) : (
                    <>
                      <span
                        className="tabular"
                        style={{
                          fontWeight: 700,
                          fontSize: '30px',
                          lineHeight: 1.05,
                          color: 'var(--color-inkstone)',
                        }}
                      >
                        {percentualQuitado}%
                      </span>
                      <span className="eyebrow" style={{ fontSize: '10px', marginTop: '4px' }}>
                        QUITADO
                      </span>
                    </>
                  )}
                </div>
              </div>
            </div>

            {/* Barras Comparativas Interativas */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {seriesComparativas.map((serie) => (
                <div
                  key={serie.id}
                  onMouseEnter={() => setFocoGrafico(serie.id)}
                  onMouseLeave={() => setFocoGrafico(null)}
                  style={{
                    backgroundColor:
                      focoGrafico === serie.id ? 'var(--surface-porcelain)' : 'transparent',
                    padding: '8px 10px',
                    borderRadius: '14px',
                    transition: 'background-color 0.2s ease',
                    cursor: 'pointer',
                  }}
                >
                  <div
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      marginBottom: '6px',
                      fontSize: '14px',
                    }}
                  >
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', fontWeight: 500 }}>
                      <span
                        style={{
                          width: '10px',
                          height: '10px',
                          borderRadius: '99px',
                          backgroundColor: serie.cor,
                          display: 'inline-block',
                        }}
                      />
                      {serie.label}
                    </span>
                    <strong className="tabular">{formatCurrency(serie.valor)}</strong>
                  </div>
                  <div className="progress-track" style={{ height: '8px' }}>
                    <div
                      className="progress-fill"
                      style={{
                        width: `${serie.percBarra}%`,
                        backgroundColor: serie.cor,
                        backgroundImage: serie.tracejado
                          ? 'repeating-linear-gradient(45deg, transparent, transparent 4px, rgba(255,255,255,0.25) 4px, rgba(255,255,255,0.25) 8px)'
                          : 'none',
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* NOVO GRÁFICO 2 — Distribuição por Categoria */}
        <div
          className="card-cloud"
          style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', gap: '24px' }}
        >
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <BarChart3 size={20} color="var(--color-signal-violet)" strokeWidth={1.85} />
                <h2 className="card-title">Despesas por Categoria</h2>
              </div>
              <span className="badge-shares badge-neutral tabular">
                {formatCurrency(totalCategorias)}
              </span>
            </div>

            {!dados?.despesasPorCategoria?.length ? (
              <div
                style={{
                  backgroundColor: 'var(--surface-porcelain)',
                  borderRadius: '16px',
                  padding: '32px 20px',
                  textAlign: 'center',
                  color: 'var(--color-smoke)',
                  fontSize: '15px',
                }}
              >
                Nenhuma despesa liquidada no período selecionado.
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {dados.despesasPorCategoria.map((cat, idx) => {
                  const perc =
                    totalCategorias > 0
                      ? Math.round((Number(cat.total) / totalCategorias) * 100)
                      : 0;
                  return (
                    <div
                      key={cat.categoriaId}
                      style={{
                        backgroundColor: 'var(--surface-porcelain)',
                        borderRadius: '16px',
                        padding: '14px 18px',
                      }}
                    >
                      <div
                        style={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                          marginBottom: '10px',
                          gap: '12px',
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <span
                            className="tabular"
                            style={{
                              width: '24px',
                              height: '24px',
                              borderRadius: '99px',
                              backgroundColor:
                                idx === 0 ? 'var(--color-signal-violet)' : 'var(--surface-cloud)',
                              color:
                                idx === 0 ? 'var(--color-porcelain)' : 'var(--color-graphite)',
                              fontSize: '12px',
                              fontWeight: 700,
                              display: 'inline-flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                            }}
                          >
                            {idx + 1}
                          </span>
                          <span style={{ fontWeight: 700, fontSize: '15px' }}>{cat.categoriaNome}</span>
                        </div>

                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                          <strong className="tabular" style={{ fontSize: '15px' }}>
                            {formatCurrency(cat.total)}
                          </strong>
                          <span className="badge-shares badge-neutral tabular" style={{ padding: '2px 8px', fontSize: '12px' }}>
                            {perc}%
                          </span>
                        </div>
                      </div>

                      <div className="progress-track" style={{ height: '8px' }}>
                        <div
                          className="progress-fill"
                          style={{
                            width: `${Math.max(perc, 5)}%`,
                            backgroundColor:
                              idx === 0 ? 'var(--color-signal-violet)' : 'var(--color-inkstone)',
                          }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <button
              type="button"
              onClick={() => onNavigate('contas-categorias')}
              className="btn-secondary"
            >
              <span>Categorias</span>
              <ArrowRight size={16} />
            </button>
          </div>
        </div>

        {/* 5. Últimas Movimentações */}
        <div
          className="card-cloud"
          style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', gap: '24px' }}
        >
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 className="card-title">Últimas Movimentações</h2>
              <span className="badge-shares badge-neutral">Recentes</span>
            </div>

            {!dados?.ultimasMovimentacoes?.length ? (
              <div
                style={{
                  backgroundColor: 'var(--surface-porcelain)',
                  borderRadius: '16px',
                  padding: '32px 20px',
                  textAlign: 'center',
                  color: 'var(--color-smoke)',
                  fontSize: '15px',
                }}
              >
                Nenhuma movimentação registrada ainda.
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {dados.ultimasMovimentacoes.map((item) => {
                  const isRec = item.tipo === 'RECEITA';
                  return (
                    <div key={item.id} className="tx-item">
                      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', minWidth: 0 }}>
                        <div
                          style={{
                            width: '36px',
                            height: '36px',
                            borderRadius: '99px',
                            backgroundColor: 'var(--surface-cloud)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            flexShrink: 0,
                          }}
                        >
                          {isRec ? (
                            <ArrowUpRight size={18} color="var(--color-functional-green)" />
                          ) : (
                            <ArrowDownRight size={18} color="var(--color-signal-violet)" />
                          )}
                        </div>
                        <div style={{ minWidth: 0 }}>
                          <div
                            style={{
                              fontWeight: 700,
                              fontSize: '15px',
                              color: 'var(--color-inkstone)',
                              whiteSpace: 'nowrap',
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                            }}
                          >
                            {item.descricao}
                          </div>
                          <div style={{ fontSize: '13px', color: 'var(--color-smoke)' }}>
                            {item.categoriaNome} • {item.contaNome} •{' '}
                            <span className="tabular">{formatDate(item.data)}</span>
                          </div>
                        </div>
                      </div>

                      <div style={{ textAlign: 'right', flexShrink: 0 }}>
                        <div
                          className="tabular"
                          style={{
                            fontWeight: 700,
                            fontSize: '15px',
                            color: isRec ? 'var(--color-functional-green)' : 'var(--color-inkstone)',
                          }}
                        >
                          {isRec ? '+' : '-'} {formatCurrency(item.valor)}
                        </div>
                        <span
                          className={`badge-shares ${
                            item.status === 'PAGO' ? 'badge-active' : 'badge-neutral'
                          }`}
                          style={{ marginTop: '4px', padding: '2px 9px', fontSize: '11px' }}
                        >
                          {STATUS_LANCAMENTO_LABELS[item.status] || item.status}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <button type="button" onClick={() => onNavigate('lancamentos')} className="btn-primary">
              <span>Ver Lançamentos</span>
              <ArrowRight size={16} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
