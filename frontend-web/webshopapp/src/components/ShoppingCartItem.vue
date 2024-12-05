<template>
    <div>{{ shoppingCartItem.product.id }}</div>
    <div>{{ shoppingCartItem.product.name }}</div>

    <v-text-field
        v-model="shoppingCartItem.quantity"
        type="number"
        outlined
    />
    <v-btn icon="mdi-minus"
           class="mx-2"
           @click="shoppingCartStore.minusProduct(shoppingCartItem.product.id)"
           :disabled="shoppingCartItem.quantity <= 1"/>
    <v-btn icon="mdi-plus"
           @click="shoppingCartStore.plusProduct(shoppingCartItem.product.id)"/>
    <v-btn icon="mdi-delete"
           @click="shoppingCartStore.deleteProduct(shoppingCartItem.product.id)"/>

    € {{ shoppingCartItem.product.price.toFixed(2) }} - € {{ totalPrice }}
</template>

<script setup>
import '@mdi/font/css/materialdesignicons.css';
import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";
import {computed} from "vue";

const shoppingCartStore = useShoppingCartStore();

const props = defineProps({
    shoppingCartItem: Object
});

const totalPrice = computed(() => {
    return (props.shoppingCartItem.quantity * props.shoppingCartItem.product.price).toFixed(2);
});
</script>
