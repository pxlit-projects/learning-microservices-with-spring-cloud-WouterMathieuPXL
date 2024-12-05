<template>
    <header>

        <div class="wrapper">

            <nav>

                <RouterLink to="/">Admin</RouterLink>
                <RouterLink to="/user">User</RouterLink>
                <v-badge
                    :content="quantity"
                    overlap
                    color="red"
                >
                    <v-btn icon="mdi-cart" :to="'/shoppingcart'" variant="text"/>
                </v-badge>
            </nav>
        </div>
    </header>
    <br/>
    <RouterView/>
</template>

<script setup>
import {RouterLink, RouterView} from 'vue-router'

import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";
import {computed} from "vue";

const shoppingCartStore = useShoppingCartStore();

const quantity = computed(() => shoppingCartStore.totalQuantity);


</script>

<style scoped>


</style>
