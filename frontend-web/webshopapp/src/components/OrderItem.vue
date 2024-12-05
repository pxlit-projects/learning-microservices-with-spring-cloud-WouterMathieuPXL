<template>
    <div>
        {{ orderItem.productName }}
        {{ orderItem.quantity }}x
        € {{ orderItem.price.toFixed(2) }}
        € {{ totalPrice }}
    </div>
</template>

<script setup>
import '@mdi/font/css/materialdesignicons.css';

const props = defineProps({
    orderItem: Object
});

const totalPrice = ((props.orderItem.quantity ?? 0) * (props.orderItem.price ?? 0)).toFixed(2);
</script>
