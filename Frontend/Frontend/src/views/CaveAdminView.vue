<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed, watch, nextTick } from "vue";
import { useCaveStore } from "@/stores/cave.js";

const caveStore = useCaveStore();

// ===== Zoom / fit-to-viewport
const zoom = ref(1);
const topFitScale = ref(1);
const wallFitScale = ref(1);

// Viewport refs
const overviewViewportRef = ref(null);
const wallViewportRef = ref(null);

// ===== Constants / refs
const canvasRef = ref(null);

// Wall (front view) refs
const wallCanvasRef = ref(null);
const selectedWall = ref("north");

const wallItems = [
  { value: "north", title: "Mur Nord" },
  { value: "south", title: "Mur Sud" },
  { value: "east", title: "Mur Est" },
  { value: "west", title: "Mur Ouest" },
];

// UI state
const showCreateCaveDialog = ref(false);
const showEditCaveDialog = ref(false);
const showCreateUnitDialog = ref(false);
const showEditUnitDialog = ref(false);
const selectedView = ref("overview");
const editingCave = ref(null);
const editingUnit = ref(null);
const editMode = ref(false);
const draggingUnit = ref(null);
const dragOffset = ref({ x: 0, y: 0 });

// Resize state (top-down)
const resizingUnit = ref(null);
const resizeDir = ref(null);
const resizeStart = ref({
  mouseX: 0,
  mouseY: 0,
  x: 0,
  y: 0,
  w: 0,
  h: 0,
});

const MIN_UNIT_W = 40;
const MIN_UNIT_H = 40;

// Wall elevation drag state
const elevatingUnit = ref(null);
const elevateStart = ref({
  mouseY: 0,
  elevation: 0,
});

// Form data
const newCaveData = reactive({
  name: "",
  description: "",
  dimensions: { width: 400, height: 300, depth: 200 },
  temperature: 12,
  humidity: 70,
});

const editCaveData = reactive({
  name: "",
  description: "",
  dimensions: { width: 400, height: 300, depth: 200 },
  temperature: 12,
  humidity: 70,
});

const newUnitData = reactive({
  name: "",
  type: "rack",
  position: { x: 50, y: 50 },
  dimensions: { width: 120, height: 180, depth: 30 },
  orientation: "vertical",
  wall: "north",
  elevation: 0,
});

// Unit types
const unitTypes = [
  { value: "rack", title: "Rack à vin" },
  { value: "shelf", title: "Étagère" },
  { value: "cabinet", title: "Armoire" },
  { value: "wall-mounted", title: "Fixation murale" },
  { value: "floor-standing", title: "Support au sol" },
];

// Load data on mount
onMounted(async () => {
  try {
    await caveStore.loadCaves();
  } catch (error) {
    console.error("Failed to load caves:", error);
  }
});

// Computed properties
const selectedCave = computed(() => caveStore.selectedCave);
const selectedUnit = computed(() => caveStore.selectedUnit);

const wallHeightCm = computed(() => {
  const c = selectedCave.value;
  return Number(c?.dimensions?.height ?? 300);
});

const wallLengthCm = computed(() => {
  const c = selectedCave.value;
  if (!c) return 0;
  const w = Number(c.dimensions.width ?? 0);
  const d = Number(c.dimensions.depth ?? 0);
  return selectedWall.value === "north" || selectedWall.value === "south" ? w : d;
});

const unitsOnSelectedWall = computed(() => {
  const c = selectedCave.value;
  if (!c?.units) return [];
  return c.units.filter((u) => (u.wall ?? "north") === selectedWall.value);
});

// Final scale used everywhere (fit-to-viewport * zoom)
const scale = computed(() => {
  const base = selectedView.value === "walls" ? wallFitScale.value : topFitScale.value;
  const s = base * zoom.value;
  return Math.max(0.15, Math.min(2.5, s));
});

// ===== Fit-to-viewport calculations
const computeTopFit = () => {
  const el = overviewViewportRef.value;
  const c = selectedCave.value;
  if (!el || !c) return;

  const pad = 24;
  const availW = Math.max(1, el.clientWidth - pad * 2);
  const availH = Math.max(1, el.clientHeight - pad * 2);

  const caveW = Math.max(1, Number(c.dimensions.width ?? 1));
  const caveD = Math.max(1, Number(c.dimensions.depth ?? 1));

  const fit = Math.min(availW / caveW, availH / caveD);
  topFitScale.value = Number.isFinite(fit) && fit > 0 ? fit : 1;
};

const computeWallFit = () => {
  const el = wallViewportRef.value;
  if (!el) return;

  const pad = 24;
  const availW = Math.max(1, el.clientWidth - pad * 2);
  const availH = Math.max(1, el.clientHeight - pad * 2);

  const W = Math.max(1, Number(wallLengthCm.value ?? 1));
  const H = Math.max(1, Number(wallHeightCm.value ?? 1));

  const fit = Math.min(availW / W, availH / H);
  wallFitScale.value = Number.isFinite(fit) && fit > 0 ? fit : 1;
};

let roTop = null;
let roWall = null;

const attachObservers = async () => {
  await nextTick();

  roTop?.disconnect();
  roWall?.disconnect();

  if (overviewViewportRef.value) {
    roTop = new ResizeObserver(() => computeTopFit());
    roTop.observe(overviewViewportRef.value);
    computeTopFit();
  }

  if (wallViewportRef.value) {
    roWall = new ResizeObserver(() => computeWallFit());
    roWall.observe(wallViewportRef.value);
    computeWallFit();
  }
};

watch([selectedCave, selectedView, selectedWall], attachObservers, { immediate: true });

// ===== CRUD (Caves)
const resetNewCaveData = () => {
  Object.assign(newCaveData, {
    name: "",
    description: "",
    dimensions: { width: 400, height: 300, depth: 200 },
    temperature: 12,
    humidity: 70,
  });
};

const resetEditCaveData = () => {
  Object.assign(editCaveData, {
    name: "",
    description: "",
    dimensions: { width: 400, height: 300, depth: 200 },
    temperature: 12,
    humidity: 70,
  });
};

const createCave = async () => {
  try {
    await caveStore.createCave({ ...newCaveData });
    showCreateCaveDialog.value = false;
    resetNewCaveData();
  } catch (error) {
    console.error("Failed to create cave:", error);
    alert("Erreur lors de la création de la cave");
  }
};

const openEditCave = (cave) => {
  editingCave.value = cave;
  Object.assign(editCaveData, {
    name: cave.name ?? "",
    description: cave.description ?? "",
    dimensions: {
      width: Number(cave.dimensions?.width ?? 400),
      height: Number(cave.dimensions?.height ?? 300),
      depth: Number(cave.dimensions?.depth ?? 200),
    },
    temperature: Number(cave.temperature ?? 12),
    humidity: Number(cave.humidity ?? 70),
  });
  showEditCaveDialog.value = true;
};

const updateCave = async () => {
  if (!editingCave.value) return;

  try {
    await caveStore.updateCave(editingCave.value.id, { ...editCaveData });
    showEditCaveDialog.value = false;
    editingCave.value = null;
    resetEditCaveData();
  } catch (error) {
    console.error("Failed to update cave:", error);
    alert("Erreur lors de la mise à jour de la cave");
  }
};

const deleteCave = async (cave) => {
  const name = cave?.name ?? "cette cave";
  if (!confirm(`Êtes-vous sûr de vouloir supprimer "${name}" ?\nLes emplacements et placements associés seront supprimés.`)) return;

  try {
    await caveStore.deleteCave(cave.id);
    selectedView.value = "overview";
  } catch (error) {
    console.error("Failed to delete cave:", error);
    alert("Erreur lors de la suppression de la cave");
  }
};

// ===== CRUD (Units)
const selectCave = (cave) => {
  caveStore.selectCave(cave);
  selectedView.value = "overview";
};

const selectUnit = (unit) => {
  caveStore.selectUnit(unit);
  selectedView.value = "detail";
};

const resetNewUnitData = () => {
  Object.assign(newUnitData, {
    name: "",
    type: "rack",
    position: { x: 50, y: 50 },
    dimensions: { width: 120, height: 180, depth: 30 },
    orientation: "vertical",
    wall: "north",
    elevation: 0,
  });
};

const createStorageUnit = async () => {
  if (!selectedCave.value) return;

  try {
    await caveStore.addStorageUnit(selectedCave.value.id, { ...newUnitData });
    showCreateUnitDialog.value = false;
    resetNewUnitData();
  } catch (error) {
    console.error("Failed to create storage unit:", error);
    alert("Erreur lors de la création de l'unité de stockage");
  }
};

const editStorageUnit = (unit) => {
  editingUnit.value = unit;
  Object.assign(newUnitData, {
    name: unit.name,
    type: unit.type,
    position: { ...unit.position },
    dimensions: { ...unit.dimensions },
    orientation: unit.orientation,
    wall: unit.wall ?? "north",
    elevation: Number(unit.elevation ?? 0),
  });
  showEditUnitDialog.value = true;
};

const updateStorageUnit = async () => {
  if (!selectedCave.value || !editingUnit.value) return;

  try {
    const updated = await caveStore.updateStorageUnit(
        selectedCave.value.id,
        editingUnit.value.id,
        { ...newUnitData }
    );

    if (selectedUnit.value?.id === updated?.id) {
      caveStore.selectUnit(updated);
    }

    showEditUnitDialog.value = false;
    editingUnit.value = null;
    resetNewUnitData();
  } catch (error) {
    console.error("Failed to update storage unit:", error);
    alert("Erreur lors de la mise à jour de l'unité de stockage");
  }
};

const deleteStorageUnit = async (unit) => {
  if (!selectedCave.value) return;

  if (confirm(`Êtes-vous sûr de vouloir supprimer l'unité "${unit.name}" ?`)) {
    try {
      await caveStore.deleteStorageUnit(selectedCave.value.id, unit.id);

      if (selectedUnit.value?.id === unit.id) {
        caveStore.selectUnit(null);
        selectedView.value = "overview";
      }
    } catch (error) {
      console.error("Failed to delete storage unit:", error);
      alert("Erreur lors de la suppression de l'unité de stockage");
    }
  }
};

// ===== UI helpers
const getUnitIcon = (type) => {
  const icons = {
    rack: "mdi-package-variant-closed",
    shelf: "mdi-shelf",
    cabinet: "mdi-archive",
    "wall-mounted": "mdi-wall",
    "floor-standing": "mdi-floor-plan",
  };
  return icons[type] || "mdi-package-variant";
};

const getUnitColor = (type) => {
  const colors = {
    rack: "brown",
    shelf: "orange",
    cabinet: "blue",
    "wall-mounted": "green",
    "floor-standing": "purple",
  };
  return colors[type] || "grey";
};

// ===== Pointer cleanup
const cleanupPointerListeners = () => {
  document.removeEventListener("mousemove", onMouseMove);
  document.removeEventListener("mouseup", onMouseUp);

  document.removeEventListener("mousemove", onResizeMove);
  document.removeEventListener("mouseup", onResizeUp);

  document.removeEventListener("mousemove", onElevateMove);
  document.removeEventListener("mouseup", onElevateUp);
};

onBeforeUnmount(() => {
  roTop?.disconnect();
  roWall?.disconnect();
  cleanupPointerListeners();
});

// ===== Edit mode / drag
const toggleEditMode = () => {
  editMode.value = !editMode.value;
  if (!editMode.value) {
    draggingUnit.value = null;
    resizingUnit.value = null;
    resizeDir.value = null;
    elevatingUnit.value = null;
    cleanupPointerListeners();
  }
};

// Top-down drag (overview)
const startDrag = (unit, event) => {
  if (!editMode.value) return;
  if (resizingUnit.value) return;
  if (elevatingUnit.value) return;
  if (event.button !== 0) return;

  const canvas = canvasRef.value;
  if (!canvas) return;

  draggingUnit.value = unit;

  const canvasRect = canvas.getBoundingClientRect();
  dragOffset.value = {
    x: event.clientX - canvasRect.left - unit.position.x * scale.value,
    y: event.clientY - canvasRect.top - unit.position.y * scale.value,
  };

  document.addEventListener("mousemove", onMouseMove);
  document.addEventListener("mouseup", onMouseUp);
};

const onMouseMove = (event) => {
  if (!draggingUnit.value || !editMode.value) return;
  if (!selectedCave.value) return;

  const canvas = canvasRef.value;
  if (!canvas) return;

  const canvasRect = canvas.getBoundingClientRect();
  const newX = (event.clientX - canvasRect.left - dragOffset.value.x) / scale.value;
  const newY = (event.clientY - canvasRect.top - dragOffset.value.y) / scale.value;

  const maxX = selectedCave.value.dimensions.width - draggingUnit.value.dimensions.width;
  const maxY = selectedCave.value.dimensions.depth - draggingUnit.value.dimensions.depth;

  const nextX = Math.max(0, Math.min(maxX, newX));
  const nextY = Math.max(0, Math.min(maxY, newY));

  draggingUnit.value.position.x = nextX;
  draggingUnit.value.position.y = nextY;

  // Snapping logic to walls
  const threshold = 20;
  const { width, depth } = selectedCave.value.dimensions;
  
  if (nextY <= threshold) {
    draggingUnit.value.position.y = 0;
    draggingUnit.value.wall = "north";
  } else if (nextY >= depth - draggingUnit.value.dimensions.depth - threshold) {
    draggingUnit.value.position.y = depth - draggingUnit.value.dimensions.depth;
    draggingUnit.value.wall = "south";
  } else if (nextX <= threshold) {
    draggingUnit.value.position.x = 0;
    draggingUnit.value.wall = "west";
  } else if (nextX >= width - draggingUnit.value.dimensions.width - threshold) {
    draggingUnit.value.position.x = width - draggingUnit.value.dimensions.width;
    draggingUnit.value.wall = "east";
  }

  // Force snap for specific types
  if (draggingUnit.value.type === 'shelf' || draggingUnit.value.type === 'wall-mounted') {
    const dN = draggingUnit.value.position.y;
    const dS = depth - draggingUnit.value.dimensions.depth - draggingUnit.value.position.y;
    const dW = draggingUnit.value.position.x;
    const dE = width - draggingUnit.value.dimensions.width - draggingUnit.value.position.x;
    const minD = Math.min(dN, dS, dW, dE);

    if (minD > 0) { // If not already snapped
      if (minD === dN) { draggingUnit.value.position.y = 0; draggingUnit.value.wall = "north"; }
      else if (minD === dS) { draggingUnit.value.position.y = depth - draggingUnit.value.dimensions.depth; draggingUnit.value.wall = "south"; }
      else if (minD === dW) { draggingUnit.value.position.x = 0; draggingUnit.value.wall = "west"; }
      else if (minD === dE) { draggingUnit.value.position.x = width - draggingUnit.value.dimensions.width; draggingUnit.value.wall = "east"; }
    }
  }
};

const onMouseUp = async () => {
  try {
    if (draggingUnit.value && selectedCave.value) {
      const updated = await caveStore.updateStorageUnit(selectedCave.value.id, draggingUnit.value.id, {
        position: { ...draggingUnit.value.position },
        wall: draggingUnit.value.wall,
      });

      if (selectedUnit.value?.id === updated?.id) {
        caveStore.selectUnit(updated);
      }
    }
  } catch (error) {
    console.error("Failed to update unit position:", error);
  } finally {
    draggingUnit.value = null;
    cleanupPointerListeners();
  }
};

const rotateUnit = async (unit) => {
  if (!editMode.value) return;
  if (!selectedCave.value) return;

  const current = Number(unit.rotation || 0);
  unit.rotation = (current + 90) % 360;

  try {
    const updated = await caveStore.updateStorageUnit(selectedCave.value.id, unit.id, {
      rotation: unit.rotation,
    });

    if (selectedUnit.value?.id === updated?.id) {
      caveStore.selectUnit(updated);
    }
  } catch (error) {
    console.error("Failed to update unit rotation:", error);
  }
};

const onUnitClick = (unit) => {
  if (editMode.value) return;
  selectUnit(unit);
};

// ===== Resize (top-down)
const startResize = (unit, dir, event) => {
  if (!editMode.value) return;
  if (event.button !== 0) return;
  if (!selectedCave.value) return;

  event.stopPropagation();

  resizingUnit.value = unit;
  resizeDir.value = dir;

  resizeStart.value = {
    mouseX: event.clientX,
    mouseY: event.clientY,
    x: unit.position.x,
    y: unit.position.y,
    w: unit.dimensions.width,
    d: unit.dimensions.depth,
  };

  document.addEventListener("mousemove", onResizeMove);
  document.addEventListener("mouseup", onResizeUp);
};

const onResizeMove = (event) => {
  if (!resizingUnit.value || !editMode.value || !selectedCave.value) return;

  const dx = (event.clientX - resizeStart.value.mouseX) / scale.value;
  const dy = (event.clientY - resizeStart.value.mouseY) / scale.value;

  let newX = resizeStart.value.x;
  let newY = resizeStart.value.y;
  let newW = resizeStart.value.w;
  let newD = resizeStart.value.d;

  const dir = resizeDir.value || "";

  if (dir.includes("e")) newW = resizeStart.value.w + dx;
  if (dir.includes("s")) newD = resizeStart.value.d + dy;
  if (dir.includes("w")) {
    newW = resizeStart.value.w - dx;
    newX = resizeStart.value.x + dx;
  }
  if (dir.includes("n")) {
    newD = resizeStart.value.d - dy;
    newY = resizeStart.value.y + dy;
  }

  newW = Math.max(MIN_UNIT_W, newW);
  newD = Math.max(MIN_UNIT_H, newD);

  const caveW = selectedCave.value.dimensions.width;
  const caveD = selectedCave.value.dimensions.depth;

  newX = Math.max(0, Math.min(caveW - newW, newX));
  newY = Math.max(0, Math.min(caveD - newD, newY));

  resizingUnit.value.position.x = newX;
  resizingUnit.value.position.y = newY;
  resizingUnit.value.dimensions.width = newW;
  resizingUnit.value.dimensions.depth = newD;
};

const onResizeUp = async () => {
  try {
    if (resizingUnit.value && selectedCave.value) {
      const updated = await caveStore.updateStorageUnit(selectedCave.value.id, resizingUnit.value.id, {
        position: { ...resizingUnit.value.position },
        dimensions: { ...resizingUnit.value.dimensions },
      });

      if (selectedUnit.value?.id === updated?.id) {
        caveStore.selectUnit(updated);
      }
    }
  } catch (error) {
    console.error("Failed to update unit dimensions:", error);
  } finally {
    resizingUnit.value = null;
    resizeDir.value = null;
    cleanupPointerListeners();
  }
};

// ===== Wall front view helpers
const getWallXcm = (unit) => {
  const w = unit.wall ?? "north";
  if (w === "north" || w === "south") return Number(unit.position?.x ?? 0);
  return Number(unit.position?.y ?? 0);
};

const getWallUnitTopPx = (unit) => {
  const elev = Number(unit.elevation ?? 0);
  const unitH = Number(unit.dimensions?.height ?? 0);
  const H = wallHeightCm.value;
  const topCm = H - (elev + unitH);
  return topCm * scale.value;
};

const getWallUnitLeftPx = (unit) => getWallXcm(unit) * scale.value;

const clampElevation = (elev, unitHeight) => {
  const H = wallHeightCm.value;
  const maxElev = Math.max(0, H - unitHeight);
  return Math.max(0, Math.min(maxElev, elev));
};

// ===== Wall elevation drag (move up/down)
const startElevateDrag = (unit, event) => {
  if (!editMode.value) return;
  if (event.button !== 0) return;
  if (!selectedCave.value) return;

  event.stopPropagation();

  elevatingUnit.value = unit;
  elevateStart.value = {
    mouseY: event.clientY,
    elevation: Number(unit.elevation ?? 0),
  };

  document.addEventListener("mousemove", onElevateMove);
  document.addEventListener("mouseup", onElevateUp);
};

const onElevateMove = (event) => {
  if (!editMode.value || !elevatingUnit.value) return;

  const dyPx = event.clientY - elevateStart.value.mouseY;
  const dyCm = dyPx / scale.value;

  const unitH = Number(elevatingUnit.value.dimensions?.height ?? 0);
  const nextElevation = clampElevation(elevateStart.value.elevation - dyCm, unitH);

  elevatingUnit.value.elevation = nextElevation;
};

const onElevateUp = async () => {
  try {
    if (elevatingUnit.value && selectedCave.value) {
      const updated = await caveStore.updateStorageUnit(selectedCave.value.id, elevatingUnit.value.id, {
        elevation: Number(elevatingUnit.value.elevation ?? 0),
        wall: elevatingUnit.value.wall ?? "north",
      });

      if (selectedUnit.value?.id === updated?.id) {
        caveStore.selectUnit(updated);
      }
    }
  } catch (error) {
    console.error("Failed to update unit elevation:", error);
  } finally {
    elevatingUnit.value = null;
    cleanupPointerListeners();
  }
};
</script>

<template>
  <div class="cave-admin-background">
    <v-container fluid class="cave-admin-container">
      <v-row class="mb-4">
        <v-col>
          <v-card class="header-card" elevation="4">
            <v-card-title class="d-flex align-center justify-space-between">
              <div class="d-flex align-center">
                <v-icon class="mr-3" size="32" color="brown">mdi-storefront</v-icon>
                <div>
                  <h1 class="text-h4 mb-1">Administration des Caves</h1>
                  <p class="text-body-2 text-medium-emphasis mb-0">
                    Gérez la disposition et la configuration de vos espaces de stockage
                  </p>
                </div>
              </div>

              <v-btn color="brown" variant="flat" @click="showCreateCaveDialog = true" class="create-cave-btn">
                <v-icon class="mr-2">mdi-plus</v-icon>
                Nouvelle Cave
              </v-btn>
            </v-card-title>
          </v-card>
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12" md="3">
          <v-card class="cave-list-card" elevation="4">
            <v-card-title class="cave-list-title">
              <v-icon class="mr-2">mdi-format-list-bulleted</v-icon>
              Mes Caves
            </v-card-title>

            <v-card-text class="pa-0">
              <v-list class="cave-list">
                <v-list-item
                    v-for="cave in caveStore.caves"
                    :key="cave.id"
                    @click="selectCave(cave)"
                    :class="{ 'cave-list-item-active': selectedCave?.id === cave.id }"
                    class="cave-list-item"
                >
                  <template #prepend>
                    <v-icon class="mr-3" color="brown">mdi-home</v-icon>
                  </template>

                  <v-list-item-title class="cave-name">{{ cave.name }}</v-list-item-title>
                  <v-list-item-subtitle class="cave-details">
                    {{ (cave.units?.length ?? 0) }} unités • {{ cave.dimensions.width }}×{{ cave.dimensions.height }}cm
                  </v-list-item-subtitle>

                  <template #append>
                    <div class="d-flex align-center" style="gap: 6px">
                      <v-chip size="small" variant="outlined" color="brown">
                        {{ cave.units?.length ?? 0 }}
                      </v-chip>

                      <v-menu location="bottom end">
                        <template #activator="{ props }">
                          <v-btn v-bind="props" icon size="small" variant="text" @click.stop>
                            <v-icon size="18">mdi-dots-vertical</v-icon>
                          </v-btn>
                        </template>

                        <v-list density="compact">
                          <v-list-item @click.stop="openEditCave(cave)">
                            <template #prepend>
                              <v-icon size="18">mdi-pencil</v-icon>
                            </template>
                            <v-list-item-title>Modifier</v-list-item-title>
                          </v-list-item>

                          <v-list-item @click.stop="deleteCave(cave)">
                            <template #prepend>
                              <v-icon size="18" color="error">mdi-delete</v-icon>
                            </template>
                            <v-list-item-title class="text-error">Supprimer</v-list-item-title>
                          </v-list-item>
                        </v-list>
                      </v-menu>
                    </div>
                  </template>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>
        </v-col>

        <v-col cols="12" md="9">
          <div v-if="selectedCave && selectedView === 'overview'">
            <v-card class="cave-overview-card" elevation="4">
              <v-card-title class="cave-overview-title">
                <div class="d-flex align-center justify-space-between w-100">
                  <div class="d-flex align-center">
                    <v-icon class="mr-3" color="brown">mdi-floor-plan</v-icon>
                    <div>
                      <h2 class="text-h5 mb-1">{{ selectedCave.name }}</h2>
                      <p class="text-body-2 text-medium-emphasis mb-0">{{ selectedCave.description }}</p>
                    </div>
                  </div>

                  <div class="d-flex align-center">
                    <v-btn-toggle
                        v-model="selectedView"
                        mandatory
                        color="brown"
                        variant="outlined"
                        density="comfortable"
                        class="mr-2"
                    >
                      <v-btn value="overview">
                        <v-icon start>mdi-floor-plan</v-icon>
                        Vue Plan
                      </v-btn>
                      <v-btn value="walls">
                        <v-icon start>mdi-wall</v-icon>
                        Vue Murs
                      </v-btn>
                    </v-btn-toggle>

                    <v-divider vertical class="mx-2 my-2" />

                    <v-btn variant="outlined" class="mr-2" @click="openEditCave(selectedCave)">
                      <v-icon class="mr-2">mdi-pencil</v-icon>
                      Modifier Cave
                    </v-btn>

                    <v-btn variant="outlined" color="error" class="mr-2" @click="deleteCave(selectedCave)">
                      <v-icon class="mr-2">mdi-delete</v-icon>
                      Supprimer
                    </v-btn>

                    <v-btn color="primary" variant="flat" @click="showCreateUnitDialog = true">
                      <v-icon class="mr-2">mdi-plus</v-icon>
                      Ajouter Unité
                    </v-btn>

                    <v-btn
                        :color="editMode ? 'success' : 'secondary'"
                        :variant="editMode ? 'flat' : 'outlined'"
                        @click="toggleEditMode"
                        class="ml-2"
                    >
                      <v-icon class="mr-2">{{ editMode ? "mdi-check" : "mdi-pencil" }}</v-icon>
                      {{ editMode ? "Terminer Édition" : "Éditer Disposition" }}
                    </v-btn>
                  </div>
                </div>
              </v-card-title>

              <v-card-text>
                <v-row class="mb-4">
                  <v-col cols="12" sm="3">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="brown" class="mb-2">mdi-package-variant-closed</v-icon>
                        <div class="text-h6">{{ selectedCave.units.length }}</div>
                        <div class="text-caption">Unités</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="3">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="blue" class="mb-2">mdi-thermometer</v-icon>
                        <div class="text-h6">{{ selectedCave.temperature }}°C</div>
                        <div class="text-caption">Température</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="3">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="green" class="mb-2">mdi-water-percent</v-icon>
                        <div class="text-h6">{{ selectedCave.humidity }}%</div>
                        <div class="text-caption">Humidité</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="3">
                    <v-card variant="outlined" class="stat-card">
                      <v-card-text class="text-center">
                        <v-icon size="32" color="orange" class="mb-2">mdi-ruler-square</v-icon>
                        <div class="text-h6">
                          {{ selectedCave.dimensions.width }}×{{ selectedCave.dimensions.height }}
                        </div>
                        <div class="text-caption">Dimensions (cm)</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>

                <div class="cave-layout-container">
                  <div class="d-flex align-center justify-space-between mb-3">
                    <h3 class="text-h6 mb-0">Vue d'ensemble (plan)</h3>

                    <div class="layout-zoom">
                      <span class="text-caption">Zoom</span>
                      <v-slider
                          v-model="zoom"
                          :min="0.5"
                          :max="2.5"
                          :step="0.05"
                          hide-details
                          density="compact"
                          style="min-width: 200px; max-width: 220px"
                      />
                      <span class="text-caption">{{ Math.round(scale * 100) }}%</span>
                    </div>
                  </div>

                  <div ref="overviewViewportRef" class="cave-layout-scroll">
                    <div class="layout-wrapper" :style="{
                        width: (selectedCave.dimensions.width * scale + 80) + 'px',
                        height: (selectedCave.dimensions.depth * scale + 80) + 'px',
                        padding: '40px',
                        position: 'relative'
                    }">
                      <!-- Wall Labels -->
                      <div class="wall-label wall-label-north">NORD (Mur 1)</div>
                      <div class="wall-label wall-label-south">SUD (Mur 3)</div>
                      <div class="wall-label wall-label-east">EST (Mur 2)</div>
                      <div class="wall-label wall-label-west">OUEST (Mur 4)</div>

                      <div
                          ref="canvasRef"
                          class="cave-layout-canvas"
                          :style="{
                          width: selectedCave.dimensions.width * scale + 'px',
                          height: selectedCave.dimensions.depth * scale + 'px'
                        }"
                      >
                      <div
                          v-for="unit in selectedCave.units"
                          :key="unit.id"
                          class="storage-unit"
                          :class="[
                          `unit-${unit.type}`,
                          {
                            'unit-editing': editMode,
                            'unit-dragging': draggingUnit === unit,
                            'unit-resizing': resizingUnit === unit
                          }
                        ]"
                          :style="{
                          left: unit.position.x * scale + 'px',
                          top: unit.position.y * scale + 'px',
                          width: unit.dimensions.width * scale + 'px',
                          height: unit.dimensions.depth * scale + 'px',
                          '--rotation': `${unit.rotation || 0}deg`,
                          cursor: editMode ? 'move' : 'pointer'
                        }"
                          @mousedown="startDrag(unit, $event)"
                          @contextmenu.prevent="rotateUnit(unit)"
                          @click="onUnitClick(unit)"
                      >
                        <div class="unit-header">
                          <v-icon :icon="getUnitIcon(unit.type)" size="16" />
                          <span class="unit-name">{{ unit.name }}</span>
                        </div>

                        <div class="unit-capacity">
                          {{ unit.capacity }} emplacements
                        </div>

                        <div v-if="!editMode" class="unit-actions">
                          <v-btn icon size="small" variant="text" @click.stop="editStorageUnit(unit)">
                            <v-icon size="14">mdi-pencil</v-icon>
                          </v-btn>
                          <v-btn icon size="small" variant="text" color="error" @click.stop="deleteStorageUnit(unit)">
                            <v-icon size="14">mdi-delete</v-icon>
                          </v-btn>
                        </div>

                        <div v-else class="unit-edit-hint">
                          <v-icon size="12" color="white">mdi-rotate-right</v-icon>
                          <span class="edit-text">Clic droit pour tourner</span>
                        </div>

                        <div v-if="editMode" class="resize-handles">
                          <div class="rh rh-nw" @mousedown="startResize(unit, 'nw', $event)"></div>
                          <div class="rh rh-ne" @mousedown="startResize(unit, 'ne', $event)"></div>
                          <div class="rh rh-sw" @mousedown="startResize(unit, 'sw', $event)"></div>
                          <div class="rh rh-se" @mousedown="startResize(unit, 'se', $event)"></div>

                          <div class="rh rh-n" @mousedown="startResize(unit, 'n', $event)"></div>
                          <div class="rh rh-s" @mousedown="startResize(unit, 's', $event)"></div>
                          <div class="rh rh-e" @mousedown="startResize(unit, 'e', $event)"></div>
                          <div class="rh rh-w" @mousedown="startResize(unit, 'w', $event)"></div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="mt-4 text-caption text-medium-emphasis">
                    Astuce : dans la Vue Murs, vous pouvez déplacer les éléments en hauteur (élévation) en mode édition.
                  </div>
                </div>
              </v-card-text>
            </v-card>
          </div>

          <div v-else-if="selectedCave && selectedView === 'walls'">
            <v-card class="cave-overview-card" elevation="4">
              <v-card-title class="cave-overview-title">
                <div class="d-flex align-center justify-space-between w-100">
                  <div class="d-flex align-center">
                    <v-btn-toggle
                        v-model="selectedView"
                        mandatory
                        color="brown"
                        variant="outlined"
                        density="comfortable"
                        class="mr-4"
                    >
                      <v-btn value="overview">
                        <v-icon start>mdi-floor-plan</v-icon>
                        Vue Plan
                      </v-btn>
                      <v-btn value="walls">
                        <v-icon start>mdi-wall</v-icon>
                        Vue Murs
                      </v-btn>
                    </v-btn-toggle>

                    <v-icon class="mr-3" color="brown">mdi-wall-outline</v-icon>
                    <div>
                      <h2 class="text-h5 mb-0">Vue frontale : {{ wallItems.find(w => w.value === selectedWall)?.title }}</h2>
                    </div>
                  </div>

                  <div class="d-flex align-center">
                    <v-btn
                        :color="editMode ? 'success' : 'secondary'"
                        :variant="editMode ? 'flat' : 'outlined'"
                        @click="toggleEditMode"
                        class="ml-2"
                    >
                      <v-icon class="mr-2">{{ editMode ? "mdi-check" : "mdi-pencil" }}</v-icon>
                      {{ editMode ? "Terminer Édition" : "Éditer (hauteur)" }}
                    </v-btn>
                  </div>
                </div>
              </v-card-title>

              <v-card-text>
                <v-tabs v-model="selectedWall" class="mb-3" color="brown" align-tabs="center">
                  <v-tab v-for="w in wallItems" :key="w.value" :value="w.value">
                    {{ w.title }}
                  </v-tab>
                </v-tabs>

                <div class="d-flex align-center justify-space-between mb-3">
                  <div class="text-body-2">
                    Longueur mur : <strong>{{ wallLengthCm }} cm</strong> • Hauteur mur : <strong>{{ wallHeightCm }} cm</strong>
                  </div>

                  <div class="layout-zoom">
                    <span class="text-caption">Zoom</span>
                    <v-slider
                        v-model="zoom"
                        :min="0.5"
                        :max="2.5"
                        :step="0.05"
                        hide-details
                        density="compact"
                        style="min-width: 200px; max-width: 220px"
                    />
                    <span class="text-caption">{{ Math.round(scale * 100) }}%</span>
                  </div>
                </div>

                <div ref="wallViewportRef" class="wall-layout-scroll">
                  <div class="layout-wrapper" :style="{
                      width: (wallLengthCm * scale + 80) + 'px',
                      height: (wallHeightCm * scale + 120) + 'px',
                      padding: '40px 40px 80px 40px',
                      position: 'relative'
                  }">
                    <div class="view-indicator side-view">VUE MURALE</div>

                    <div
                        ref="wallCanvasRef"
                        class="wall-layout-canvas"
                        :style="{
                        width: wallLengthCm * scale + 'px',
                        height: wallHeightCm * scale + 'px'
                      }"
                    >
                      <div class="wall-floor-line"></div>
                      <div class="wall-ceiling-line"></div>

                    <div
                        v-for="unit in unitsOnSelectedWall"
                        :key="unit.id"
                        class="wall-unit"
                        :class="[
                        `unit-${unit.type}`,
                        {
                          'unit-editing': editMode,
                          'unit-resizing': elevatingUnit === unit
                        }
                      ]"
                        :style="{
                        left: getWallUnitLeftPx(unit) + 'px',
                        top: getWallUnitTopPx(unit) + 'px',
                        width: (selectedWall === 'north' || selectedWall === 'south' ? unit.dimensions.width : unit.dimensions.depth) * scale + 'px',
                        height: unit.dimensions.height * scale + 'px'
                      }"
                        @click="!editMode && onUnitClick(unit)"
                    >
                      <div class="wall-unit-label">
                        <v-icon :icon="getUnitIcon(unit.type)" size="14" />
                        <span class="unit-name">{{ unit.name }}</span>
                      </div>

                      <div class="wall-unit-meta">
                        <span>{{ Math.round(unit.elevation ?? 0) }} cm</span>
                      </div>

                      <div v-if="editMode" class="wall-elevate-handle" @mousedown="startElevateDrag(unit, $event)">
                        <v-icon size="14" color="white">mdi-arrow-up-down</v-icon>
                        <span class="text">Glisser</span>
                      </div>

                      <div v-if="!editMode" class="unit-actions">
                        <v-btn icon size="small" variant="text" @click.stop="editStorageUnit(unit)">
                          <v-icon size="14">mdi-pencil</v-icon>
                        </v-btn>
                        <v-btn icon size="small" variant="text" color="error" @click.stop="deleteStorageUnit(unit)">
                          <v-icon size="14">mdi-delete</v-icon>
                        </v-btn>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

                <div class="mt-3 text-caption text-medium-emphasis">
                  En mode édition : attrapez “Glisser” pour monter/descendre l’élément (hauteur/élévation). La valeur est sauvegardée.
                </div>
              </v-card-text>
            </v-card>
          </div>

          <div v-else-if="selectedUnit && selectedView === 'detail'">
            <v-card class="unit-detail-card" elevation="4">
              <v-card-title class="unit-detail-title">
                <v-btn icon variant="text" @click="selectedView = 'overview'" class="mr-2">
                  <v-icon>mdi-arrow-left</v-icon>
                </v-btn>

                <v-icon class="mr-3" :icon="getUnitIcon(selectedUnit.type)" :color="getUnitColor(selectedUnit.type)" />

                <div>
                  <h2 class="text-h5 mb-1">{{ selectedUnit.name }}</h2>
                  <p class="text-body-2 text-medium-emphasis mb-0">
                    {{ selectedUnit.type }} • {{ selectedUnit.capacity }} emplacements
                  </p>
                </div>
              </v-card-title>

              <v-card-text>
                <v-row>
                  <v-col cols="12" md="8">
                    <div class="unit-grid-container">
                      <h4 class="text-subtitle-1 mb-3">Vue détaillée des emplacements</h4>
                      <div class="unit-grid">
                        <div
                            v-for="space in selectedUnit.spaces"
                            :key="space.id"
                            class="storage-space"
                            :class="{ 'space-occupied': caveStore.occupiedSpaces.includes(space) }"
                        >
                          <div class="space-content">
                            <v-icon v-if="caveStore.occupiedSpaces.includes(space)" color="green" size="20">
                              mdi-bottle-wine
                            </v-icon>
                            <span v-else class="space-number">
                              {{ space.position.row + 1 }}-{{ space.position.column + 1 }}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </v-col>

                  <v-col cols="12" md="4">
                    <v-card variant="outlined" class="unit-properties">
                      <v-card-title class="text-subtitle-1">Propriétés</v-card-title>
                      <v-card-text>
                        <div class="property-item">
                          <strong>Type:</strong> {{ unitTypes.find(t => t.value === selectedUnit.type)?.title }}
                        </div>
                        <div class="property-item">
                          <strong>Mur:</strong> {{ wallItems.find(w => w.value === (selectedUnit.wall ?? 'north'))?.title }}
                        </div>
                        <div class="property-item">
                          <strong>Élévation:</strong> {{ Math.round(selectedUnit.elevation ?? 0) }} cm
                        </div>
                        <div class="property-item">
                          <strong>Position:</strong> {{ selectedUnit.position.x }}, {{ selectedUnit.position.y }}
                        </div>
                        <div class="property-item">
                          <strong>Dimensions:</strong>
                          {{ selectedUnit.dimensions.width }}×{{ selectedUnit.dimensions.height }}×{{ selectedUnit.dimensions.depth }} cm
                        </div>
                        <div class="property-item">
                          <strong>Orientation:</strong> {{ selectedUnit.orientation === 'vertical' ? 'Verticale' : 'Horizontale' }}
                        </div>
                        <div class="property-item">
                          <strong>Capacité:</strong> {{ selectedUnit.capacity }} bouteilles
                        </div>
                        <div class="property-item">
                          <strong>Occupés:</strong> {{ caveStore.occupiedSpaces.length }}/{{ selectedUnit.capacity }}
                        </div>

                        <div class="mt-3 d-flex" style="gap: 8px; flex-wrap: wrap">
                          <v-btn variant="outlined" @click="editStorageUnit(selectedUnit)">
                            <v-icon class="mr-2">mdi-pencil</v-icon>
                            Modifier
                          </v-btn>
                          <v-btn variant="outlined" color="error" @click="deleteStorageUnit(selectedUnit)">
                            <v-icon class="mr-2">mdi-delete</v-icon>
                            Supprimer
                          </v-btn>
                        </div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
          </div>

          <div v-else class="no-selection">
            <v-card class="no-selection-card" elevation="4">
              <v-card-text class="text-center pa-8">
                <v-icon size="64" color="grey" class="mb-4">mdi-storefront-outline</v-icon>
                <h3 class="text-h5 mb-2">Aucune cave sélectionnée</h3>
                <p class="text-body-1 text-medium-emphasis mb-4">
                  Sélectionnez une cave dans la liste ou créez-en une nouvelle pour commencer.
                </p>
                <v-btn color="brown" variant="flat" @click="showCreateCaveDialog = true" size="large">
                  <v-icon class="mr-2">mdi-plus</v-icon>
                  Créer ma première cave
                </v-btn>
              </v-card-text>
            </v-card>
          </div>
        </v-col>
      </v-row>
    </v-container>

    <v-dialog v-model="showCreateCaveDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="brown">mdi-plus</v-icon>
          Créer une nouvelle cave
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-text-field v-model="newCaveData.name" label="Nom de la cave" variant="outlined" required class="mb-3" />
            <v-textarea v-model="newCaveData.description" label="Description" variant="outlined" class="mb-3" />

            <v-row>
              <v-col cols="4">
                <v-text-field v-model.number="newCaveData.dimensions.width" label="Largeur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newCaveData.dimensions.height" label="Hauteur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newCaveData.dimensions.depth" label="Profondeur (cm)" type="number" variant="outlined" />
              </v-col>
            </v-row>

            <v-row class="mt-3">
              <v-col cols="6">
                <v-text-field v-model.number="newCaveData.temperature" label="Température (°C)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="6">
                <v-text-field v-model.number="newCaveData.humidity" label="Humidité (%)" type="number" variant="outlined" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="outlined" @click="showCreateCaveDialog = false">Annuler</v-btn>
          <v-btn color="brown" variant="flat" @click="createCave" :disabled="!newCaveData.name">Créer la cave</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showEditCaveDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="brown">mdi-pencil</v-icon>
          Modifier la cave
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-text-field v-model="editCaveData.name" label="Nom de la cave" variant="outlined" required class="mb-3" />
            <v-textarea v-model="editCaveData.description" label="Description" variant="outlined" class="mb-3" />

            <v-row>
              <v-col cols="4">
                <v-text-field v-model.number="editCaveData.dimensions.width" label="Largeur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="editCaveData.dimensions.height" label="Hauteur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="editCaveData.dimensions.depth" label="Profondeur (cm)" type="number" variant="outlined" />
              </v-col>
            </v-row>

            <v-row class="mt-3">
              <v-col cols="6">
                <v-text-field v-model.number="editCaveData.temperature" label="Température (°C)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="6">
                <v-text-field v-model.number="editCaveData.humidity" label="Humidité (%)" type="number" variant="outlined" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn
              variant="outlined"
              @click="
              showEditCaveDialog = false;
              editingCave = null;
              resetEditCaveData();
            "
          >
            Annuler
          </v-btn>
          <v-btn color="brown" variant="flat" @click="updateCave" :disabled="!editCaveData.name">
            Mettre à jour
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showCreateUnitDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="primary">mdi-plus</v-icon>
          Ajouter une unité de stockage
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-text-field v-model="newUnitData.name" label="Nom de l'unité" variant="outlined" required class="mb-3" />

            <v-select v-model="newUnitData.type" :items="unitTypes" label="Type d'unité" variant="outlined" class="mb-3" />

            <v-select
                v-model="newUnitData.wall"
                :items="wallItems"
                label="Mur (pour la vue frontale)"
                variant="outlined"
                class="mb-3"
            />

            <v-text-field
                v-model.number="newUnitData.elevation"
                label="Élévation (cm depuis le sol)"
                type="number"
                variant="outlined"
                class="mb-3"
            />

            <v-select
                v-model="newUnitData.orientation"
                :items="[
                { value: 'vertical', title: 'Verticale' },
                { value: 'horizontal', title: 'Horizontale' }
              ]"
                label="Orientation"
                variant="outlined"
                class="mb-3"
            />

            <v-row>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.width" label="Largeur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.height" label="Hauteur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.depth" label="Profondeur (cm)" type="number" variant="outlined" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="outlined" @click="showCreateUnitDialog = false">Annuler</v-btn>
          <v-btn color="primary" variant="flat" @click="createStorageUnit" :disabled="!newUnitData.name">Ajouter l'unité</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showEditUnitDialog" max-width="600px">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon class="mr-3" color="primary">mdi-pencil</v-icon>
          Modifier l'unité de stockage
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-text-field v-model="newUnitData.name" label="Nom de l'unité" variant="outlined" required class="mb-3" />
            <v-select v-model="newUnitData.type" :items="unitTypes" label="Type d'unité" variant="outlined" class="mb-3" />

            <v-select
                v-model="newUnitData.wall"
                :items="wallItems"
                label="Mur (pour la vue frontale)"
                variant="outlined"
                class="mb-3"
            />

            <v-text-field
                v-model.number="newUnitData.elevation"
                label="Élévation (cm depuis le sol)"
                type="number"
                variant="outlined"
                class="mb-3"
            />

            <v-select
                v-model="newUnitData.orientation"
                :items="[
                { value: 'vertical', title: 'Verticale' },
                { value: 'horizontal', title: 'Horizontale' }
              ]"
                label="Orientation"
                variant="outlined"
                class="mb-3"
            />

            <v-row>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.width" label="Largeur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.height" label="Hauteur (cm)" type="number" variant="outlined" />
              </v-col>
              <v-col cols="4">
                <v-text-field v-model.number="newUnitData.dimensions.depth" label="Profondeur (cm)" type="number" variant="outlined" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn variant="outlined" @click="showEditUnitDialog = false">Annuler</v-btn>
          <v-btn color="primary" variant="flat" @click="updateStorageUnit" :disabled="!newUnitData.name">Mettre à jour</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<style scoped>
/* keep your existing styles as-is */
.cave-admin-background {
  background: linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%);
  min-height: 100vh;
  padding: 16px;
}
.cave-admin-container {
  max-width: 100vw;
  margin: 0 auto;
}
.header-card,
.cave-list-card,
.cave-overview-card,
.unit-detail-card,
.no-selection-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(139, 69, 19, 0.1);
}
.create-cave-btn {
  background: linear-gradient(45deg, #8b4513, #a0522d);
  color: white;
  border-radius: 20px;
}
.cave-list-title {
  background: linear-gradient(90deg, #8b4513, #a0522d);
  color: white;
  border-radius: 10px 10px 0 0;
}
.cave-list-item {
  border-radius: 6px;
  margin: 2px 8px;
  transition: all 0.3s ease;
}
.cave-list-item:hover {
  background: rgba(139, 69, 19, 0.1);
}
.cave-list-item-active {
  background: rgba(139, 69, 19, 0.2);
  border-left: 3px solid #8b4513;
}
.cave-name {
  font-weight: 600;
  color: #8b4513;
}
.cave-details {
  font-size: 0.75rem;
}
.cave-overview-title,
.unit-detail-title {
  background: linear-gradient(90deg, rgba(139, 69, 19, 0.1), rgba(160, 82, 45, 0.1));
  color: #8b4513;
  border-radius: 10px 10px 0 0;
}
.stat-card {
  border-radius: 8px;
  transition: all 0.3s ease;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.2);
}
.cave-layout-container {
  margin-top: 24px;
}
.layout-zoom {
  display: flex;
  align-items: center;
  gap: 10px;
}
.cave-overview-card {
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
}
.cave-overview-card :deep(.v-card-text) {
  flex: 1 1 auto;
  overflow: hidden;
}
.cave-layout-scroll,
.wall-layout-scroll {
  overflow: auto;
  padding: 12px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.02);
  height: 100%;
  max-height: calc(100vh - 420px);
}
.cave-layout-canvas {
  position: relative;
  background-color: #e0dcd5;
  background-image: 
    linear-gradient(rgba(0, 0, 0, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  border: 4px solid #8b4513;
  border-radius: 8px;
  margin: 0 auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2), inset 0 0 20px rgba(0, 0, 0, 0.1);
}
.storage-unit {
  position: absolute;
  border-radius: 2px;
  transition: all 0.25s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0;
  font-size: 10px;
  overflow: hidden;
  transform: rotate(var(--rotation));
  transform-origin: center;
  user-select: none;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3);
}
.storage-unit:hover {
  z-index: 10;
  transform: rotate(var(--rotation)) scale(1.02);
  box-shadow: 4px 4px 10px rgba(0, 0, 0, 0.4);
}
.unit-rack {
  background: #8b4513;
  background-image: 
    repeating-linear-gradient(45deg, rgba(255, 255, 255, 0.05) 0px, rgba(255, 255, 255, 0.05) 2px, transparent 2px, transparent 10px),
    repeating-linear-gradient(-45deg, rgba(255, 255, 255, 0.05) 0px, rgba(255, 255, 255, 0.05) 2px, transparent 2px, transparent 10px);
  border: 1px solid #5d2e0a;
}
.unit-shelf {
  background: #deb887;
  background-image: linear-gradient(to right, rgba(0, 0, 0, 0.1) 1px, transparent 1px);
  background-size: 15px 100%;
  border: 1px solid #8b4513;
}
.unit-cabinet {
  background: #505050;
  background-image: 
    linear-gradient(to bottom, transparent 90%, rgba(0, 0, 0, 0.3) 100%),
    linear-gradient(to right, transparent 48%, rgba(255, 255, 255, 0.1) 50%, transparent 52%);
  border: 1px solid #333;
}
.unit-wall-mounted {
  background: #a52a2a;
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.2) 1px, transparent 1px);
  background-size: 10px 10px;
  border: 1px solid #800000;
}
.unit-floor-standing {
  background: #708090;
  background-image: repeating-linear-gradient(0deg, transparent, transparent 10px, rgba(0, 0, 0, 0.05) 10px, rgba(0, 0, 0, 0.05) 11px);
  border: 1px solid #4682b4;
}
.layout-wrapper {
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;
}
.wall-label {
  position: absolute;
  font-weight: bold;
  font-size: 14px;
  color: #8b4513;
  opacity: 0.6;
  text-transform: uppercase;
  letter-spacing: 2px;
}
.wall-label-north { top: 10px; left: 50%; transform: translateX(-50%); }
.wall-label-south { bottom: 10px; left: 50%; transform: translateX(-50%); }
.wall-label-east { right: 10px; top: 50%; transform: rotate(90deg) translateX(50%); transform-origin: right center; }
.wall-label-west { left: 10px; top: 50%; transform: rotate(-90deg) translateX(-50%); transform-origin: left center; }

.view-indicator {
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(139, 69, 19, 0.8);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 10px;
  font-weight: bold;
  letter-spacing: 1px;
  z-index: 5;
}

.wall-ceiling-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to bottom, #ddd, transparent);
}

.unit-header {
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: bold;
  color: white;
  padding: 2px 4px;
  backdrop-filter: blur(2px);
}
.unit-name {
  font-size: 9px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.unit-capacity {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.8);
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.5);
}
.unit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.25s ease;
}
.storage-unit:hover .unit-actions {
  opacity: 1;
}
.unit-editing {
  border-style: dashed !important;
  border-width: 3px !important;
}
.unit-dragging,
.unit-resizing {
  z-index: 1000;
  opacity: 0.9;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}
.unit-edit-hint {
  display: flex;
  align-items: center;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.25s ease;
  font-size: 8px;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}
.storage-unit:hover .unit-edit-hint {
  opacity: 1;
}
.edit-text {
  font-size: 7px;
  white-space: nowrap;
}
.resize-handles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.rh {
  position: absolute;
  width: 10px;
  height: 10px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(0, 0, 0, 0.25);
  border-radius: 2px;
  pointer-events: auto;
}
.rh-nw { left: -5px; top: -5px; cursor: nwse-resize; }
.rh-ne { right: -5px; top: -5px; cursor: nesw-resize; }
.rh-sw { left: -5px; bottom: -5px; cursor: nesw-resize; }
.rh-se { right: -5px; bottom: -5px; cursor: nwse-resize; }
.rh-n { left: 50%; top: -5px; transform: translateX(-50%); cursor: ns-resize; }
.rh-s { left: 50%; bottom: -5px; transform: translateX(-50%); cursor: ns-resize; }
.rh-e { right: -5px; top: 50%; transform: translateY(-50%); cursor: ew-resize; }
.rh-w { left: -5px; top: 50%; transform: translateY(-50%); cursor: ew-resize; }

.wall-layout-canvas {
  position: relative;
  background-color: #d2b48c;
  background-image: 
    linear-gradient(rgba(0,0,0,0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0,0,0,0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  border: 4px solid #5d2e0a;
  border-radius: 8px;
  margin: 0 auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3), inset 0 0 50px rgba(0,0,0,0.1);
}
.wall-floor-line {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 12px;
  background: linear-gradient(to bottom, #8b4513, #5d2e0a);
  border-top: 1px solid rgba(255,255,255,0.2);
  z-index: 2;
}
.wall-unit {
  position: absolute;
  border: 2px solid;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 6px;
  color: white;
  user-select: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.wall-unit:hover {
  z-index: 10;
  transform: scale(1.03);
  box-shadow: 0 10px 18px rgba(0, 0, 0, 0.22);
}
.wall-unit-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.55);
}
.wall-unit-meta {
  font-size: 11px;
  opacity: 0.95;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.55);
}
.wall-elevate-handle {
  margin-top: 6px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.25);
  cursor: ns-resize;
  width: fit-content;
}
.wall-elevate-handle .text {
  font-size: 11px;
  font-weight: 700;
}
.unit-grid-container {
  margin-bottom: 24px;
}
.unit-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 4px;
  max-width: 400px;
  margin: 0 auto;
}
.storage-space {
  aspect-ratio: 1;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f9f9f9;
  transition: all 0.25s ease;
  cursor: pointer;
}
.storage-space:hover {
  background: #e9e9e9;
  transform: scale(1.05);
}
.space-occupied {
  background: linear-gradient(45deg, #98fb98, #32cd32);
  border-color: #228b22;
}
.space-content {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: bold;
  color: #666;
}
.space-occupied .space-content {
  color: white;
}
.space-number {
  font-size: 8px;
}
.unit-properties {
  border-radius: 8px;
}
.property-item {
  margin-bottom: 8px;
  font-size: 0.875rem;
}
.property-item strong {
  color: #8b4513;
}
.no-selection {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

@media (max-width: 960px) {
  .cave-admin-background {
    padding: 8px;
  }
  .cave-overview-card {
    height: auto;
  }
  .cave-layout-scroll,
  .wall-layout-scroll {
    max-height: 60vh;
  }
  .unit-grid {
    grid-template-columns: repeat(4, 1fr);
    max-width: 300px;
  }
}
@media (max-width: 600px) {
  .cave-admin-background {
    padding: 4px;
  }
  .cave-layout-scroll,
  .wall-layout-scroll {
    max-height: 55vh;
  }
  .unit-grid {
    grid-template-columns: repeat(3, 1fr);
    max-width: 250px;
  }
  .storage-space {
    min-height: 30px;
  }
}
</style>
