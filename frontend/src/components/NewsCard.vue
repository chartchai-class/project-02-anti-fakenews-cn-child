<template>
  <div class="bg-white overflow-hidden shadow rounded-lg">
    <img v-if="news.image" :src="news.image" alt="News Image" class="w-full h-48 object-cover">
    <div class="px-4 py-5 sm:p-6">
      <div class="flex justify-between items-center mb-2">
        <span :class="getStatusClass(news.fakeStatus)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
          {{ news.fakeStatus }}
        </span>
        <span class="text-sm text-gray-500">{{ formatDate(news.createdTime) }}</span>
      </div>
      <h3 class="text-lg leading-6 font-medium text-gray-900 mb-2">
        <router-link :to="'/news/' + news.id" class="hover:underline">{{ news.topic }}</router-link>
      </h3>
      <p class="mt-1 max-w-2xl text-sm text-gray-500 truncate">
        {{ news.content }}
      </p>
      <div class="mt-4 flex items-center justify-between">
        <span class="text-sm text-gray-500">By {{ news.reporterName }}</span>
        <router-link :to="'/news/' + news.id" class="text-indigo-600 hover:text-indigo-900 text-sm font-medium">Read more</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  news: Object
})

const getStatusClass = (status) => {
  if (status === 'FAKE') return 'bg-red-100 text-red-800'
  if (status === 'NOT_FAKE') return 'bg-green-100 text-green-800'
  return 'bg-gray-100 text-gray-800'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString()
}
</script>
