<template>
    <ShoppingCartItem
        v-for="shoppingCartItem in shoppingCartStore.shoppingCart.shoppingCartItems"
        :key="shoppingCartItem.id"
        :shoppingCartItem="shoppingCartItem"
    />
    <div> Totale prijs: {{ totalPrice }}</div>
    <v-btn @click="checkout()">Checkout</v-btn>
</template>

<script setup>
import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";
import ShoppingCartItem from "@/components/ShoppingCartItem.vue";
import {computed, onMounted} from "vue";

const shoppingCartStore = useShoppingCartStore();

onMounted(async () => {
    await shoppingCartStore.getShoppingCart();

    console.log(shoppingCartStore.shoppingCart);
});

const totalPrice = computed(() => {
    return shoppingCartStore.totalPrice.toFixed(2);
});

const checkout = async () => {
    await shoppingCartStore.checkOut();
    await shoppingCartStore.getShoppingCart();
}
</script>
