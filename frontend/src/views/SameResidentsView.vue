<script setup>
import { ref, watch, onMounted } from 'vue';
import StarWarsService from '../service/StarWarsService';
import RandomName from '../service/RandomName'

const name = ref('Luke Skywalker');
const residents = ref([]);
const randomName = ref(RandomName.getRandom());

function getResidents(name) {
  StarWarsService.get(name).then((response) => (residents.value = response.data.data))
}

watch(name, async (newName) => {
  getResidents(newName);
});

onMounted(() => {
  getResidents(name.value)
})
</script>

<template>
  <div class="form-list">
    <div class="form-group">
      <label for="title">Enter name</label>
      <input
        @keyup="getResidents(name)"
        type="text"
        class="form-control"
        id="title"
        required
        v-model="name"
        name="title"
      />
      <p class="note">Try <button class="astext" @click="name = randomName">{{ randomName }}</button></p>
    </div>
    <h4>Residents:</h4>
    <li v-for="resident in residents" :key="resident">
      {{ resident.name }}
    </li>
  </div>
</template>

<style>
.form-list {
  width: 100%;
  font-size: 1.5rem;
  text-align: left;
  margin-top: 2rem;
}

.note {
  font-size: 0.8rem;
}

.astext {
    background:none;
    border:none;
    margin:0;
    padding:0;
}
</style>
