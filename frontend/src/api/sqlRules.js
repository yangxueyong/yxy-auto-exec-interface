// api/sqlRules.js
import api from './index'

export const sqlRulesApi = {
    list: () => api.get('/sql-rules'),
    get: (id) => api.get(`/sql-rules/${id}`),
    create: (data) => api.post('/sql-rules', data),
    update: (data) => api.put('/sql-rules', data),
    delete: (id) => api.delete(`/sql-rules/${id}`)
}