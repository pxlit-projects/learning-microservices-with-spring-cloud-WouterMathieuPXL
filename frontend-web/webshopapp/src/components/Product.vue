<template>
    <v-card variant="flat" rounded="xl">
        <v-card-title class="pt-2 px-2 pb-0">{{ product.name }}</v-card-title>
        <v-card-subtitle class="d-flex justify-space-between py-0 px-2">
         <span class="text-caption">  {{ category }}</span>
            <v-chip label size="x-small" color="blue" class="mb-3"> &euro; {{ product.price.toFixed(2) }}</v-chip>
        </v-card-subtitle>

        <v-card-item class="justify-center ma-0 pa-2">
            <img class="product-image" v-if="product.imageUrl"
                 :src="`http://localhost:8084/images/${product.imageUrl}`" alt="{{ product.name }}"/>
            <div v-else class="product-image no-image"/>
        </v-card-item>
        <v-card-item class="pa-0">
            <span class="d-inline-block text-truncate text-caption px-2 py-0 ">
            {{ product.description }}
            </span>
        </v-card-item>


        <v-card-item class="py-0 px-2 labels">
            <v-chip-group
                row>
                <v-chip v-for="label in product.labels" :key="label.id"
                        size="x-small"
                        :style="{ backgroundColor: label.color.toLowerCase() }">{{ label.name }}
                </v-chip>
            </v-chip-group>
        </v-card-item>
        <v-card-actions class="d-flex justify-center pa-0">
            <v-btn v-if="!userStore.isAdmin"
                   prepend-icon="mdi-cart"
                   @click="shoppingCartStore.plusProduct(product.id)">Add
            </v-btn>

            <v-btn v-if="userStore.isAdmin"
                   icon="mdi-pencil"
                   @click="productCatalogStore.openAdminDialog(product)"/>
            <v-btn v-if="userStore.isAdmin"
                   icon="mdi-delete"
                   @click="productCatalogStore.deleteProduct(product.id)"/>
        </v-card-actions>
    </v-card>
</template>

<script setup>
import '@mdi/font/css/materialdesignicons.css';
import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";
import {useUserStore} from "@/stores/userStore.js";
import {useProductCatalogStore} from "@/stores/useProductCatalogStore.js";
import AdminDialog from "@/views/AdminDialog.vue";
import {computed} from "vue";

const userStore = useUserStore();
const productCatalogStore = useProductCatalogStore();
const shoppingCartStore = useShoppingCartStore();

const props = defineProps({
    product: Object
});

const category = computed(() => {
    return productCatalogStore.categories.find((category) => category.key === props.product.category)?.value || 'Unknown Category';
})
</script>

<style scoped>
.product-image {
    width: 200px;
    height: 200px;
}

.no-image {
    background-color: lightgray;
}
.labels {
    height: 40px;
}
</style>
