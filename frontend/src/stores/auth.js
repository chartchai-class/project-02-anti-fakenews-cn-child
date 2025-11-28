import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: JSON.parse(localStorage.getItem('user')) || null,
        token: localStorage.getItem('token') || null
    }),
    getters: {
        isAuthenticated: (state) => !!state.token,
        isMember: (state) => state.user?.role === 'ROLE_MEMBER' || state.user?.role === 'ROLE_ADMIN',
        isAdmin: (state) => state.user?.role === 'ROLE_ADMIN'
    },
    actions: {
        async login(credentials) {
            try {
                const response = await axios.post(`/api/auth/login`, credentials)
                this.token = response.data.accessToken
                localStorage.setItem('token', this.token)

                // Decode token to get username and role
                const payload = JSON.parse(atob(this.token.split('.')[1]));
                this.user = { username: payload.sub, role: payload.role };
                localStorage.setItem('user', JSON.stringify(this.user))

                return true
            } catch (error) {
                console.error('Login failed', error)
                return false
            }
        },
        async register(userData) {
            try {
                await axios.post(`/api/auth/register`, userData)
                return true
            } catch (error) {
                console.error('Registration failed', error)
                return false
            }
        },
        logout() {
            this.user = null
            this.token = null
            localStorage.removeItem('user')
            localStorage.removeItem('token')
        }
    }
})
