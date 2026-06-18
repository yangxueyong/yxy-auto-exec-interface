// frontend/src/store/index.js
import { defineStore } from 'pinia'

export const useMainStore = defineStore('main', {
    state: () => ({
        // 全局状态
        user: null,
        loading: false
    }),
    getters: {
        // 计算属性
        isLoggedIn: (state) => !!state.user
    },
    actions: {
        // 方法
        setUser(user) {
            this.user = user
        },
        setLoading(loading) {
            this.loading = loading
        }
    }
})