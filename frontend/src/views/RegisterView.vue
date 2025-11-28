<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-md">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">Create your account</h2>
      </div>
      <form class="mt-8 space-y-6" @submit.prevent="register">
        


        <div v-if="error" class="text-red-500 text-sm text-center">{{ error }}</div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label for="firstname" class="block text-sm font-medium text-gray-700">First Name</label>
            <input type="text" id="firstname" v-model="form.firstname" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
          </div>
          <div>
            <label for="lastname" class="block text-sm font-medium text-gray-700">Last Name</label>
            <input type="text" id="lastname" v-model="form.lastname" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
          </div>
        </div>

        <div>
          <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
          <input type="text" id="username" v-model="form.username" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
        </div>

        <div>
          <label for="email" class="block text-sm font-medium text-gray-700">Email address</label>
          <input type="email" id="email" v-model="form.email" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
        </div>

        <div>
          <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
          <input type="password" id="password" v-model="form.password" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
        </div>

        <div>
          <label for="confirmPassword" class="block text-sm font-medium text-gray-700">Confirm Password</label>
          <input type="password" id="confirmPassword" v-model="form.confirmPassword" required class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500">
        </div>

        <div>
           <label class="block text-sm font-medium text-gray-700">Profile Image</label>
           <input type="file" @change="handleFileUpload" class="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"/>
        </div>

        <div>
          <button
            type="submit"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Register
          </button>
        </div>
        
        <div class="text-center mt-4">
            <router-link to="/login" class="text-indigo-600 hover:text-indigo-500 text-sm">Already have an account? Login</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const role = ref('ROLE_READER');
const error = ref('');
const router = useRouter();

const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  firstname: '',
  lastname: '',
  profileImage: ''
});

const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    try {
        const res = await axios.post('http://localhost:8080/api/files/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        form.value.profileImage = res.data.url;
    } catch (e) {
        console.error("Upload failed", e);
        error.value = 'Profile image upload failed.';
    }
};

const register = async () => {
  if (form.value.password !== form.value.confirmPassword) {
    error.value = "Passwords do not match!";
    return;
  }
  try {
    await axios.post('http://localhost:8080/api/auth/register', {
        username: form.value.username,
        password: form.value.password,
        email: form.value.email,
        firstname: form.value.firstname,
        lastname: form.value.lastname,
        role: role.value,
        profileImage: form.value.profileImage
    });
    alert("Registration successful! Please login.");
    router.push('/login');
  } catch (err) {
    console.error(err);
    error.value = "Registration failed";
  }
};
</script>
