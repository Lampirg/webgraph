<script setup>

import StarWarsService from "../service/StarWarsService";

defineProps({
  name: {
    type: String,
    required: true
  }
});
</script>

<template>
    <li v-for="(resident, index) in StarWarsService.get(name)" :key="index">
        {{ resident.name }}
    </li>
</template>