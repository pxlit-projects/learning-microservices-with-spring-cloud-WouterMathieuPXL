<template>
    <header>

        <div class="wrapper">
            <v-switch
                :v-model="userStore.isAdmin"
                @change="userStore.toggleAdmin"
                label="Admin"
                hide-details
                inset
                color="primary"
            />
            <nav>

                <RouterLink to="/">Catalog</RouterLink>
                <RouterLink v-if="userStore.isAdmin" to="/user">Admin</RouterLink>
                <v-badge v-if="!userStore.isAdmin"
                    :content="quantity"
                    overlap
                    color="red"
                >
                    <v-btn icon="mdi-cart" :to="'/user'" variant="text"/>
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
import {useUserStore} from "@/stores/userStore.js";

const userStore = useUserStore();
const shoppingCartStore = useShoppingCartStore();

const quantity = computed(() => shoppingCartStore.totalQuantity);


</script>

<style scoped>


</style>
