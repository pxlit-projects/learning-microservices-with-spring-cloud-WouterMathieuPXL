<template>
    <main>
        <v-text-field
            label="Zoeken"
            prepend-inner-icon="mdi-magnify"
            solo
            hide-details
        ></v-text-field>

        <v-expansion-panels>
            <v-expansion-panel>
                <v-expansion-panel-title>
                    Labels
                </v-expansion-panel-title>
                <v-expansion-panel-text>


                    <v-chip-group
                        column
                        multiple
                        v-model="selectedLabels"
                    >
                        <v-chip filter v-for="label in store.labels"
                                :key="label.id"
                                :value="label.id"
                                :style="{ backgroundColor: label.color.toLowerCase() }">
                            {{ label.name }}
                        </v-chip>
                    </v-chip-group>
                </v-expansion-panel-text>
            </v-expansion-panel>
        </v-expansion-panels>

        <Product v-for="product in filteredProducts" :key="product.id" :product="product"/>

    </main>
</template>

<script setup>
import {productCatalogStore} from '@/stores/productcatalogstore';

import {computed, onMounted, ref} from "vue";
import Product from "@/components/Product.vue";

const store = productCatalogStore();

onMounted(async () => {
    await store.getProductCatalog();
    await store.getLabels();
});
const selectedLabels = ref([]); // Geselecteerde labels

const filteredProducts = computed(() => {
    console.log("Selected Labels (Proxy):", selectedLabels);
    console.log("Selected Labels (Value):", selectedLabels.value);

    if (selectedLabels.value.length === 0) {
        return store.products;
    }

    return store.products.filter((product) =>
        product.labels.some((label) =>
            selectedLabels.value.includes(label.id)
        )
    );
});


</script>
