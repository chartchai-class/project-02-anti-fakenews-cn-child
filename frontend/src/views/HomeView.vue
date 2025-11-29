<template>
  <div>
    <div class="mb-6 flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
      <h1 class="text-3xl font-bold text-gray-900">Latest News</h1>
      
      <!-- Search and Filters -->
      <div class="flex flex-col md:flex-row space-y-2 md:space-y-0 md:space-x-4 items-center">
        <!-- Search Input -->
        <div class="relative">
            <input 
                v-model="searchKeyword" 
                @input="handleSearch"
                type="text" 
                placeholder="Search news..." 
                class="pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:ring-indigo-500 focus:border-indigo-500"
            >
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
            </div>
        </div>

        <!-- Page Size Selector -->
        <select v-model="pageSize" @change="fetchNews" class="border border-gray-300 rounded-md py-2 px-3 focus:ring-indigo-500 focus:border-indigo-500">
            <option :value="3">3 per page</option>
            <option :value="6">6 per page</option>
            <option :value="12">12 per page</option>
            <option :value="24">24 per page</option>
        </select>

        <!-- Status Filters -->
        <div class="flex space-x-2">
            <button @click="setFilter('ALL')" :class="filter === 'ALL' ? 'bg-indigo-600 text-white' : 'bg-white text-gray-700'" class="px-3 py-2 rounded-md text-sm font-medium shadow-sm border border-gray-300 transition-colors">All</button>
            <button @click="setFilter('FAKE')" :class="filter === 'FAKE' ? 'bg-red-600 text-white' : 'bg-white text-gray-700'" class="px-3 py-2 rounded-md text-sm font-medium shadow-sm border border-gray-300 transition-colors">Fake</button>
            <button @click="setFilter('NOT_FAKE')" :class="filter === 'NOT_FAKE' ? 'bg-green-600 text-white' : 'bg-white text-gray-700'" class="px-3 py-2 rounded-md text-sm font-medium shadow-sm border border-gray-300 transition-colors">Real</button>
            <button @click="setFilter('UNKNOWN')" :class="filter === 'UNKNOWN' ? 'bg-gray-600 text-white' : 'bg-white text-gray-700'" class="px-3 py-2 rounded-md text-sm font-medium shadow-sm border border-gray-300 transition-colors">Unknown</button>
            
            <!-- Admin Only: Show Deleted -->
            <button v-if="authStore.isAdmin" @click="toggleDeleted" :class="statusFilter === 'HIDDEN' ? 'bg-gray-800 text-white' : 'bg-white text-gray-700 border-dashed border-gray-400'" class="px-3 py-2 rounded-md text-sm font-medium shadow-sm border transition-colors ml-4">
                {{ statusFilter === 'HIDDEN' ? 'Showing Deleted' : 'Show Deleted' }}
            </button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="text-center py-12">
      <p class="text-gray-500">Loading news...</p>
    </div>

    <div v-else-if="newsList.length === 0" class="text-center py-12">
      <p class="text-gray-500">No news found.</p>
    </div>

    <div v-else class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
      <div v-for="news in newsList" :key="news.id" class="relative">
        <NewsCard :news="news" />
        <button v-if="authStore.isAdmin" @click="deleteNews(news.id)" class="absolute top-2 right-2 bg-red-600 text-white px-2 py-1 rounded text-xs hover:bg-red-700 z-10">Delete</button>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="mt-8 flex justify-center">
      <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
        <button @click="changePage(page - 1)" :disabled="page === 0" class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50">
          Previous
        </button>
        <span class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
          Page {{ page + 1 }} of {{ totalPages }}
        </span>
        <button @click="changePage(page + 1)" :disabled="page >= totalPages - 1" class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50">
          Next
        </button>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import NewsCard from '../components/NewsCard.vue'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const newsList = ref([])
const loading = ref(true)
const filter = ref('ALL')
const page = ref(0)
const totalPages = ref(0)
const pageSize = ref(6)
const searchKeyword = ref('')

// Simple debounce implementation
const debounce = (fn, delay) => {
  let timeoutId;
  return (...args) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => fn(...args), delay);
  };
};

const fetchNews = async () => {
  loading.value = true
  try {
    let url = `${import.meta.env.VITE_BACKEND_URL}/api/news?page=${page.value}&size=${pageSize.value}`
    
    if (searchKeyword.value) {
        url += `&keyword=${encodeURIComponent(searchKeyword.value)}`
    } else if (statusFilter.value) {
        url += `&status=${statusFilter.value}`
    } else if (filter.value !== 'ALL') {
      url += `&fakeStatus=${filter.value}`
    }
    
    const response = await axios.get(url)
    newsList.value = response.data.content
    totalPages.value = response.data.totalPages
  } catch (error) {
    console.error('Error fetching news:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = debounce(() => {
    page.value = 0;
    fetchNews();
}, 500);

const setFilter = (newFilter) => {
  filter.value = newFilter
  searchKeyword.value = '' // Clear search when filtering by status
  page.value = 0 // Reset to first page
  fetchNews()
}

const statusFilter = ref(null)

const toggleDeleted = () => {
    if (statusFilter.value === 'HIDDEN') {
        statusFilter.value = null
    } else {
        statusFilter.value = 'HIDDEN'
        filter.value = 'ALL' // Reset fake filter when showing deleted
    }
    page.value = 0
    fetchNews()
}

const changePage = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage
    fetchNews()
  }
}

const deleteNews = async (id) => {
  if (!confirm('Are you sure you want to delete this news?')) return

  try {
    await axios.delete(`${import.meta.env.VITE_BACKEND_URL}/api/news/${id}`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    fetchNews()
  } catch (error) {
    console.error('Error deleting news:', error)
    alert('Failed to delete news')
  }
}

onMounted(() => {
  fetchNews()
})
</script>
