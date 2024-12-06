<template>
    <v-row class="ma-0 pa-0">
        <v-col cols="1" class=" text-caption mb-3 pa-0">
nr.
        </v-col>
        <v-col cols="3" class=" text-caption ma-0 pa-0">
            Date time
        </v-col>
        <v-col cols="1" class=" text-caption ma-0 pa-0">
            By
        </v-col>
        <v-col cols="1" class="text-caption ma-0 pa-0">
            Product
        </v-col>
        <v-col cols="6" class="text-caption ma-0 pa-0">
            action
        </v-col>
    </v-row>
    <v-row v-for="auditLog in logBookStore.auditLogs" :key="auditLog.id" class="ma-0 pa-0">
        <v-col cols="1" class=" text-caption ma-0 pa-0">
            # {{ auditLog.id }}
        </v-col>
        <v-col cols="3" class=" text-caption ma-0 pa-0">
            {{ formatDate(auditLog.timestamp) }}
        </v-col>
        <v-col cols="1" class=" text-caption ma-0 pa-0">
            {{ auditLog.performedBy }}
        </v-col>
        <v-col cols="1" class="text-caption ma-0 pa-0">
            {{ auditLog.productId }}
        </v-col>
        <v-col cols="6" class="text-caption ma-0 pa-0">
            {{ auditLog.action }}
        </v-col>
    </v-row>
</template>

<script setup>
import {useLogBookStore} from "@/stores/logBookStore.js";
import {onMounted} from "vue";

const logBookStore = useLogBookStore();
onMounted(() => {
    logBookStore.getAuditLogs();
})

const formatDate = (isoString) => {
    const date = new Date(isoString);

    const formattedDate = new Intl.DateTimeFormat('nl-NL', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
    }).format(date);

    return formattedDate.replace(',', '')
}

</script>
