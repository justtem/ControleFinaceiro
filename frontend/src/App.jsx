import React, { useEffect, useState, useCallback } from 'react';
import {
  Terminal,
  LayoutGrid,
  ArrowLeftRight,
  CreditCard,
  Target,
  Shield,
  LogOut,
} from 'lucide-react';
import { api, authStorage } from './services/api';
import AuthView from './components/AuthView';
import DashboardView from './components/DashboardView';
import LancamentosView from './components/LancamentosView';
import ContasCategoriasView from './components/ContasCategoriasView';
import MetasView from './components/MetasView';
import PerfilLgpdView from './components/PerfilLgpdView';

export default function App() {
  const [autenticado, setAutenticado] = useState(Boolean(authStorage.getToken()));
  const [usuario, setUsuario] = useState(null);
  const [visaoAtiva, setVisaoAtiva] = useState('dashboard');
  const [toast, setToast] = useState(null);

  const notify = useCallback((message, type = 'info') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 4200);
  }, []);

  const carregarPerfil = useCallback(async () => {
    if (!authStorage.getToken()) {
      setAutenticado(false);
      setUsuario(null);
      return;
    }
    try {
      const dados = await api.usuarios.me();
      setUsuario(dados);
      setAutenticado(true);
    } catch {
      authStorage.clearToken();
      setAutenticado(false);
      setUsuario(null);
    }
  }, []);

  useEffect(() => {
    if (autenticado) carregarPerfil();
  }, [autenticado, carregarPerfil]);

  useEffect(() => {
    const handleUnauthorized = () => {
      setAutenticado(false);
      setUsuario(null);
      notify('Sessão encerrada.', 'error');
    };
    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, [notify]);

  const handleLogout = () => {
    authStorage.clearToken();
    setAutenticado(false);
    setUsuario(null);
    setVisaoAtiva('dashboard');
  };

  const itensMenu = [
    { id: 'dashboard', label: 'Visão Geral', icon: LayoutGrid },
    { id: 'lancamentos', label: 'Lançamentos', icon: ArrowLeftRight },
    { id: 'contas-categorias', label: 'Contas & Categorias', icon: CreditCard },
    { id: 'metas', label: 'Metas', icon: Target },
    { id: 'perfil-lgpd', label: 'Privacidade', icon: Shield },
  ];

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {toast && (
        <div
          className="view-enter"
          style={{
            position: 'fixed',
            bottom: '28px',
            right: '28px',
            zIndex: 60,
            backgroundColor:
              toast.type === 'error' ? 'var(--color-inkstone)' : 'var(--color-signal-violet)',
            color: 'var(--color-porcelain)',
            padding: '14px 24px',
            borderRadius: '99px',
            boxShadow: 'var(--shadow-xl)',
            fontSize: '14px',
            fontWeight: 500,
          }}
          role="status"
        >
          {toast.message}
        </div>
      )}

      {!autenticado ? (
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
          <AuthView onLoginSuccess={() => setAutenticado(true)} notify={notify} />
        </div>
      ) : (
        <>
          <header className="navbar-shares">
            <div
              className="container-shares"
              style={{
                display: 'flex',
                flexWrap: 'wrap',
                justifyContent: 'space-between',
                alignItems: 'center',
                gap: '16px',
                paddingTop: '10px',
                paddingBottom: '10px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <div
                  style={{
                    width: '38px',
                    height: '38px',
                    borderRadius: '99px',
                    backgroundColor: 'var(--surface-cloud)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}
                >
                  <Terminal size={20} color="var(--color-signal-violet)" strokeWidth={2.2} />
                </div>
                <span style={{ fontWeight: 700, fontSize: '19px', letterSpacing: '-0.02em' }}>
                  Shares Finance
                </span>
              </div>

              <nav style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }} aria-label="Navegação Principal">
                {itensMenu.map((item) => {
                  const Icon = item.icon;
                  const ativo = visaoAtiva === item.id;
                  return (
                    <button
                      key={item.id}
                      type="button"
                      onClick={() => setVisaoAtiva(item.id)}
                      className={`nav-link ${ativo ? 'active' : ''}`}
                    >
                      <Icon
                        size={17}
                        color={ativo ? 'var(--color-signal-violet)' : 'var(--color-graphite)'}
                        strokeWidth={1.85}
                      />
                      <span>{item.label}</span>
                    </button>
                  );
                })}
              </nav>

              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                {usuario?.nome && (
                  <span
                    className="badge-shares badge-neutral"
                    style={{ padding: '6px 14px', fontSize: '14px' }}
                  >
                    {usuario.nome}
                  </span>
                )}
                <button type="button" onClick={handleLogout} className="btn-neutral" title="Sair">
                  <LogOut size={16} />
                  <span>Sair</span>
                </button>
              </div>
            </div>
          </header>

          <main className="container-shares section-spacing view-enter" style={{ flex: 1 }} key={visaoAtiva}>
            {visaoAtiva === 'dashboard' && (
              <DashboardView onNavigate={setVisaoAtiva} notify={notify} />
            )}
            {visaoAtiva === 'lancamentos' && <LancamentosView notify={notify} />}
            {visaoAtiva === 'contas-categorias' && <ContasCategoriasView notify={notify} />}
            {visaoAtiva === 'metas' && <MetasView notify={notify} />}
            {visaoAtiva === 'perfil-lgpd' && (
              <PerfilLgpdView
                usuario={usuario}
                onUserUpdated={setUsuario}
                onAccountDeleted={() => {
                  setAutenticado(false);
                  setUsuario(null);
                }}
                notify={notify}
              />
            )}
          </main>
        </>
      )}

      <footer className="footer-dark">
        <div
          className="container-shares"
          style={{
            display: 'flex',
            flexWrap: 'wrap',
            justifyContent: 'space-between',
            alignItems: 'center',
            gap: '16px',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <Terminal size={18} color="var(--color-signal-violet)" />
            <span style={{ fontWeight: 700, fontSize: '15px', color: 'var(--color-porcelain)' }}>
              Shares Finance
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '14px', flexWrap: 'wrap' }}>
            <span style={{ color: 'var(--color-ash)', fontSize: '14px' }}>
              dpo.privacidade@projetofinanceiro.com.br
            </span>
            <span
              className="badge-shares"
              style={{
                backgroundColor: 'var(--color-inkstone)',
                color: 'var(--color-porcelain)',
                border: '1px solid var(--color-graphite)',
                fontSize: '12px',
                padding: '3px 10px',
              }}
            >
              <Shield size={12} color="var(--color-signal-violet)" />
              LGPD
            </span>
          </div>
        </div>
      </footer>
    </div>
  );
}
