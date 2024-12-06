<template>

    <div v-for="auditLog in logBookStore.auditLogs" :key="auditLog.id">
        {{ auditLog.id }}
        {{ auditLog.productId }}
        {{ auditLog.action }}
        {{ auditLog.performedBy }}
        {{ auditLog.timestamp }}
    </div>


</template>

<script setup>
import {useLogBookStore} from "@/stores/logBookStore.js";
import {onMounted} from "vue";

const logBookStore = useLogBookStore();
onMounted(() => {
    logBookStore.getAuditLogs();
})

</script>
