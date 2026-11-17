import React, { useEffect, useState, useCallback } from 'react';
import {
  CreditCard,
  Landmark,
  Wallet,
  PiggyBank,
  Plus,
  Pencil,
  Trash2,
  X,
  ArrowUpRight,
  ArrowDownRight,
} from 'lucide-react';
import { api } from '../services/api';
import { formatCurrency, TIPO_CONTA_LABELS } from '../utils/formatters';

export default function ContasCategoriasView({ notify }) {
  const [contas, setContas] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [carregando, setCarregando] = useState(true);

  const [modalConta, setModalConta] = useState(false);
  const [contaId, setContaId] = useState(null);
  const [nomeConta, setNomeConta] = useState('');
  const [tipoConta, setTipoConta] = useState('CONTA_DIGITAL');
  const [saldoInicial, setSaldoInicial] = useState('0.00');

  const [modalCat, setModalCat] = useState(false);
  const [catId, setCatId] = useState(null);
  const [nomeCat, setNomeCat] = useState('');
  const [tipoCat, setTipoCat] = useState('DESPESA');
  const [descCat, setDescCat] = useState('');

  const carregar = useCallback(async () => {
    setCarregando(true);
    try {
      const [c, cat] = await Promise.all([api.contas.listar(), api.categorias.listar()]);
      setContas(c);
      setCategorias(cat);
    } catch (err) {
      notify(err.message, 'error');
    } finally {
      setCarregando(false);
    }
  }, [notify]);

  useEffect(() => {
    carregar();
  }, [carregar]);

  const salvarConta = async (e) => {
    e.preventDefault();
    const payload = {
      nome: nomeConta.trim(),
      tipo: tipoConta,
      saldoInicial: Number(saldoInicial),
    };
    try {
      if (contaId) await api.contas.atualizar(contaId, payload);
      else await api.contas.cadastrar(payload);
      notify('Conta salva.', 'success');
      setModalConta(false);
      carregar();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  const excluirConta = async (c) => {
    if (!window.confirm(`Deseja excluir a conta "${c.nome}"?`)) return;
    try {
      await api.contas.excluir(c.id);
      notify('Conta excluída.', 'success');
      carregar();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  const salvarCategoria = async (e) => {
    e.preventDefault();
    const payload = {
      nome: nomeCat.trim(),
      tipo: tipoCat,
      descricao: descCat.trim() || null,
    };
    try {
      if (catId) await api.categorias.atualizar(catId, payload);
      else await api.categorias.cadastrar(payload);
      notify('Categoria salva.', 'success');
      setModalCat(false);
      carregar();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  const excluirCategoria = async (cat) => {
    if (!window.confirm(`Deseja excluir a categoria "${cat.nome}"?`)) return;
    try {
      await api.categorias.excluir(cat.id);
      notify('Categoria excluída.', 'success');
      carregar();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  const obterIconeConta = (tipo) => {
    switch (tipo) {
      case 'CONTA_BANCARIA':
        return Landmark;
      case 'POUPANCA':
        return PiggyBank;
      case 'CARTEIRA':
        return Wallet;
      default:
        return CreditCard;
    }
  };

  const gruposCategorias = [
    {
      tipo: 'RECEITA',
      titulo: 'Entradas',
      corIndicador: 'var(--color-functional-green)',
      Icone: ArrowUpRight,
    },
    {
      tipo: 'DESPESA',
      titulo: 'Saídas',
      corIndicador: 'var(--color-signal-violet)',
      Icone: ArrowDownRight,
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '40px' }}>
      {/* Seção de Contas — Estética de Cartões Financeiros Modernos */}
      <section style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
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
            <h1 className="section-title">Contas</h1>
            <span className="badge-shares badge-neutral tabular">{contas.length}</span>
          </div>

          <button
            type="button"
            onClick={() => {
              setContaId(null);
              setNomeConta('');
              setTipoConta('CONTA_DIGITAL');
              setSaldoInicial('0.00');
              setModalConta(true);
            }}
            className="btn-primary"
          >
            <Plus size={18} />
            <span>Nova Conta</span>
          </button>
        </div>

        {carregando ? (
          <p style={{ color: 'var(--color-slate)' }}>Carregando contas...</p>
        ) : contas.length === 0 ? (
          <div className="card-cloud">
            <p style={{ color: 'var(--color-slate)' }}>Nenhuma conta cadastrada ainda.</p>
          </div>
        ) : (
          <div className="grid-3">
            {contas.map((c) => {
              const IconeModalidade = obterIconeConta(c.tipo);
              return (
                <div key={c.id} className="bank-card">
                  {/* Topo do Cartão: Ícone + Badge + Ações com Ícones Discretos */}
                  <div
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      gap: '12px',
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <div
                        style={{
                          width: '40px',
                          height: '40px',
                          borderRadius: '99px',
                          backgroundColor: 'var(--surface-porcelain)',
                          border: '1px solid var(--color-mist)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                        }}
                      >
                        <IconeModalidade
                          size={19}
                          color="var(--color-signal-violet)"
                          strokeWidth={1.85}
                        />
                      </div>
                      <span className="badge-shares badge-neutral">
                        {TIPO_CONTA_LABELS[c.tipo] || c.tipo}
                      </span>
                    </div>

                    <div style={{ display: 'inline-flex', gap: '6px' }}>
                      <button
                        type="button"
                        onClick={() => {
                          setContaId(c.id);
                          setNomeConta(c.nome);
                          setTipoConta(c.tipo);
                          setSaldoInicial(String(c.saldoInicial));
                          setModalConta(true);
                        }}
                        className="btn-icon"
                        title="Editar conta"
                        aria-label={`Editar conta ${c.nome}`}
                      >
                        <Pencil size={15} />
                      </button>
                      <button
                        type="button"
                        onClick={() => excluirConta(c)}
                        className="btn-icon"
                        title="Excluir conta"
                        aria-label={`Excluir conta ${c.nome}`}
                      >
                        <Trash2 size={15} />
                      </button>
                    </div>
                  </div>

                  {/* Centro do Cartão: Saldo Atual em Destaque */}
                  <div style={{ margin: '24px 0 20px' }}>
                    <div className="stat-value">{formatCurrency(c.saldoAtual)}</div>
                  </div>

                  {/* Rodapé do Cartão: Nome da Conta + Saldo Inicial */}
                  <div
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'flex-end',
                      gap: '12px',
                      borderTop: '1px solid var(--color-mist)',
                      paddingTop: '14px',
                    }}
                  >
                    <div>
                      <div style={{ fontWeight: 700, fontSize: '16px', color: 'var(--color-inkstone)' }}>
                        {c.nome}
                      </div>
                      <div className="tabular" style={{ fontSize: '13px', color: 'var(--color-smoke)' }}>
                        Inicial: {formatCurrency(c.saldoInicial)}
                      </div>
                    </div>

                    <span
                      style={{
                        width: '8px',
                        height: '8px',
                        borderRadius: '99px',
                        backgroundColor: c.ativa
                          ? 'var(--color-functional-green)'
                          : 'var(--color-ash)',
                        marginBottom: '6px',
                      }}
                      title={c.ativa ? 'Ativa' : 'Inativa'}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </section>

      {/* Seção de Categorias — Pills / Tags Interativas */}
      <section style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
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
            <h2 className="section-title">Categorias</h2>
            <span className="badge-shares badge-neutral tabular">{categorias.length}</span>
          </div>

          <button
            type="button"
            onClick={() => {
              setCatId(null);
              setNomeCat('');
              setTipoCat('DESPESA');
              setDescCat('');
              setModalCat(true);
            }}
            className="btn-secondary"
          >
            <Plus size={18} />
            <span>Nova Categoria</span>
          </button>
        </div>

        <div className="grid-2">
          {gruposCategorias.map(({ tipo, titulo, corIndicador, Icone }) => {
            const listaGrupo = categorias.filter((c) => c.tipo === tipo);
            return (
              <div
                key={tipo}
                className="card-cloud"
                style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}
              >
                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div
                      style={{
                        width: '32px',
                        height: '32px',
                        borderRadius: '99px',
                        backgroundColor: 'var(--surface-porcelain)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <Icone size={17} color={corIndicador} strokeWidth={2} />
                    </div>
                    <h3 className="card-title">{titulo}</h3>
                  </div>

                  <span className="badge-shares badge-neutral tabular">
                    {listaGrupo.length}
                  </span>
                </div>

                {listaGrupo.length === 0 ? (
                  <p style={{ color: 'var(--color-smoke)', fontSize: '14px' }}>
                    Nenhuma categoria em {titulo.toLowerCase()}.
                  </p>
                ) : (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                    {listaGrupo.map((cat) => (
                      <div
                        key={cat.id}
                        className="category-pill"
                        title={cat.descricao || cat.nome}
                      >
                        <span
                          style={{
                            width: '8px',
                            height: '8px',
                            borderRadius: '99px',
                            backgroundColor: corIndicador,
                            flexShrink: 0,
                          }}
                        />
                        <span style={{ fontWeight: 700, fontSize: '14px' }}>{cat.nome}</span>
                        {cat.descricao && (
                          <span style={{ fontSize: '13px', color: 'var(--color-smoke)' }}>
                            • {cat.descricao}
                          </span>
                        )}

                        <div className="category-pill-actions">
                          <button
                            type="button"
                            onClick={() => {
                              setCatId(cat.id);
                              setNomeCat(cat.nome);
                              setTipoCat(cat.tipo);
                              setDescCat(cat.descricao || '');
                              setModalCat(true);
                            }}
                            className="pill-action-btn"
                            title="Editar"
                            aria-label={`Editar categoria ${cat.nome}`}
                          >
                            <Pencil size={13} />
                          </button>
                          <button
                            type="button"
                            onClick={() => excluirCategoria(cat)}
                            className="pill-action-btn"
                            title="Excluir"
                            aria-label={`Excluir categoria ${cat.nome}`}
                          >
                            <Trash2 size={13} />
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </section>

      {modalConta && (
        <div className="modal-backdrop">
          <form
            onSubmit={salvarConta}
            className="modal-floating"
            style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 className="card-title">{contaId ? 'Editar Conta' : 'Nova Conta'}</h3>
              <button
                type="button"
                onClick={() => setModalConta(false)}
                className="btn-icon"
                aria-label="Fechar"
              >
                <X size={18} />
              </button>
            </div>
            <div>
              <label className="label-shares">Nome da Conta</label>
              <input
                type="text"
                required
                className="input-shares"
                value={nomeConta}
                onChange={(e) => setNomeConta(e.target.value)}
              />
            </div>
            <div>
              <label className="label-shares">Modalidade</label>
              <select
                className="input-shares"
                value={tipoConta}
                onChange={(e) => setTipoConta(e.target.value)}
              >
                {Object.entries(TIPO_CONTA_LABELS).map(([k, v]) => (
                  <option key={k} value={k}>
                    {v}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="label-shares">Saldo Inicial (R$)</label>
              <input
                type="number"
                step="0.01"
                required
                className="input-shares tabular"
                value={saldoInicial}
                onChange={(e) => setSaldoInicial(e.target.value)}
              />
            </div>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <button
                type="button"
                onClick={() => setModalConta(false)}
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

      {modalCat && (
        <div className="modal-backdrop">
          <form
            onSubmit={salvarCategoria}
            className="modal-floating"
            style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 className="card-title">{catId ? 'Editar Categoria' : 'Nova Categoria'}</h3>
              <button
                type="button"
                onClick={() => setModalCat(false)}
                className="btn-icon"
                aria-label="Fechar"
              >
                <X size={18} />
              </button>
            </div>
            <div>
              <label className="label-shares">Nome</label>
              <input
                type="text"
                required
                className="input-shares"
                value={nomeCat}
                onChange={(e) => setNomeCat(e.target.value)}
              />
            </div>
            <div>
              <label className="label-shares">Grupo</label>
              <select
                className="input-shares"
                value={tipoCat}
                onChange={(e) => setTipoCat(e.target.value)}
              >
                <option value="RECEITA">Entrada (Receita)</option>
                <option value="DESPESA">Saída (Despesa)</option>
              </select>
            </div>
            <div>
              <label className="label-shares">Descrição (Opcional)</label>
              <input
                type="text"
                className="input-shares"
                value={descCat}
                onChange={(e) => setDescCat(e.target.value)}
              />
            </div>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <button type="button" onClick={() => setModalCat(false)} className="btn-secondary">
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
