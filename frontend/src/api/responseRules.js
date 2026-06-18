// api/responseRules.js
import api from './index'

export const responseRulesApi = {
    list: () => api.get('/response-rules'),
    get: (id) => api.get(`/response-rules/${id}`),
    create: (data) => api.post('/response-rules', data),
    update: (data) => api.put('/response-rules', data),
    delete: (id) => api.delete(`/response-rules/${id}`)
}