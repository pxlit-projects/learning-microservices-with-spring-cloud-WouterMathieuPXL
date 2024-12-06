<template>
    <v-card>
        <v-card-title>{{ product.name }}</v-card-title>
        <v-card-subtitle>
            &euro; {{ product.price.toFixed(2) }}
        </v-card-subtitle>
        <v-card-item>
            <img class="product-image" v-if="product.imageUrl" width="150"
                 :src="`http://localhost:8084/images/${product.imageUrl}`" alt="{{ product.name }}"/>
            <div v-else class="product-image no-image"/>
        </v-card-item>
        <v-card-item>{{ product.description }}</v-card-item>
        <v-card-item v-for="label in product.labels" :key="label.id">
            <v-chip label size="small" :style="{ backgroundColor: label.color.toLowerCase() }">{{ label.name }}</v-chip>
        </v-card-item>
        <v-card-actions>


            <v-btn v-if="!userStore.isAdmin" prepend-icon="mdi-plus" @click="shoppingCartStore.plusProduct(product.id)">
                Add to Cart
            </v-btn>


            <v-btn
                v-if="userStore.isAdmin"
                prepend-icon="mdi-pencil"
                @click="productCatalogStore.openAdminDialog(product)">
                Edit product
            </v-btn>


            <v-btn v-if="userStore.isAdmin" prepend-icon="mdi-delete"
                   @click="productCatalogStore.deleteProduct(product.id)">
                Delete product
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script setup>
import '@mdi/font/css/materialdesignicons.css';
import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";
import {useUserStore} from "@/stores/userStore.js";
import {useProductCatalogStore} from "@/stores/useProductCatalogStore.js";
import AdminDialog from "@/views/AdminDialog.vue";

const userStore = useUserStore();
const productCatalogStore = useProductCatalogStore();
const shoppingCartStore = useShoppingCartStore();

const props = defineProps({
    product: Object
});


</script>

<style scoped>
.product-image {
    width: 150px;
    height: 150px;
}

.no-image {
    background-color: lightgray;
}
</style>
