<template>

    <main>
        <v-container class="nav">
            <v-row>
                <v-col cols="3" class="ma-0 pa-0">
                    <v-switch
                        v-model="userStore.isAdmin"
                        @change="userStore.toggleAdmin"
                        label="Admin"
                        hide-details
                        inset
                    />

                </v-col>
                <v-col cols="9" class="d-flex justify-end">
                    <v-btn variant="flat" to="/" class="mt-2 mr-8">Catalog</v-btn>
                    <v-btn variant="flat" v-if="userStore.isAdmin" to="/user" class="mt-2">Logbook</v-btn>
                    <v-badge v-if="!userStore.isAdmin"
                             :content="quantity"
                             overlap
                             color="red"
                    >
                        <v-btn icon="mdi-cart" :to="'/user'" variant="text"/>
                    </v-badge>
                </v-col>

            </v-row>


        </v-container>
        <RouterView/>
    </main>
</template>

<script setup>
import {RouterLink, RouterView} from 'vue-router'

import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";
import {computed, onMounted} from "vue";
import {useUserStore} from "@/stores/userStore.js";
import {useProductCatalogStore} from "@/stores/useProductCatalogStore.js";

const userStore = useUserStore();
const shoppingCartStore = useShoppingCartStore();
const productCatalogStore = useProductCatalogStore();

const quantity = computed(() => shoppingCartStore.totalQuantity);

console.log(`Stored in localStorage: ${localStorage.getItem('isAdmin')}`);

onMounted(async () => {
    await productCatalogStore.getProductCatalog();
    await productCatalogStore.getLabels();
    await productCatalogStore.getLabelColors();
    await productCatalogStore.getCategories();
});


</script>

<style>
body {
    //background-color: lightgray;
}

main {
    max-width: 700px;
    margin: 0 auto;
}
.nav {
    height: 70px;
}
</style>
