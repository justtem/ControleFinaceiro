import React, { useEffect, useState, useCallback } from 'react';
import {
  Plus,
  Pencil,
  Trash2,
  X,
  RotateCcw,
  SlidersHorizontal,
  ArrowUpRight,
  ArrowDownRight,
} from 'lucide-react';
import { api } from '../services/api';
import { formatCurrency, formatDate, getHojeIso, STATUS_LANCAMENTO_LABELS } from '../utils/formatters';

export default function LancamentosView({ notify }) {
  const [lancamentos, setLancamentos] = useState([]);
  const [contas, setContas] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [gavetaFiltrosAberta, setGavetaFiltrosAberta] = useState(false);

  const [filtros, setFiltros] = useState({
    dataInicio: '',
    dataFim: '',
    categoriaId: '',
    contaId: '',
    tipo: '',
    status: '',
  });

  const [modalAberto, setModalAberto] = useState(false);
  const [editandoId, setEditandoId] = useState(null);
  const [form, setForm] = useState({
    descricao: '',
    valor: '',
    tipo: 'DESPESA',
    data: getHojeIso(),
    dataVencimento: '',
    status: 'PAGO',
    categoriaId: '',
    contaId: '',
  });

  const carregarDados = useCallback(
    async (f = filtros) => {
      setCarregando(true);
      try {
        const [listaLanc, listaContas, listaCat] = await Promise.all([
          api.lancamentos.listar(f),
          api.contas.listar(),
          api.categorias.listar(),
        ]);
        setLancamentos(listaLanc);
        setContas(listaContas);
        setCategorias(listaCat);
      } catch (err) {
        notify(err.message, 'error');
      } finally {
        setCarregando(false);
      }
    },
    [filtros, notify]
  );

  useEffect(() => {
    carregarDados();
  }, [carregarDados]);

  const qtdFiltrosAtivos = Object.values(filtros).filter(
    (v) => v !== null && v !== undefined && v !== ''
  ).length;

  const categoriasCompativeis = categorias.filter((c) => c.tipo === form.tipo);

  const abrirNovo = () => {
    const cat = categorias.find((c) => c.tipo === 'DESPESA');
    const conta = contas.find((c) => c.ativa);
    setEditandoId(null);
    setForm({
      descricao: '',
      valor: '',
      tipo: 'DESPESA',
      data: getHojeIso(),
      dataVencimento: '',
      status: 'PAGO',
      categoriaId: cat ? String(cat.id) : '',
      contaId: conta ? String(conta.id) : '',
    });
    setModalAberto(true);
  };

  const limparFiltros = () => {
    const limpo = {
      dataInicio: '',
      dataFim: '',
      categoriaId: '',
      contaId: '',
      tipo: '',
      status: '',
    };
    setFiltros(limpo);
    carregarDados(limpo);
  };

  const salvar = async (e) => {
    e.preventDefault();
    const payload = {
      descricao: form.descricao.trim(),
      valor: Number(form.valor),
      tipo: form.tipo,
      data: form.data,
      dataVencimento: form.dataVencimento || null,
      status: form.status,
      categoriaId: Number(form.categoriaId),
      contaId: Number(form.contaId),
    };
    try {
      if (editandoId) await api.lancamentos.atualizar(editandoId, payload);
      else await api.lancamentos.cadastrar(payload);
      notify('Lançamento salvo.', 'success');
      setModalAberto(false);
      carregarDados();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  const excluir = async (l) => {
    if (!window.confirm(`Excluir lançamento "${l.descricao}"?`)) return;
    try {
      await api.lancamentos.excluir(l.id);
      notify('Lançamento excluído.', 'success');
      carregarDados();
    } catch (err) {
      notify(err.message, 'error');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Barra Superior Compacta */}
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
          <h1 className="section-title">Lançamentos</h1>
          <span className="badge-shares badge-neutral tabular">
            {lancamentos.length} {lancamentos.length === 1 ? 'item' : 'itens'}
          </span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
          {qtdFiltrosAtivos > 0 && (
            <button type="button" onClick={limparFiltros} className="btn-neutral">
              <RotateCcw size={14} />
              <span>Limpar</span>
            </button>
          )}

          <button
            type="button"
            onClick={() => setGavetaFiltrosAberta((prev) => !prev)}
            className={gavetaFiltrosAberta || qtdFiltrosAtivos > 0 ? 'btn-secondary' : 'btn-neutral'}
          >
            <SlidersHorizontal size={16} />
            <span>Filtros</span>
            {qtdFiltrosAtivos > 0 && (
              <span
                className="tabular"
                style={{
                  backgroundColor: 'var(--color-signal-violet)',
                  color: 'var(--color-porcelain)',
                  borderRadius: '99px',
                  padding: '1px 8px',
                  fontSize: '12px',
                  fontWeight: 700,
                }}
              >
                {qtdFiltrosAtivos}
              </span>
            )}
          </button>

          <button type="button" onClick={abrirNovo} className="btn-primary">
            <Plus size={18} />
            <span>Novo Lançamento</span>
          </button>
        </div>
      </div>

      {/* Gaveta Retrátil de Filtros */}
      {gavetaFiltrosAberta && (
        <form
          onSubmit={(e) => {
            e.preventDefault();
            carregarDados(filtros);
          }}
          className="card-cloud view-enter"
          style={{ padding: '24px' }}
        >
          <div className="grid-3">
            <div>
              <label className="label-shares">Data Inicial</label>
              <input
                type="date"
                className="input-shares tabular"
                value={filtros.dataInicio}
                onChange={(e) => setFiltros({ ...filtros, dataInicio: e.target.value })}
              />
            </div>
            <div>
              <label className="label-shares">Data Final</label>
              <input
                type="date"
                className="input-shares tabular"
                value={filtros.dataFim}
                onChange={(e) => setFiltros({ ...filtros, dataFim: e.target.value })}
              />
            </div>
            <div>
              <label className="label-shares">Tipo</label>
              <select
                className="input-shares"
                value={filtros.tipo}
                onChange={(e) => setFiltros({ ...filtros, tipo: e.target.value })}
              >
                <option value="">Todos</option>
                <option value="RECEITA">Receita</option>
                <option value="DESPESA">Despesa</option>
              </select>
            </div>
            <div>
              <label className="label-shares">Status</label>
              <select
                className="input-shares"
                value={filtros.status}
                onChange={(e) => setFiltros({ ...filtros, status: e.target.value })}
              >
                <option value="">Todos</option>
                <option value="PAGO">Pago</option>
                <option value="PENDENTE">Pendente</option>
                <option value="CANCELADO">Cancelado</option>
              </select>
            </div>
            <div>
              <label className="label-shares">Categoria</label>
              <select
                className="input-shares"
                value={filtros.categoriaId}
                onChange={(e) => setFiltros({ ...filtros, categoriaId: e.target.value })}
              >
                <option value="">Todas</option>
                {categorias.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nome}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="label-shares">Conta</label>
              <select
                className="input-shares"
                value={filtros.contaId}
                onChange={(e) => setFiltros({ ...filtros, contaId: e.target.value })}
              >
                <option value="">Todas</option>
                {contas.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nome}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div
            style={{
              display: 'flex',
              justifyContent: 'flex-end',
              gap: '10px',
              marginTop: '20px',
            }}
          >
            <button type="button" onClick={limparFiltros} className="btn-neutral">
              <RotateCcw size={14} />
              <span>Redefinir</span>
            </button>
            <button type="submit" className="btn-primary">
              Aplicar Filtros
            </button>
          </div>
        </form>
      )}

      {/* Tabela Limpa em Destaque */}
      <div className="card-porcelain" style={{ overflowX: 'auto', padding: '12px 20px' }}>
        {carregando ? (
          <p style={{ color: 'var(--color-slate)', padding: '28px 12px' }}>
            Carregando lançamentos...
          </p>
        ) : lancamentos.length === 0 ? (
          <p style={{ color: 'var(--color-slate)', padding: '28px 12px' }}>
            Nenhum lançamento encontrado.
          </p>
        ) : (
          <table className="table-shares">
            <thead>
              <tr>
                <th>Data</th>
                <th>Descrição</th>
                <th>Categoria</th>
                <th>Conta</th>
                <th>Status</th>
                <th style={{ textAlign: 'right' }}>Valor</th>
                <th style={{ textAlign: 'right' }}>Ações</th>
              </tr>
            </thead>
            <tbody>
              {lancamentos.map((l) => {
                const isRec = l.tipo === 'RECEITA';
                return (
                  <tr key={l.id}>
                    <td className="tabular" style={{ whiteSpace: 'nowrap' }}>
                      <div style={{ fontWeight: 500 }}>{formatDate(l.data)}</div>
                      {l.dataVencimento && (
                        <div style={{ fontSize: '12px', color: 'var(--color-smoke)' }}>
                          Venc. {formatDate(l.dataVencimento)}
                        </div>
                      )}
                    </td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <span
                          style={{
                            width: '28px',
                            height: '28px',
                            borderRadius: '99px',
                            backgroundColor: 'var(--surface-cloud)',
                            display: 'inline-flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            flexShrink: 0,
                          }}
                        >
                          {isRec ? (
                            <ArrowUpRight size={15} color="var(--color-functional-green)" />
                          ) : (
                            <ArrowDownRight size={15} color="var(--color-signal-violet)" />
                          )}
                        </span>
                        <span style={{ fontWeight: 700 }}>{l.descricao}</span>
                      </div>
                    </td>
                    <td>
                      <span className="badge-shares badge-neutral">{l.categoriaNome}</span>
                    </td>
                    <td style={{ color: 'var(--color-slate)' }}>{l.contaNome}</td>
                    <td>
                      <span
                        className={`badge-shares ${
                          l.status === 'PAGO' ? 'badge-active' : 'badge-neutral'
                        }`}
                      >
                        {STATUS_LANCAMENTO_LABELS[l.status] || l.status}
                      </span>
                    </td>
                    <td
                      className="tabular"
                      style={{
                        textAlign: 'right',
                        fontWeight: 700,
                        whiteSpace: 'nowrap',
                        color: isRec ? 'var(--color-functional-green)' : 'var(--color-inkstone)',
                      }}
                    >
                      {isRec ? '+' : '-'} {formatCurrency(l.valor)}
                    </td>
                    <td style={{ textAlign: 'right', whiteSpace: 'nowrap' }}>
                      <div style={{ display: 'inline-flex', gap: '6px' }}>
                        <button
                          type="button"
                          onClick={() => {
                            setEditandoId(l.id);
                            setForm({
                              descricao: l.descricao,
                              valor: String(l.valor),
                              tipo: l.tipo,
                              data: l.data,
                              dataVencimento: l.dataVencimento || '',
                              status: l.status,
                              categoriaId: String(l.categoriaId),
                              contaId: String(l.contaId),
                            });
                            setModalAberto(true);
                          }}
                          className="btn-icon"
                          title="Editar"
                          aria-label="Editar lançamento"
                        >
                          <Pencil size={15} />
                        </button>
                        <button
                          type="button"
                          onClick={() => excluir(l)}
                          className="btn-icon"
                          title="Excluir"
                          aria-label="Excluir lançamento"
                        >
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>

      {modalAberto && (
        <div className="modal-backdrop">
          <form
            onSubmit={salvar}
            className="modal-floating"
            style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 className="card-title">
                {editandoId ? 'Editar Lançamento' : 'Novo Lançamento'}
              </h3>
              <button
                type="button"
                onClick={() => setModalAberto(false)}
                className="btn-icon"
                aria-label="Fechar"
              >
                <X size={18} />
              </button>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              {[
                { id: 'RECEITA', label: 'Entrada (Receita)' },
                { id: 'DESPESA', label: 'Saída (Despesa)' },
              ].map((t) => (
                <button
                  key={t.id}
                  type="button"
                  onClick={() => {
                    const cat = categorias.find((c) => c.tipo === t.id);
                    setForm({ ...form, tipo: t.id, categoriaId: cat ? String(cat.id) : '' });
                  }}
                  className={form.tipo === t.id ? 'btn-primary' : 'btn-secondary'}
                  style={{ flex: 1 }}
                >
                  {t.label}
                </button>
              ))}
            </div>

            <div className="grid-2">
              <div>
                <label className="label-shares">Descrição</label>
                <input
                  type="text"
                  required
                  className="input-shares"
                  value={form.descricao}
                  onChange={(e) => setForm({ ...form, descricao: e.target.value })}
                />
              </div>
              <div>
                <label className="label-shares">Valor (R$)</label>
                <input
                  type="number"
                  step="0.01"
                  min="0.01"
                  required
                  className="input-shares tabular"
                  value={form.valor}
                  onChange={(e) => setForm({ ...form, valor: e.target.value })}
                />
              </div>
            </div>

            <div className="grid-3">
              <div>
                <label className="label-shares">Data</label>
                <input
                  type="date"
                  required
                  className="input-shares tabular"
                  value={form.data}
                  onChange={(e) => setForm({ ...form, data: e.target.value })}
                />
              </div>
              <div>
                <label className="label-shares">Vencimento</label>
                <input
                  type="date"
                  className="input-shares tabular"
                  value={form.dataVencimento}
                  onChange={(e) => setForm({ ...form, dataVencimento: e.target.value })}
                />
              </div>
              <div>
                <label className="label-shares">Status</label>
                <select
                  className="input-shares"
                  value={form.status}
                  onChange={(e) => setForm({ ...form, status: e.target.value })}
                >
                  <option value="PAGO">Pago</option>
                  <option value="PENDENTE">Pendente</option>
                  <option value="CANCELADO">Cancelado</option>
                </select>
              </div>
            </div>

            <div className="grid-2">
              <div>
                <label className="label-shares">Categoria</label>
                <select
                  required
                  className="input-shares"
                  value={form.categoriaId}
                  onChange={(e) => setForm({ ...form, categoriaId: e.target.value })}
                >
                  <option value="" disabled>
                    Selecione...
                  </option>
                  {categoriasCompativeis.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.nome}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="label-shares">Conta</label>
                <select
                  required
                  className="input-shares"
                  value={form.contaId}
                  onChange={(e) => setForm({ ...form, contaId: e.target.value })}
                >
                  <option value="" disabled>
                    Selecione...
                  </option>
                  {contas.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.nome}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <button type="button" onClick={() => setModalAberto(false)} className="btn-secondary">
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
