import React, { useEffect, useState } from 'react';
import { Shield, X, ChevronDown, ChevronUp } from 'lucide-react';
import { api } from '../services/api';

export default function LgpdTermsModal({ isOpen, onClose, onAccept }) {
  const [termos, setTermos] = useState(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [abertoIdx, setAbertoIdx] = useState(0);

  useEffect(() => {
    if (!isOpen) return;
    setCarregando(true);
    setErro('');
    api.auth
      .termosLgpd()
      .then(setTermos)
      .catch((err) => setErro(err.message || 'Falha ao consultar os termos de privacidade.'))
      .finally(() => setCarregando(false));
  }, [isOpen]);

  if (!isOpen) return null;

  const secoesFaq = termos
    ? [
        {
          pergunta: '1. Finalidade do uso dos dados',
          resposta: termos.finalidadeTratamento,
        },
        {
          pergunta: '2. Dados coletados',
          resposta: termos.dadosColetados?.join(' • '),
        },
        {
          pergunta: '3. Retenção, portabilidade e exclusão',
          resposta: `${termos.retencaoDados} (${termos.compartilhamentoDados})`,
        },
        {
          pergunta: '4. Contato de Privacidade (DPO)',
          resposta: termos.contatoEncarregadoDpo,
        },
      ]
    : [];

  return (
    <div className="modal-backdrop" role="dialog" aria-modal="true" aria-labelledby="lgpd-title">
      <div className="modal-floating">
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '20px',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div
              style={{
                width: '40px',
                height: '40px',
                borderRadius: '99px',
                backgroundColor: 'var(--surface-cloud)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <Shield size={20} color="var(--color-signal-violet)" strokeWidth={1.85} />
            </div>
            <div>
              <h2 id="lgpd-title" className="card-title">
                Termos e Privacidade
              </h2>
              {termos?.versaoTermos && (
                <span style={{ fontSize: '13px', color: 'var(--color-smoke)' }}>
                  Versão {termos.versaoTermos}
                </span>
              )}
            </div>
          </div>
          <button type="button" onClick={onClose} className="btn-icon" aria-label="Fechar">
            <X size={18} />
          </button>
        </div>

        {carregando && (
          <p style={{ color: 'var(--color-slate)', padding: '24px 0' }}>
            Carregando termos de privacidade...
          </p>
        )}

        {erro && (
          <div
            className="accordion-shares"
            style={{ marginBottom: '16px', border: '1px solid var(--color-inkstone)' }}
          >
            <p style={{ color: 'var(--color-inkstone)' }}>{erro}</p>
          </div>
        )}

        {termos && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {secoesFaq.map((item, idx) => {
              const aberto = abertoIdx === idx;
              return (
                <div key={idx} className="accordion-shares">
                  <button
                    type="button"
                    onClick={() => setAbertoIdx(aberto ? -1 : idx)}
                    style={{
                      width: '100%',
                      background: 'transparent',
                      border: 'none',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      cursor: 'pointer',
                      textAlign: 'left',
                      fontFamily: 'var(--font-aeonik)',
                      fontSize: '15px',
                      fontWeight: 700,
                      color: 'var(--color-inkstone)',
                    }}
                  >
                    <span>{item.pergunta}</span>
                    {aberto ? (
                      <ChevronUp size={18} color="var(--color-graphite)" />
                    ) : (
                      <ChevronDown size={18} color="var(--color-graphite)" />
                    )}
                  </button>
                  {aberto && (
                    <p
                      style={{
                        marginTop: '10px',
                        color: 'var(--color-slate)',
                        fontSize: '14px',
                        lineHeight: 1.55,
                      }}
                    >
                      {item.resposta}
                    </p>
                  )}
                </div>
              );
            })}
          </div>
        )}

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
          <button type="button" onClick={onClose} className="btn-secondary">
            Fechar
          </button>
          {onAccept && (
            <button
              type="button"
              onClick={() => {
                onAccept();
                onClose();
              }}
              className="btn-primary"
            >
              Concordar e Continuar
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
