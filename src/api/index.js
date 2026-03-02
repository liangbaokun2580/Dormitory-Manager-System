import client from './client'

export const auth = {
  login: (username, password) =>
    client.post('/api/auth/login', { username, password }),
  studentLogin: (studentId, name) =>
    client.post('/api/auth/student-login', { studentId, name }),
  workerLogin: (username, password) =>
    client.post('/api/auth/worker-login', { username, password }),
}

export const students = {
  list: (keyword) =>
    client.get(`/api/students${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`),
  unassigned: () => client.get('/api/students/unassigned'),
  get: (id) => client.get(`/api/students/${id}`),
  getByStudentId: (studentId) => client.get(`/api/students/by-student-id/${encodeURIComponent(studentId)}`),
  create: (data) => client.post('/api/students', data),
  update: (id, data) => client.put(`/api/students/${id}`, data),
  delete: (id) => client.delete(`/api/students/${id}`),
  allocate: (studentId, roomId) =>
    client.post('/api/students/allocate', { studentId, roomId }),
  deallocate: (id) => client.post(`/api/students/${id}/deallocate`),
}

export const dormitories = {
  list: () => client.get('/api/dormitories'),
  get: (id) => client.get(`/api/dormitories/${id}`),
  create: (data) => client.post('/api/dormitories', data),
  update: (id, data) => client.put(`/api/dormitories/${id}`, data),
  delete: (id) => client.delete(`/api/dormitories/${id}`),
}

export const rooms = {
  list: (dormitory, status) => {
    const params = new URLSearchParams()
    if (dormitory) params.set('dormitory', dormitory)
    if (status) params.set('status', status)
    const q = params.toString()
    return client.get(`/api/rooms${q ? `?${q}` : ''}`)
  },
  available: (gender) =>
    client.get(`/api/rooms/available${gender ? `?gender=${encodeURIComponent(gender)}` : ''}`),
  create: (data) => client.post('/api/rooms', data),
}

export const stats = {
  get: () => client.get('/api/stats'),
}

export const repairs = {
  list: (status, keyword) => {
    const params = new URLSearchParams()
    if (status) params.set('status', status)
    if (keyword) params.set('keyword', keyword)
    const q = params.toString()
    return client.get(`/api/repairs${q ? `?${q}` : ''}`)
  },
  get: (id) => client.get(`/api/repairs/${id}`),
  create: (data) => client.post('/api/repairs', data),
  update: (id, data) => client.put(`/api/repairs/${id}`, data),
  complete: (id) => client.post(`/api/repairs/${id}/complete`),
  delete: (id) => client.delete(`/api/repairs/${id}`),
}

export const studentPortal = {
  profile: (studentId) => client.get(`/api/student/profile/${encodeURIComponent(studentId)}`),
  repairs: (studentId, status) => {
    const params = new URLSearchParams()
    if (status) params.set('status', status)
    const q = params.toString()
    return client.get(`/api/student/repairs/${encodeURIComponent(studentId)}${q ? `?${q}` : ''}`)
  },
  submitRepair: (data) => client.post('/api/student/repairs', data),
}

export const workerPortal = {
  repairs: (status, keyword) => {
    const params = new URLSearchParams()
    if (status) params.set('status', status)
    if (keyword) params.set('keyword', keyword)
    const q = params.toString()
    return client.get(`/api/worker/repairs${q ? `?${q}` : ''}`)
  },
  accept: (id) => client.post(`/api/worker/repairs/${id}/accept`),
  complete: (id) => client.post(`/api/worker/repairs/${id}/complete`),
}
