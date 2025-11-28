<template>
  <div class="container mx-auto px-4 py-8 max-w-2xl">
    <h1 class="text-3xl font-bold mb-6 text-gray-800">Post News</h1>
    <div class="bg-white p-8 rounded-xl shadow-md">
      <form @submit.prevent="submitNews" class="space-y-6">
        
        <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Topic</label>
            <input v-model="news.topic" type="text" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="Enter news topic">
        </div>

        <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Content</label>
            <textarea v-model="news.content" rows="6" required class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="Write the news content..."></textarea>
        </div>

        <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Image URL</label>
            <input v-model="news.image" type="text" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="https://example.com/image.jpg">
            <p class="text-xs text-gray-500 mt-1">Or upload an image (not implemented yet for news creation, using URL for now)</p>
        </div>

        <div v-if="error" class="text-red-500 text-sm">{{ error }}</div>

        <button type="submit" class="w-full bg-indigo-600 text-white py-3 rounded-lg font-bold hover:bg-indigo-700 transition-colors shadow-md">
            Publish News
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const news = ref({
  topic: '',
  content: '',
  image: 'https://picsum.photos/800/600' // Default random image
});
const error = ref('');
const router = useRouter();
const authStore = useAuthStore();

const submitNews = async () => {
  try {
    const token = authStore.token;
    await axios.post(`/api/news`, news.value, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    router.push('/');
  } catch (e) {
    console.error(e);
    error.value = 'Failed to create news';
  }
};
</script>
