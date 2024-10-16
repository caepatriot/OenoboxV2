<script setup>
import { RouterLink, RouterView } from 'vue-router'

</script>


<template>
  <v-app>
      <header>
        <div class="wrapper">
          <nav>
            <!--        <RouterLink to="/">Home</RouterLink>-->
            <!--        <RouterLink to="/about">About</RouterLink>-->
          </nav>
        </div>
      </header>
      <main>
        <RouterView />
      </main>
  </v-app>
</template>



<style scoped>
main {
  height: 100%;
}
</style>
