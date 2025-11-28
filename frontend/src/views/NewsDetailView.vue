<template>
  <div class="container mx-auto px-4 py-8 max-w-7xl" v-if="news">
    <!-- News Header & Summary -->
    <div class="bg-white rounded-2xl shadow-xl overflow-hidden mb-8 transform transition-all hover:shadow-2xl">
      <img :src="news.image" alt="News Image" class="w-full h-96 object-cover" v-if="news.image" />
      <div class="p-8">
        <div class="flex justify-between items-center mb-4">
            <h1 class="text-4xl font-bold text-gray-800 font-serif">{{ news.topic }}</h1>
            <span :class="{'bg-red-100 text-red-800': news.fakeStatus === 'FAKE', 'bg-green-100 text-green-800': news.fakeStatus === 'NOT_FAKE', 'bg-gray-100 text-gray-800': news.fakeStatus === 'UNKNOWN'}" class="px-3 py-1 rounded-full text-sm font-semibold">
                {{ news.fakeStatus }}
            </span>
        </div>
        <p class="text-gray-500 text-sm mb-6 flex items-center">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            {{ new Date(news.createdTime).toLocaleString() }}
            <span class="mx-2">•</span>
            <span class="font-medium text-blue-600">By {{ news.reporter ? news.reporter.username : 'Unknown' }}</span>
        </p>
        
        <!-- Summary / Content -->
        <div class="prose max-w-none text-gray-700 leading-relaxed text-lg">
          {{ news.content }}
        </div>

        <!-- AI Analysis Section -->
        <div class="mt-6 bg-gray-50 rounded-lg p-4 border border-gray-200">
            <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center">
                <svg class="w-5 h-5 mr-2 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
                AI Analysis Report
            </h3>
            <div v-if="analysis" class="space-y-2 text-sm">
                <div class="flex items-center">
                    <span class="font-semibold text-gray-700 w-20">Verdict:</span>
                    <span :class="analysis.verdict === 'Real' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'" class="px-2 py-0.5 rounded-full text-xs font-bold">
                        {{ analysis.verdict }}
                    </span>
                </div>
                <div class="flex items-start">
                    <span class="font-semibold text-gray-700 w-20 mt-0.5">Reasoning:</span>
                    <p class="text-gray-600 flex-1">{{ analysis.reasoning }}</p>
                </div>
                <div class="flex items-center gap-3">
                    <span class="font-semibold text-gray-700">Confidence:</span>
                    <div class="flex-1 h-1.5 bg-gray-200 rounded-full overflow-hidden max-w-xs">
                        <div class="h-full bg-indigo-500" :style="{ width: analysis.confidence + '%' }"></div>
                    </div>
                    <span class="text-xs text-gray-500">{{ analysis.confidence }}%</span>
                </div>
            </div>
            <div v-else class="text-gray-500 italic text-sm">
                Analyzing content...
            </div>
        </div>

        <!-- Action Buttons -->
        <div class="mt-10 flex justify-center space-x-6">
            <router-link v-if="authStore.isMember || authStore.isAdmin" :to="{ path: '/news/' + news.id + '/discussion', hash: '#cast-vote' }" class="flex items-center px-8 py-4 bg-gradient-to-r from-purple-500 to-indigo-600 text-white rounded-full font-bold text-lg shadow-lg hover:from-purple-600 hover:to-indigo-700 transform hover:-translate-y-1 transition-all">
                <svg class="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                Vote Now
            </router-link>
            <router-link :to="{ path: '/news/' + news.id + '/discussion', hash: '#discussion' }" class="flex items-center px-8 py-4 bg-gradient-to-r from-pink-500 to-rose-600 text-white rounded-full font-bold text-lg shadow-lg hover:from-pink-600 hover:to-rose-700 transform hover:-translate-y-1 transition-all">
                <svg class="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"></path></svg>
                View Comments
            </router-link>
        </div>
        
        <div class="mt-6 text-right" v-if="authStore.isAdmin">
             <button @click="deleteNews" class="text-red-500 hover:text-red-700 font-semibold text-sm uppercase tracking-wider">Delete News</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { useAuthStore } from '../stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const news = ref(null);
const analysis = ref(null);

const analyzeNews = (text) => {
    // Simple mock AI analysis based on keywords
    const fakeKeywords = ['shocking', 'miracle', 'secret', 'banned', 'won\'t tell you', '100%'];
    const realKeywords = ['report', 'study', 'analysis', 'official', 'statement', 'according to'];
    
    let fakeScore = 0;
    let realScore = 0;
    
    const lowerText = text.toLowerCase();
    
    fakeKeywords.forEach(k => { if(lowerText.includes(k)) fakeScore++; });
    realKeywords.forEach(k => { if(lowerText.includes(k)) realScore++; });
    
    // Random factor for demo purposes if no keywords found
    if (fakeScore === 0 && realScore === 0) {
        return Math.random() > 0.5 ? 
            { verdict: 'Real', reasoning: 'The content structure and vocabulary align with standard journalistic practices. No obvious sensationalism detected.', confidence: 75 } :
            { verdict: 'Fake', reasoning: 'The article lacks verifiable sources and uses somewhat emotive language typical of clickbait.', confidence: 60 };
    }
    
    if (fakeScore >= realScore) {
        return { 
            verdict: 'Fake', 
            reasoning: `Detected usage of sensationalist keywords (${fakeScore} matches) often associated with misinformation.`, 
            confidence: 60 + (fakeScore * 10) 
        };
    } else {
        return { 
            verdict: 'Real', 
            reasoning: `Contains terminology consistent with verified reporting (${realScore} matches).`, 
            confidence: 60 + (realScore * 10) 
        };
    }
};

const fetchNews = async () => {
  try {
    const res = await axios.get(`${import.meta.env.VITE_BACKEND_URL}/api/news/${route.params.id}`);
    news.value = res.data;
    // Trigger analysis
    setTimeout(() => {
        analysis.value = analyzeNews(news.value.content + " " + news.value.topic);
    }, 800); // Simulate processing delay
  } catch (e) {
    console.error(e);
  }
};

const deleteNews = async () => {
    if(!confirm("Are you sure?")) return;
    try {
        const token = authStore.token;
        await axios.delete(`${import.meta.env.VITE_BACKEND_URL}/api/news/${news.value.id}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        router.push('/');
    } catch(e) {
        alert("Failed to delete");
    }
}

onMounted(fetchNews);
</script>
