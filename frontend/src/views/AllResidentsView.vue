<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import StarWarsService from '../service/StarWarsService'

const all = ref([])

onMounted(() => {
  StarWarsService.getAll().then((response) => (all.value = response.data.data))
})
</script>

<template>
  <li v-for="person in all" :key="person">
    <RouterLink :to="{ name: 'find', query: { toSearch: person.name } }">
      {{ person.name }}
    </RouterLink>
  </li>
</template>
