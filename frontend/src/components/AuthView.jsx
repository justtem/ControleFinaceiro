import React, { useState } from 'react';
import {
  Terminal,
  Shield,
  Lock,
  Activity,
  Check,
  Circle,
  AlertCircle,
  ChevronDown,
  ChevronUp,
} from 'lucide-react';
import { api, authStorage } from '../services/api';
import LgpdTermsModal from './LgpdTermsModal';

export default function AuthView({ onLoginSuccess, notify }) {
  const [aba, setAba] = useState('login');
  const [carregando, setCarregando] = useState(false);
  const [erroGeral, setErroGeral] = useState('');
  const [bloqueio429, setBloqueio429] = useState(false);
  const [errosCampo, setErrosCampo] = useState({});
  const [modalLgpdAberto, setModalLgpdAberto] = useState(false);
  const [faqAberta, setFaqAberta] = useState(null);

  const [loginEmail, setLoginEmail] = useState('');
  const [loginSenha, setLoginSenha] = useState('');

  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [aceitouTermos, setAceitouTermos] = useState(false);

  const tamOk = senha.length >= 8 && senha.length <= 100;
  const letraOk = /[A-Za-z]/.test(senha);
  const numOk = /\d/.test(senha);

  const handleLogin = async (e) => {
    e.preventDefault();
    setCarregando(true);
    setErroGeral('');
    setBloqueio429(false);
    try {
      const res = await api.auth.login({ email: loginEmail.trim(), senha: loginSenha });
      authStorage.setToken(res.token);
      notify('Sessão iniciada com sucesso.', 'success');
      onLoginSuccess();
    } catch (err) {
      if (err.status === 429) setBloqueio429(true);
      setErroGeral(err.message || 'E-mail ou senha inválidos.');
    } finally {
      setCarregando(false);
    }
  };

  const handleCadastro = async (e) => {
    e.preventDefault();
    setCarregando(true);
    setErroGeral('');
    setErrosCampo({});
    try {
      await api.auth.register({
        nome: nome.trim(),
        email: email.trim(),
        senha,
        aceitouTermos,
      });
      notify('Conta criada com sucesso! Faça login para continuar.', 'success');
      setLoginEmail(email.trim());
      setLoginSenha('');
      setNome('');
      setEmail('');
      setSenha('');
      setAceitouTermos(false);
      setAba('login');
    } catch (err) {
      setErroGeral(err.message);
      if (err.fieldErrors) setErrosCampo(err.fieldErrors);
    } finally {
      setCarregando(false);
    }
  };

  const faqs = [
    {
      q: 'Como o saldo das minhas contas é calculado?',
      a: 'O saldo de cada conta reflete em tempo real o saldo inicial somado às receitas recebidas e subtraído apenas das despesas já quitadas.',
    },
    {
      q: 'Como minha conta é protegida contra acessos indevidos?',
      a: 'Após 5 tentativas consecutivas de login com senha incorreta, o acesso é bloqueado temporariamente por 15 minutos para proteger seu patrimônio.',
    },
    {
      q: 'Posso exportar ou excluir meus dados a qualquer momento?',
      a: 'Sim. Na aba Privacidade você pode exportar todo o seu histórico em formato JSON ou encerrar sua conta de forma definitiva e imediata.',
    },
  ];

  return (
    <div className="view-enter">
      <header className="navbar-shares">
        <div
          className="container-shares"
          style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
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
            <span style={{ fontWeight: 700, fontSize: '20px', color: 'var(--color-inkstone)' }}>
              Shares Finance
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <button
              type="button"
              onClick={() => setModalLgpdAberto(true)}
              className="btn-secondary"
            >
              Privacidade & Termos
            </button>
          </div>
        </div>
      </header>

      <main className="container-shares">
        <section className="section-spacing">
          <div
            className="card-cloud grid-2"
            style={{
              textAlign: 'left',
              alignItems: 'center',
              gap: '48px',
              padding: 'clamp(28px, 4vw, 52px)',
            }}
          >
            <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
              <span className="badge-shares badge-active" style={{ alignSelf: 'flex-start' }}>
                Gestão Patrimonial Pessoal
              </span>
              <h1 className="display-headline">
                Clareza absoluta para o seu patrimônio.
              </h1>
              <p style={{ color: 'var(--color-slate)', fontSize: '18px', maxWidth: '560px' }}>
                Acompanhe contas, lançamentos, categorias e metas financeiras em uma interface limpa, rápida e com controle total sobre a sua privacidade.
              </p>

              <div style={{ display: 'flex', gap: '36px', flexWrap: 'wrap', paddingTop: '8px' }}>
                <div>
                  <div className="stat-value">100%</div>
                  <div style={{ color: 'var(--color-smoke)', fontSize: '14px' }}>
                    Saldos em Tempo Real
                  </div>
                </div>
                <div>
                  <div className="stat-value">Zero</div>
                  <div style={{ color: 'var(--color-smoke)', fontSize: '14px' }}>
                    Compartilhamento com Terceiros
                  </div>
                </div>
                <div>
                  <div className="stat-value">LGPD</div>
                  <div style={{ color: 'var(--color-smoke)', fontSize: '14px' }}>
                    Portabilidade & Exclusão
                  </div>
                </div>
              </div>
            </div>

            <div
              style={{
                backgroundColor: 'var(--surface-porcelain)',
                borderRadius: '28px',
                padding: '32px',
                boxShadow: 'var(--shadow-xl)',
                border: '1px solid var(--color-mist)',
              }}
            >
              <div style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
                <button
                  type="button"
                  onClick={() => {
                    setAba('login');
                    setErroGeral('');
                    setBloqueio429(false);
                  }}
                  className={aba === 'login' ? 'btn-primary' : 'btn-secondary'}
                  style={{ flex: 1 }}
                >
                  Entrar
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setAba('register');
                    setErroGeral('');
                    setErrosCampo({});
                  }}
                  className={aba === 'register' ? 'btn-primary' : 'btn-secondary'}
                  style={{ flex: 1 }}
                >
                  Criar Conta
                </button>
              </div>

              {erroGeral && (
                <div
                  className="accordion-shares"
                  style={{
                    marginBottom: '20px',
                    border: '1px solid var(--color-inkstone)',
                    display: 'flex',
                    gap: '12px',
                    alignItems: 'flex-start',
                  }}
                  role="alert"
                >
                  <AlertCircle
                    size={20}
                    color="var(--color-signal-violet)"
                    style={{ flexShrink: 0, marginTop: '2px' }}
                  />
                  <div>
                    <div
                      style={{
                        fontWeight: 700,
                        fontSize: '14px',
                        color: 'var(--color-inkstone)',
                      }}
                    >
                      {bloqueio429 ? 'Acesso Temporariamente Protegido' : 'Atenção'}
                    </div>
                    <div
                      style={{
                        fontSize: '14px',
                        color: 'var(--color-slate)',
                        marginTop: '4px',
                      }}
                    >
                      {erroGeral}
                    </div>
                  </div>
                </div>
              )}

              {aba === 'login' ? (
                <form
                  onSubmit={handleLogin}
                  style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}
                >
                  <div>
                    <label className="label-shares" htmlFor="login-email">
                      E-mail
                    </label>
                    <input
                      id="login-email"
                      type="email"
                      required
                      className="input-shares"
                      value={loginEmail}
                      onChange={(e) => setLoginEmail(e.target.value)}
                      placeholder="nome@exemplo.com"
                    />
                  </div>

                  <div>
                    <label className="label-shares" htmlFor="login-senha">
                      Senha
                    </label>
                    <input
                      id="login-senha"
                      type="password"
                      required
                      className="input-shares"
                      value={loginSenha}
                      onChange={(e) => setLoginSenha(e.target.value)}
                      placeholder="••••••••"
                    />
                  </div>

                  <button
                    type="submit"
                    disabled={carregando}
                    className="btn-primary"
                    style={{ width: '100%' }}
                  >
                    {carregando ? 'Entrando...' : 'Acessar Conta'}
                  </button>
                </form>
              ) : (
                <form
                  onSubmit={handleCadastro}
                  style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}
                >
                  <div>
                    <label className="label-shares" htmlFor="reg-nome">
                      Nome Completo
                    </label>
                    <input
                      id="reg-nome"
                      type="text"
                      required
                      maxLength={100}
                      className="input-shares"
                      value={nome}
                      onChange={(e) => setNome(e.target.value)}
                      placeholder="Ana Silva"
                    />
                    {errosCampo.nome && (
                      <span style={{ fontSize: '13px', color: 'var(--color-signal-violet)' }}>
                        {errosCampo.nome}
                      </span>
                    )}
                  </div>

                  <div>
                    <label className="label-shares" htmlFor="reg-email">
                      E-mail
                    </label>
                    <input
                      id="reg-email"
                      type="email"
                      required
                      className="input-shares"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      placeholder="ana.silva@email.com"
                    />
                    {errosCampo.email && (
                      <span style={{ fontSize: '13px', color: 'var(--color-signal-violet)' }}>
                        {errosCampo.email}
                      </span>
                    )}
                  </div>

                  <div>
                    <label className="label-shares" htmlFor="reg-senha">
                      Senha
                    </label>
                    <input
                      id="reg-senha"
                      type="password"
                      required
                      minLength={8}
                      maxLength={100}
                      className="input-shares"
                      value={senha}
                      onChange={(e) => setSenha(e.target.value)}
                      placeholder="Mínimo de 8 caracteres"
                    />
                    <div
                      style={{
                        display: 'flex',
                        gap: '8px',
                        flexWrap: 'wrap',
                        marginTop: '8px',
                      }}
                    >
                      <span className={`badge-shares ${tamOk ? 'badge-active' : 'badge-neutral'}`}>
                        {tamOk ? <Check size={13} /> : <Circle size={13} />} 8+ caracteres
                      </span>
                      <span
                        className={`badge-shares ${letraOk ? 'badge-active' : 'badge-neutral'}`}
                      >
                        {letraOk ? <Check size={13} /> : <Circle size={13} />} Letras
                      </span>
                      <span className={`badge-shares ${numOk ? 'badge-active' : 'badge-neutral'}`}>
                        {numOk ? <Check size={13} /> : <Circle size={13} />} Números
                      </span>
                    </div>
                    {errosCampo.senha && (
                      <span style={{ fontSize: '13px', color: 'var(--color-signal-violet)' }}>
                        {errosCampo.senha}
                      </span>
                    )}
                  </div>

                  <div className="accordion-shares" style={{ padding: '14px 16px' }}>
                    <label
                      style={{
                        display: 'flex',
                        gap: '12px',
                        alignItems: 'flex-start',
                        cursor: 'pointer',
                        fontSize: '14px',
                        color: 'var(--color-graphite)',
                      }}
                    >
                      <input
                        type="checkbox"
                        checked={aceitouTermos}
                        onChange={(e) => setAceitouTermos(e.target.checked)}
                        style={{
                          marginTop: '4px',
                          accentColor: 'var(--color-signal-violet)',
                          width: '16px',
                          height: '16px',
                        }}
                      />
                      <span>
                        Li e concordo com os{' '}
                        <button
                          type="button"
                          onClick={() => setModalLgpdAberto(true)}
                          style={{
                            background: 'none',
                            border: 'none',
                            color: 'var(--color-signal-violet)',
                            fontWeight: 700,
                            textDecoration: 'underline',
                            cursor: 'pointer',
                            fontFamily: 'inherit',
                          }}
                        >
                          Termos de Uso e Política de Privacidade
                        </button>
                        .
                      </span>
                    </label>
                    {errosCampo.aceitouTermos && (
                      <p
                        style={{
                          fontSize: '13px',
                          color: 'var(--color-signal-violet)',
                          marginTop: '8px',
                        }}
                      >
                        {errosCampo.aceitouTermos}
                      </p>
                    )}
                  </div>

                  <button
                    type="submit"
                    disabled={carregando}
                    className="btn-primary"
                    style={{ width: '100%' }}
                  >
                    {carregando ? 'Criando conta...' : 'Criar Conta'}
                  </button>
                </form>
              )}
            </div>
          </div>
        </section>

        <section style={{ paddingBottom: '48px' }}>
          <div className="grid-3">
            <div className="feature-card">
              <Activity size={28} color="var(--color-signal-violet)" strokeWidth={1.85} />
              <h3 className="card-title">Balanço e Liquidação Visual</h3>
              <p style={{ color: 'var(--color-slate)', fontSize: '15px' }}>
                Visualize instantaneamente quanto das suas despesas do mês já foi quitado e o que ainda está pendente.
              </p>
            </div>

            <div className="feature-card">
              <Lock size={28} color="var(--color-signal-violet)" strokeWidth={1.85} />
              <h3 className="card-title">Segurança e Proteção Ativa</h3>
              <p style={{ color: 'var(--color-slate)', fontSize: '15px' }}>
                Suas contas e movimentações são isoladas com criptografia moderna e proteção automática contra acessos indevidos.
              </p>
            </div>

            <div className="feature-card">
              <Shield size={28} color="var(--color-signal-violet)" strokeWidth={1.85} />
              <h3 className="card-title">Controle dos Seus Dados</h3>
              <p style={{ color: 'var(--color-slate)', fontSize: '15px' }}>
                Transparência total sobre como seus dados são tratados, com exportação em um clique e encerramento imediato quando desejar.
              </p>
            </div>
          </div>
        </section>

        <section style={{ maxWidth: '820px', margin: '0 auto', paddingBottom: '56px' }}>
          <h2 className="section-title" style={{ textAlign: 'center', marginBottom: '24px' }}>
            Dúvidas Frequentes
          </h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {faqs.map((f, i) => {
              const aberta = faqAberta === i;
              return (
                <div key={i} className="accordion-shares">
                  <button
                    type="button"
                    onClick={() => setFaqAberta(aberta ? null : i)}
                    style={{
                      width: '100%',
                      background: 'none',
                      border: 'none',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      cursor: 'pointer',
                      fontFamily: 'var(--font-aeonik)',
                      fontSize: '16px',
                      fontWeight: 500,
                      color: 'var(--color-inkstone)',
                      textAlign: 'left',
                    }}
                  >
                    <span>{f.q}</span>
                    {aberta ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
                  </button>
                  {aberta && (
                    <p style={{ marginTop: '12px', color: 'var(--color-slate)', fontSize: '15px' }}>
                      {f.a}
                    </p>
                  )}
                </div>
              );
            })}
          </div>
        </section>
      </main>

      <LgpdTermsModal
        isOpen={modalLgpdAberto}
        onClose={() => setModalLgpdAberto(false)}
        onAccept={() => setAceitouTermos(true)}
      />
    </div>
  );
}
