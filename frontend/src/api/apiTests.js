// api/apiTests.js
import api from './index'

export const apiTestsApi = {
    list: () => api.get('/api-tests'),
    get: (id) => api.get(`/api-tests/${id}`),
    create: (data) => api.post('/api-tests', data),
    startTest: (id) => api.post(`/api-tests/${id}/start`),
    getResult: (id) => api.get(`/api-tests/${id}/result`),
    delete: (id) => api.delete(`/api-tests/${id}`)
}