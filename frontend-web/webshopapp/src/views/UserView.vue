<template>
    <main>
        <Product v-for="product in store.products" :key="product.id" :product="product"/>
    </main></template>

<script setup>
import {productCatalogStore} from '@/stores/productcatalogstore';

import {onMounted} from "vue";
import Product from "@/components/Product.vue";

const store = productCatalogStore();

onMounted(async () => await store.getProductCatalog());

</script>
