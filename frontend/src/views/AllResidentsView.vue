<script setup>
import { ref, onMounted } from 'vue';
import StarWarsService from '../service/StarWarsService';

const all = ref([]);

onMounted(() => {
    StarWarsService.getAll().then((response) => all.value = response.data.data);
});
</script>

<template>
    <li v-for="person in all" :key="person">
      {{ person.name }}
    </li>
</template>