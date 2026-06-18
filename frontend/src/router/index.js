import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/sql-rules'
        },
        {
            path: '/sql-rules',
            name: 'SqlRules',
            component: () => import('@/views/SqlRules.vue')
        },
        {
            path: '/param-rules',
            name: 'ParamRules',
            component: () => import('@/views/ParamRules.vue')
        },
        {
            path: '/response-rules',
            name: 'ResponseRules',
            component: () => import('@/views/ResponseRules.vue')
        },
        {
            path: '/api-definitions',
            name: 'ApiDefinitions',
            component: () => import('@/views/ApiDefinitions.vue')
        },
        {
            path: '/api-tests',
            name: 'ApiTests',
            component: () => import('@/views/ApiTests.vue')
        }
    ]
})

export default router