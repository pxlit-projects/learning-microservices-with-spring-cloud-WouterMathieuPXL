<template>
    <main>
        <v-text-field
            prepend-inner-icon="mdi-magnify"
            solo
            hide-details
            v-model="searchQuery"
        ></v-text-field>

        <v-select
            :items="[ { key: null, value: 'Categorieën' }, ...productCatalogStore.categories ]"
            item-value="key"
            item-title="value"
            v-model="selectedCategory"
            solo
            hide-details
        ></v-select>

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
                        <v-chip filter v-for="label in productCatalogStore.labels"
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
import {useProductCatalogStore} from '@/stores/useProductCatalogStore.js';
import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";

import {computed, onMounted, ref} from "vue";
import Product from "@/components/Product.vue";

const productCatalogStore = useProductCatalogStore();
const shoppingCartStore = useShoppingCartStore();

onMounted(async () => {
    await productCatalogStore.getProductCatalog();
    await productCatalogStore.getLabels();
    await productCatalogStore.getCategories();
    await shoppingCartStore.getShoppingCart();

    console.log("Categories in Component:", productCatalogStore.categories);
});

const searchQuery = ref("");
const selectedCategory = ref(null);
const selectedLabels = ref([]);

const filteredProducts = computed(() => {
    console.log("Search Query:", searchQuery.value);
    console.log("Search Category:", selectedCategory.value);
    console.log("Selected Labels (Value):", selectedLabels.value);

    return productCatalogStore.products.filter((product) => {
            const matchesName = `${product.name} ${product.description}`
                .toLowerCase()
                .includes(searchQuery.value.toLowerCase());

            const matchesCategory =
                selectedCategory.value === null ||
                product.category === selectedCategory.value;

            const matchesLabels =
                selectedLabels.value.length === 0 ||
                product.labels.some((label) =>
                    selectedLabels.value.includes(label.id)
                );

            return matchesName && matchesCategory && matchesLabels;
        }
    );
});

</script>
