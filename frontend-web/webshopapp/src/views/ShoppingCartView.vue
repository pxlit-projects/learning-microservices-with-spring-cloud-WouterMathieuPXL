<template>
    <v-container class="ma-0 pa-0 mt-5">
    <ShoppingCartItem
        v-for="shoppingCartItem in shoppingCartStore.shoppingCart.shoppingCartItems"
        :key="shoppingCartItem.id"
        :shoppingCartItem="shoppingCartItem"
    />
    </v-container>
    <v-row class="ma-0 my-4 pa-0">
        <v-col cols="10" class="d-flex justify-end ma-0 pa-0">
            Totale prijs:
        </v-col>
        <v-col cols="2" class="d-flex justify-end ma-0 pa-0">
            €    {{ totalPrice }}
        </v-col>
    </v-row>
    <v-row class="ma-0 my-4 pa-0 d-flex justify-end ">
    <v-btn class="rounded-pill" color="blue" @click="checkout()" :disabled="shoppingCartStore.totalQuantity <= 0">Checkout</v-btn>
    </v-row>

    <v-dialog v-model="isDialogVisible" persistent>
        <v-card>
            <v-card-title>Bestelling # {{ shoppingCartStore.order.id }}</v-card-title>
            <v-card-text>

                <OrderItem v-for="orderItem in shoppingCartStore.order.orderItems"
                           :key="orderItem.id"
                           :orderItem="orderItem"/>
                € {{ orderPrice }}
            </v-card-text>
            <v-card-actions>
                <v-btn @click="isDialogVisible = false">Close</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script setup>
import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";
import ShoppingCartItem from "@/components/ShoppingCartItem.vue";
import {computed, onMounted, ref} from "vue";
import OrderItem from "@/components/OrderItem.vue";

const shoppingCartStore = useShoppingCartStore();

onMounted(async () => {
    await shoppingCartStore.getShoppingCart();

    console.log(shoppingCartStore.shoppingCart);
});

const totalPrice = computed(() => shoppingCartStore.totalPrice.toFixed(2));
const orderPrice = computed(() => shoppingCartStore.orderPrice.toFixed(2));
const isDialogVisible = ref(false);

const checkout = async () => {
    await shoppingCartStore.checkOut();
    isDialogVisible.value = true;
    await shoppingCartStore.getShoppingCart();
}
</script>
