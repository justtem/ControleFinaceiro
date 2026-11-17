const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const TOKEN_STORAGE_KEY = 'financeiro_jwt_token';

export const authStorage = {
  getToken: () => sessionStorage.getItem(TOKEN_STORAGE_KEY),
  setToken: (token) => sessionStorage.setItem(TOKEN_STORAGE_KEY, token),
  clearToken: () => sessionStorage.removeItem(TOKEN_STORAGE_KEY),
};

export async function apiFetch(endpoint, options = {}) {
  const token = authStorage.getToken();

  const headers = {
    Accept: 'application/json, text/plain, */*',
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  let response;
  try {
    response = await fetch(`${API_BASE_URL}${endpoint}`, { ...options, headers });
  } catch {
    throw {
      status: 0,
      message: 'Servidor indisponível em http://localhost:8080. Verifique se o Spring Boot está rodando.',
      fieldErrors: null,
    };
  }

  if (response.status === 204) return null;

  const rawText = await response.text();
  let parsedData = null;

  if (rawText) {
    try {
      parsedData = JSON.parse(rawText);
    } catch {
      parsedData = rawText;
    }
  }

  if (!response.ok) {
    if (response.status === 401 && !endpoint.startsWith('/auth/login')) {
      authStorage.clearToken();
      window.dispatchEvent(new CustomEvent('auth:unauthorized'));
    }

    let errorMessage = 'Erro ao processar a solicitação.';
    let fieldErrors = null;

    if (typeof parsedData === 'string' && parsedData.trim()) {
      errorMessage = parsedData;
    } else if (parsedData && typeof parsedData === 'object') {
      if (parsedData.mensagem) {
        errorMessage = parsedData.mensagem;
      } else {
        fieldErrors = parsedData;
        const firstError = Object.values(parsedData)[0];
        if (typeof firstError === 'string') errorMessage = firstError;
      }
    }

    throw { status: response.status, message: errorMessage, fieldErrors };
  }

  return parsedData;
}

export const api = {
  auth: {
    termosLgpd: () => apiFetch('/auth/termos-lgpd'),
    register: (dados) => apiFetch('/auth/register', { method: 'POST', body: JSON.stringify(dados) }),
    login: (cred) => apiFetch('/auth/login', { method: 'POST', body: JSON.stringify(cred) }),
  },
  usuarios: {
    me: () => apiFetch('/usuarios/me'),
    atualizar: (dados) => apiFetch('/usuarios/me', { method: 'PUT', body: JSON.stringify(dados) }),
    exportarLgpd: () => apiFetch('/usuarios/me/exportar'),
    excluirConta: () => apiFetch('/usuarios/me', { method: 'DELETE' }),
  },
  dashboard: {
    resumo: (dataInicio, dataFim) => {
      const p = new URLSearchParams();
      if (dataInicio) p.append('dataInicio', dataInicio);
      if (dataFim) p.append('dataFim', dataFim);
      const q = p.toString();
      return apiFetch(`/dashboard${q ? `?${q}` : ''}`);
    },
    mensal: (ano, mes) => {
      const p = new URLSearchParams();
      if (ano) p.append('ano', ano);
      if (mes) p.append('mes', mes);
      return apiFetch(`/dashboard/mensal?${p.toString()}`);
    },
  },
  categorias: {
    listar: () => apiFetch('/categorias'),
    cadastrar: (dados) => apiFetch('/categorias', { method: 'POST', body: JSON.stringify(dados) }),
    atualizar: (id, dados) => apiFetch(`/categorias/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
    excluir: (id) => apiFetch(`/categorias/${id}`, { method: 'DELETE' }),
  },
  contas: {
    listar: () => apiFetch('/contas'),
    cadastrar: (dados) => apiFetch('/contas', { method: 'POST', body: JSON.stringify(dados) }),
    atualizar: (id, dados) => apiFetch(`/contas/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
    excluir: (id) => apiFetch(`/contas/${id}`, { method: 'DELETE' }),
  },
  lancamentos: {
    listar: (filtros = {}) => {
      const p = new URLSearchParams();
      Object.entries(filtros).forEach(([k, v]) => {
        if (v !== null && v !== undefined && v !== '') p.append(k, v);
      });
      const q = p.toString();
      return apiFetch(`/lancamentos${q ? `?${q}` : ''}`);
    },
    cadastrar: (dados) => apiFetch('/lancamentos', { method: 'POST', body: JSON.stringify(dados) }),
    atualizar: (id, dados) => apiFetch(`/lancamentos/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
    excluir: (id) => apiFetch(`/lancamentos/${id}`, { method: 'DELETE' }),
  },
  metas: {
    listar: () => apiFetch('/metas'),
    cadastrar: (dados) => apiFetch('/metas', { method: 'POST', body: JSON.stringify(dados) }),
    atualizar: (id, dados) => apiFetch(`/metas/${id}`, { method: 'PUT', body: JSON.stringify(dados) }),
    excluir: (id) => apiFetch(`/metas/${id}`, { method: 'DELETE' }),
  },
};