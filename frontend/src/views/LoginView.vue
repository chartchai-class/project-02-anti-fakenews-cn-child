<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-md">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">Sign in to your account</h2>
      </div>
      <form class="mt-8 space-y-6" @submit.prevent="handleLogin">
        
        <FloatingInput v-model="username" name="username">Username</FloatingInput>
        <FloatingInput v-model="password" type="password" name="password">Password</FloatingInput>

        <div v-if="error" class="text-red-500 text-sm text-center">{{ error }}</div>

        <div>
          <button
            type="submit"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Sign in
          </button>
        </div>
        
        <div class="text-center mt-4">
            <router-link to="/register" class="text-indigo-600 hover:text-indigo-500 text-sm">Don't have an account? Register</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import FloatingInput from '../components/FloatingInput.vue';

const username = ref('');
const password = ref('');
const error = ref('');
const authStore = useAuthStore();
const router = useRouter();

const handleLogin = async () => {
  const success = await authStore.login({
    username: username.value,
    password: password.value
  });
  if (success) {
    router.push('/');
  } else {
    error.value = 'Invalid username or password';
  }
};
</script>
