<template>
    <v-row class="ma-0 pa-0">
        <v-col cols="1" class="d-flex justify-left align-center ma-0 pa-0">{{ shoppingCartItem.product.id }}</v-col>
        <v-col cols="4" class="d-flex justify-left align-center ma-0 pa-0">{{ shoppingCartItem.product.name }}</v-col>
        <v-col cols="1" class="d-flex justify-end align-center ma-0 pa-0 px-3">
            {{ shoppingCartItem.quantity }}
        </v-col>
        <v-col cols="2" class="d-flex justify-space-between align-center ma-0 pa-0">
            <v-btn icon="mdi-minus"  variant="text"
                   @click="shoppingCartStore.minusProduct(shoppingCartItem.product.id)"
                   :disabled="shoppingCartItem.quantity <= 1"/>

            <v-btn icon="mdi-plus" variant="text"
                   @click="shoppingCartStore.plusProduct(shoppingCartItem.product.id)"/>

            <v-btn icon="mdi-delete" variant="text"
                   @click="shoppingCartStore.deleteProduct(shoppingCartItem.product.id)"/>
        </v-col>
        <v-col cols="4" class="d-flex justify-end align-center ma-0 pa-0">  € {{ shoppingCartItem.product.price.toFixed(2) }} - € {{ totalPrice }}</v-col>
    </v-row>

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
