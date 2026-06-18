import axios from 'axios'

const api = axios.create({
    baseURL: '/api',
    timeout: 30000
})

api.interceptors.response.use(
    response => {
        if (response.data.code === 200) {
            return response.data
        }
        return Promise.reject(response.data)
    },
    error => {
        return Promise.reject(error)
    }
)

export default api