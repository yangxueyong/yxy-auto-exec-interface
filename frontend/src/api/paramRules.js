// api/paramRules.js
import api from './index'

export const paramRulesApi = {
    list: () => api.get('/param-rules'),
    get: (id) => api.get(`/param-rules/${id}`),
    create: (data) => api.post('/param-rules', data),
    update: (data) => api.put('/param-rules', data),
    delete: (id) => api.delete(`/param-rules/${id}`)
}