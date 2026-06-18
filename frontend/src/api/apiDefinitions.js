// api/apiDefinitions.js
import api from './index'

export const apiDefinitionsApi = {
    list: () => api.get('/api-definitions'),
    get: (id) => api.get(`/api-definitions/${id}`),
    create: (data) => api.post('/api-definitions', data),
    update: (data) => api.put('/api-definitions', data),
    delete: (id) => api.delete(`/api-definitions/${id}`)
}