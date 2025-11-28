<template>
  <div class="container mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold mb-6 text-gray-800">User Management</h1>
    
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in users" :key="user.id">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div class="flex-shrink-0 h-10 w-10">
                  <img class="h-10 w-10 rounded-full object-cover" :src="user.profileImage || 'https://via.placeholder.com/40'" alt="" />
                </div>
                <div class="ml-4">
                  <div class="text-sm font-medium text-gray-900">{{ user.firstname }} {{ user.lastname }}</div>
                  <div class="text-sm text-gray-500">{{ user.username }} ({{ user.email }})</div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" 
                    :class="{
                      'bg-green-100 text-green-800': user.role === 'ROLE_ADMIN',
                      'bg-blue-100 text-blue-800': user.role === 'ROLE_MEMBER',
                      'bg-gray-100 text-gray-800': user.role === 'ROLE_READER'
                    }">
                {{ user.role }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
              <button v-if="user.role === 'ROLE_READER'" @click="promoteUser(user, 'ROLE_MEMBER')" class="text-indigo-600 hover:text-indigo-900 mr-4">Promote to Member</button>
              <button v-if="user.role === 'ROLE_MEMBER'" @click="promoteUser(user, 'ROLE_READER')" class="text-red-600 hover:text-red-900">Demote to Reader</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const users = ref([]);
const authStore = useAuthStore();

const fetchUsers = async () => {
    try {
        const res = await axios.get('http://localhost:8080/api/users', {
            headers: { 'Authorization': `Bearer ${authStore.token}` }
        });
        users.value = res.data;
    } catch (e) {
        console.error("Failed to fetch users", e);
    }
};

const promoteUser = async (user, newRole) => {
    if (!confirm(`Are you sure you want to change ${user.username}'s role to ${newRole}?`)) return;
    try {
        await axios.put(`http://localhost:8080/api/users/${user.id}/role`, newRole, {
            headers: { 
                'Authorization': `Bearer ${authStore.token}`,
                'Content-Type': 'text/plain'
            }
        });
        user.role = newRole; // Optimistic update
    } catch (e) {
        console.error("Failed to update role", e);
        alert("Failed to update role");
    }
};

onMounted(fetchUsers);
</script>
