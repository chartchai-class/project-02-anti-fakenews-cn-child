import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import NewsDetailView from '../views/NewsDetailView.vue'
import NewsDiscussionView from '../views/NewsDiscussionView.vue'
import AddNewsView from '../views/AddNewsView.vue'
import AdminUserView from '../views/AdminUserView.vue'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterView
        },
        {
            path: '/news/:id',
            name: 'news-detail',
            component: NewsDetailView
        },
        {
            path: '/news/:id/discussion',
            name: 'news-discussion',
            component: NewsDiscussionView
        },
        {
            path: '/add-news',
            name: 'add-news',
            component: AddNewsView,
            meta: { requiresAuth: true }
        },
        {
            path: '/admin/users',
            name: 'admin-users',
            component: AdminUserView,
            meta: { requiresAuth: true }
        }
    ]
})

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next('/login')
    } else {
        next()
    }
})

export default router
