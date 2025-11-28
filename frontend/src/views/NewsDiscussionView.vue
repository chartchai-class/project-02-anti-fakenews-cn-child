<template>
  <div class="container mx-auto px-4 py-8 max-w-7xl" v-if="news">
    <!-- News Header (Brief) -->
    <div class="bg-white rounded-2xl shadow-md overflow-hidden mb-8 p-6 flex justify-between items-center">
        <div>
            <h1 class="text-2xl font-bold text-gray-800 font-serif mb-2">Discussion: {{ news.topic }}</h1>
            <router-link :to="'/news/' + news.id" class="text-indigo-600 hover:text-indigo-800 text-sm font-medium">← Back to Article</router-link>
        </div>
        <span :class="{'bg-red-100 text-red-800': news.fakeStatus === 'FAKE', 'bg-green-100 text-green-800': news.fakeStatus === 'NOT_FAKE', 'bg-gray-100 text-gray-800': news.fakeStatus === 'UNKNOWN'}" class="px-3 py-1 rounded-full text-sm font-semibold">
            {{ news.fakeStatus }}
        </span>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Left Column: Vote Form & Pie Chart -->
        <div class="lg:col-span-1 space-y-8">
            <!-- Pie Chart Section -->
            <div class="bg-white rounded-2xl shadow-lg p-6 border-t-4 border-indigo-500">
                <h3 class="text-lg font-bold text-gray-800 mb-4">Community Verdict</h3>
                <div class="h-48 relative">
                    <Pie v-if="chartData" :data="chartData" :options="chartOptions" />
                    <div v-else class="flex items-center justify-center h-full text-gray-400">
                        No votes yet
                    </div>
                </div>
            </div>

            <!-- Vote Section -->
            <div id="cast-vote" class="bg-white rounded-2xl shadow-lg p-6 border-t-4 border-purple-500 scroll-mt-8">
              <h2 class="text-xl font-bold mb-4 text-gray-800 flex items-center">
                  <span class="bg-purple-100 text-purple-600 p-2 rounded-lg mr-3">
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                  </span>
                  Cast Your Vote
              </h2>
              
              <div v-if="authStore.isAuthenticated && (authStore.isMember || authStore.isAdmin)">
                <form @submit.prevent="submitVote" class="space-y-4">
                  <div class="flex space-x-4 justify-center">
                    <label class="cursor-pointer transform transition-transform hover:scale-105">
                      <input type="radio" :value="true" v-model="vote.isFake" class="hidden peer">
                      <div class="w-24 h-24 rounded-xl border-2 border-gray-200 peer-checked:border-red-500 peer-checked:bg-red-50 flex flex-col items-center justify-center text-gray-400 peer-checked:text-red-600 transition-all">
                          <span class="font-bold">Fake</span>
                      </div>
                    </label>
                    <label class="cursor-pointer transform transition-transform hover:scale-105">
                      <input type="radio" :value="false" v-model="vote.isFake" class="hidden peer">
                      <div class="w-24 h-24 rounded-xl border-2 border-gray-200 peer-checked:border-green-500 peer-checked:bg-green-50 flex flex-col items-center justify-center text-gray-400 peer-checked:text-green-600 transition-all">
                          <span class="font-bold">Real</span>
                      </div>
                    </label>
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Your Comment</label>
                    <textarea v-model="vote.comment" rows="2" class="w-full border-gray-300 rounded-lg shadow-sm focus:ring-purple-500 focus:border-purple-500 text-sm" placeholder="Why?"></textarea>
                  </div>
                  
                  <div>
                     <label class="block text-sm font-medium text-gray-700 mb-1">Evidence (Optional)</label>
                     <input type="file" @change="handleFileUpload" class="block w-full text-xs text-gray-500 file:mr-2 file:py-1 file:px-2 file:rounded-full file:border-0 file:text-xs file:font-semibold file:bg-purple-50 file:text-purple-700 hover:file:bg-purple-100"/>
                  </div>

                  <button type="submit" class="w-full bg-purple-600 text-white py-2 rounded-lg font-bold hover:bg-purple-700 transition-colors shadow-md text-sm">
                    Submit Vote
                  </button>
                </form>
              </div>
              <div v-else-if="!authStore.isAuthenticated" class="text-center py-6 bg-gray-50 rounded-xl">
                <p class="text-gray-600 mb-2 text-sm">Please login to vote.</p>
                <router-link to="/login" class="inline-block px-4 py-1.5 bg-blue-600 text-white rounded-full hover:bg-blue-700 transition-colors text-sm">Login</router-link>
              </div>
              <div v-else class="text-center py-6 bg-gray-50 rounded-xl">
                 <p class="text-gray-600 text-sm">Voting restricted to Members.</p>
              </div>
            </div>
        </div>

        <!-- Right Column: Comments List -->
        <div class="lg:col-span-2">
            <div id="discussion" class="bg-white rounded-2xl shadow-lg p-8 border-t-4 border-pink-500 h-full scroll-mt-8">
              <h2 class="text-2xl font-bold mb-6 text-gray-800 flex items-center">
                  <span class="bg-pink-100 text-pink-600 p-2 rounded-lg mr-3">
                      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"></path></svg>
                  </span>
                  Community Discussion ({{ votes.length }})
              </h2>
              
              <div class="space-y-6 max-h-[800px] overflow-y-auto pr-2 custom-scrollbar">
                <div v-for="v in paginatedVotes" :key="v.id" class="bg-gray-50 p-6 rounded-xl shadow-sm hover:shadow-md transition-shadow">
                  <div class="flex justify-between items-start mb-3">
                    <div class="flex items-center">
                        <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center text-white font-bold text-lg mr-3">
                            {{ v.user ? v.user.username.charAt(0).toUpperCase() : '?' }}
                        </div>
                        <div>
                            <span class="font-bold text-gray-800 block">{{ v.user ? v.user.username : 'Anonymous' }}</span>
                            <span :class="v.isFake ? 'text-red-600' : 'text-green-600'" class="text-xs font-bold uppercase tracking-wide">
                                Voted: {{ v.isFake ? 'Fake' : 'Real' }}
                            </span>
                        </div>
                    </div>
                    <button v-if="authStore.isAdmin" @click="deleteVote(v.id)" class="text-gray-400 hover:text-red-500 transition-colors">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                    </button>
                  </div>
                  <p class="text-gray-700 leading-relaxed">{{ v.comment }}</p>
                  <img v-if="v.image" :src="v.image" class="mt-4 rounded-lg max-h-48 object-cover shadow-sm" />
                </div>
                <div v-if="votes.length === 0" class="text-center text-gray-500 py-8 italic">
                    No comments yet. Be the first to share your thoughts!
                </div>
              </div>

              <!-- Pagination Controls -->
              <div class="mt-6 flex flex-col sm:flex-row justify-center items-center space-y-4 sm:space-y-0 sm:space-x-6">
                  <!-- Page Size Selector -->
                  <div class="flex items-center space-x-2">
                      <span class="text-sm text-gray-600">Show:</span>
                      <select v-model="itemsPerPage" @change="currentPage = 1" class="text-sm border-gray-300 rounded-md shadow-sm focus:border-indigo-500 focus:ring-indigo-500 bg-gray-50 py-1 pl-2 pr-8">
                          <option :value="5">5</option>
                          <option :value="10">10</option>
                          <option :value="20">20</option>
                          <option :value="50">50</option>
                      </select>
                  </div>

                  <!-- Navigation -->
                  <div v-if="totalPages > 1" class="flex items-center space-x-2">
                      <button @click="currentPage--" :disabled="currentPage === 1" class="px-3 py-1 rounded-md bg-gray-100 text-gray-600 hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                          Previous
                      </button>
                      <span class="text-sm text-gray-600 font-medium">Page {{ currentPage }} of {{ totalPages }}</span>
                      <button @click="currentPage++" :disabled="currentPage === totalPages" class="px-3 py-1 rounded-md bg-gray-100 text-gray-600 hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                          Next
                      </button>
                  </div>
              </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '../stores/auth';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Pie } from 'vue-chartjs'

ChartJS.register(ArcElement, Tooltip, Legend)

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const news = ref(null);
const votes = ref([]);
const vote = ref({
  isFake: null,
  comment: '',
  image: null
});

// Pagination
const currentPage = ref(1);
const itemsPerPage = ref(5);

const paginatedVotes = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    const end = start + itemsPerPage.value;
    return votes.value.slice(start, end);
});

const totalPages = computed(() => Math.ceil(votes.value.length / itemsPerPage.value));

const chartData = computed(() => {
    if (votes.value.length === 0) return null;
    const fakeCount = votes.value.filter(v => v.isFake).length;
    const realCount = votes.value.filter(v => !v.isFake).length;
    return {
        labels: ['Fake', 'Real'],
        datasets: [
            {
                backgroundColor: ['#EF4444', '#10B981'],
                data: [fakeCount, realCount]
            }
        ]
    }
});

const chartOptions = {
    responsive: true,
    maintainAspectRatio: false
};

const fetchNews = async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_BACKEND_URL}/api/news/${route.params.id}`);
    news.value = res.data;
    votes.value = res.data.votes || [];
    
    // Handle hash scroll
    if (route.hash) {
        setTimeout(() => {
            const el = document.querySelector(route.hash);
            if (el) el.scrollIntoView({ behavior: 'smooth' });
        }, 500);
    }
  } catch (e) {
    console.error(e);
  }
};

const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    try {
        const res = await axios.post(`${import.meta.env.VITE_BACKEND_URL}/api/files/upload`, formData, {
            headers: { 
                'Content-Type': 'multipart/form-data'
            }
        });
        vote.value.image = res.data.url;
    } catch (e) {
        console.error("Upload failed", e);
    }
};
const submitVote = async () => {
    if (vote.value.isFake === null) {
        alert("Please select Fake or Real");
        return;
    }
    try {
        const token = authStore.token;
        await axios.post(`${import.meta.env.VITE_BACKEND_URL}/api/votes`, {
            newsId: route.params.id,
            isFake: vote.value.isFake,
            comment: vote.value.comment,
            image: vote.value.image
        }, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        // Refresh
        fetchNews();
        vote.value = { isFake: null, comment: '', image: null };
    } catch (e) {
        console.error(e);
        alert("Failed to vote");
    }
};

const deleteVote = async (id) => {
    if(!confirm("Delete this comment?")) return;
    try {
        const token = authStore.token;
        await axios.delete(`${import.meta.env.VITE_BACKEND_URL}/api/votes/${id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        fetchNews();
    } catch(e) {
        alert("Failed to delete vote");
    }
}

onMounted(fetchNews);
</script>
