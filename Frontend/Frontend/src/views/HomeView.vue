<script setup>
import { RouterLink } from 'vue-router'
import TheWelcome from '../components/TheWelcome.vue'
</script>

<template>
  <div class="home-background">
    <v-container fluid class="home-container">
      <!-- Navigation Header -->
      <v-row class="mb-4">
        <v-col>
          <v-card class="nav-card" elevation="4">
            <v-card-title class="d-flex align-center justify-center">
              <h1 class="text-h4 mb-0 wine-title">Oenobox</h1>
            </v-card-title>
            <v-card-text class="pa-4">
              <v-row class="nav-row">
                <v-col cols="12" sm="4" class="nav-col">
                  <RouterLink to="/tasting" class="nav-link">
                    <v-card variant="outlined" class="nav-item-card" hover>
                      <v-card-text class="text-center pa-4">
                        <v-icon size="48" color="purple" class="mb-3">mdi-glass-wine</v-icon>
                        <h3 class="text-h6 mb-2">Dégustation</h3>
                        <p class="text-body-2 text-medium-emphasis mb-0">
                          Enregistrez et consultez vos notes de dégustation
                        </p>
                      </v-card-text>
                    </v-card>
                  </RouterLink>
                </v-col>

                <v-col cols="12" sm="4" class="nav-col">
                  <RouterLink to="/cave-admin" class="nav-link">
                    <v-card variant="outlined" class="nav-item-card" hover>
                      <v-card-text class="text-center pa-4">
                        <v-icon size="48" color="brown" class="mb-3">mdi-storefront</v-icon>
                        <h3 class="text-h6 mb-2">Administration Cave</h3>
                        <p class="text-body-2 text-medium-emphasis mb-0">
                          Créez et gérez la disposition de votre cave
                        </p>
                      </v-card-text>
                    </v-card>
                  </RouterLink>
                </v-col>

                <v-col cols="12" sm="4" class="nav-col">
                  <RouterLink to="/cave-placement" class="nav-link">
                    <v-card variant="outlined" class="nav-item-card" hover>
                      <v-card-text class="text-center pa-4">
                        <v-icon size="48" color="green" class="mb-3">mdi-bottle-wine</v-icon>
                        <h3 class="text-h6 mb-2">Placement Bouteilles</h3>
                        <p class="text-body-2 text-medium-emphasis mb-0">
                          Ajoutez et organisez vos bouteilles dans les espaces
                        </p>
                      </v-card-text>
                    </v-card>
                  </RouterLink>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Welcome Content -->
      <v-row>
        <v-col>
          <TheWelcome />
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
.home-background {
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  min-height: 100vh;
  padding: 16px;
}

.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.nav-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(139, 69, 19, 0.1);
}

.wine-title {
  background: linear-gradient(45deg, #8b4513, #a0522d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
}

.nav-row {
  margin: 0;
}

.nav-col {
  padding: 8px;
}

.nav-link {
  text-decoration: none;
  display: block;
  height: 100%;
}

.nav-item-card {
  height: 100%;
  border-radius: 8px;
  transition: all 0.3s ease;
  border-color: rgba(139, 69, 19, 0.2);
}

.nav-item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(139, 69, 19, 0.15);
  border-color: rgba(139, 69, 19, 0.4);
}

/* Responsive Design */
@media (max-width: 600px) {
  .home-background {
    padding: 8px;
  }

  .nav-col {
    padding: 4px;
  }

  .nav-item-card {
    margin-bottom: 8px;
  }
}
</style>
