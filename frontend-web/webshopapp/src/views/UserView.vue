<template>
    <LogBookView v-if="userStore.isAdmin"/>
    <ShoppingCartView v-else/>
</template>

<script setup>
import {useUserStore} from "@/stores/userStore.js";
import LogBookView from "@/views/LogBookView.vue";
import ShoppingCartView from "@/views/ShoppingCartView.vue";

const userStore = useUserStore();
</script>
