<template>
  <nav class="bg-white shadow-lg">
    <div class="max-w-7xl mx-auto px-4">
      <div class="flex justify-between h-16">
        <div class="flex">
          <router-link to="/" class="flex-shrink-0 flex items-center">
            <span class="text-xl font-bold text-indigo-600">Anti-Fake News</span>
          </router-link>
        </div>
        <div class="flex items-center space-x-4">
          <router-link to="/" class="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium">Home</router-link>
          
          <template v-if="authStore.isAuthenticated">
            <router-link v-if="authStore.isMember || authStore.isAdmin" to="/add-news" class="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium">Add News</router-link>
            <router-link v-if="authStore.isAdmin" to="/admin/users" class="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium">Manage Users</router-link>
            <span class="text-gray-500 text-sm">Welcome, {{ authStore.user?.username }} ({{ authStore.user?.role }})</span>
            <button @click="logout" class="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700">Logout</button>
          </template>
          
          <template v-else>
            <router-link to="/login" class="text-gray-700 hover:text-indigo-600 px-3 py-2 rounded-md text-sm font-medium">Login</router-link>
            <router-link to="/register" class="bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700">Register</router-link>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>
