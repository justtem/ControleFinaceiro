const brlFormatter = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
});

export function formatCurrency(value) {
  const num = Number(value ?? 0);
  return brlFormatter.format(Number.isNaN(num) ? 0 : num);
}

export function formatDate(isoDateString) {
  if (!isoDateString) return '—';
  const parts = isoDateString.split('T')[0].split('-');
  if (parts.length !== 3) return isoDateString;
  return `${parts[2]}/${parts[1]}/${parts[0]}`;
}

export function formatDateTime(isoDateTimeString) {
  if (!isoDateTimeString) return '—';
  const date = new Date(isoDateTimeString);
  if (Number.isNaN(date.getTime())) return isoDateTimeString;
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'medium',
  }).format(date);
}

export const TIPO_CONTA_LABELS = {
  CONTA_BANCARIA: 'Conta Bancária',
  CONTA_DIGITAL: 'Conta Digital',
  POUPANCA: 'Poupança',
  CARTEIRA: 'Carteira Física',
};

export const STATUS_LANCAMENTO_LABELS = {
  PAGO: 'Pago',
  PENDENTE: 'Pendente',
  CANCELADO: 'Cancelado',
};

export const STATUS_META_LABELS = {
  EM_ANDAMENTO: 'Em Andamento',
  CONCLUIDA: 'Concluída',
  CANCELADA: 'Cancelada',
};

export function getHojeIso() {
  return new Date().toISOString().split('T')[0];
}