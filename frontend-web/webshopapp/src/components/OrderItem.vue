<template>
    <v-row>
        <v-col cols="6" class="d-flex justify-left align-center ma-0 pa-0">
            {{ orderItem.productName }}
        </v-col>
        <v-col cols="2" class="d-flex justify-end align-center ma-0 pa-0 px-3">
            {{ orderItem.quantity }}x
        </v-col>
        <v-col cols="2" class="d-flex justify-end align-center ma-0 pa-0">
            € {{ orderItem.price.toFixed(2) }}
        </v-col>
        <v-col cols="2" class="d-flex justify-end align-center ma-0 pa-0">
            € {{ totalPrice }}
        </v-col>
    </v-row>
</template>

<script setup>
import '@mdi/font/css/materialdesignicons.css';

const props = defineProps({
    orderItem: Object
});

const totalPrice = ((props.orderItem.quantity ?? 0) * (props.orderItem.price ?? 0)).toFixed(2);
</script>
