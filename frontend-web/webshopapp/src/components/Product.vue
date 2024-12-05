<template>
    <v-card>
        <v-card-title>{{ product.name }}</v-card-title>
        <v-card-subtitle>
            &euro; {{ product.price.toFixed(2) }}
        </v-card-subtitle>
        <v-card-item>
            <img class="product-image" v-if="product.imageUrl" width="150"
                 :src="`http://localhost:8081/${product.imageUrl}`" alt="{{ product.name }}"/>
            <div v-else class="product-image no-image"/>
        </v-card-item>
        <v-card-item>{{ product.description }}</v-card-item>
        <v-card-item v-for="label in product.labels" :key="label.id">
            <v-chip label size="small" :style="{ backgroundColor: label.color.toLowerCase() }">{{ label.name }}</v-chip>
        </v-card-item>
        <v-card-actions>

            <v-btn prepend-icon="mdi-plus">
                Add to Cart
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script setup>
import '@mdi/font/css/materialdesignicons.css';



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
</style>/