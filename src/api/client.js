const BASE = import.meta.env.VITE_API_URL || ''

function request(url, options = {}) {
  const token = sessionStorage.getItem('token')
  const headers = {
    'Content-Type': 'application/json',
    ...(token && { Authorization: token }),
    ...options.headers,
  }
  return fetch(BASE + url, { ...options, headers })
    .then(async (res) => {
      const text = await res.text()
      const data = text ? JSON.parse(text) : {}
      if (!res.ok) throw new Error(data.message || '请求失败')
      return data
    })
}

export default {
  get: (url) => request(url, { method: 'GET' }),
  post: (url, body) => request(url, { method: 'POST', body: JSON.stringify(body) }),
  put: (url, body) => request(url, { method: 'PUT', body: JSON.stringify(body) }),
  delete: (url) => request(url, { method: 'DELETE' }),
}
