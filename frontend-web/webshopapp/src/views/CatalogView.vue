<template>
    <v-container class="ma-0 pa-0">
        <v-row>
            <v-col cols="8">
                <v-text-field prepend-inner-icon="mdi-magnify"
                              v-model="searchQuery"
                              rounded
                              variant="solo"

                />
            </v-col>
            <v-col cols="4">

                <v-select :items="[ { key: null, value: 'Categorieën' }, ...productCatalogStore.categories ]"
                          item-value="key" item-title="value"
                          v-model="selectedCategory"
                          rounded
                          variant="solo"/>
            </v-col>

        </v-row>


        <!--        <v-expansion-panels>-->
        <!--            <v-expansion-panel>-->
        <!--                <v-expansion-panel-title>-->
        <!--                    Labels-->
        <!--                </v-expansion-panel-title>-->
        <!--                <v-expansion-panel-text>-->
        <v-chip-group column multiple
                      v-model="selectedLabels">
            <v-chip filter v-for="label in productCatalogStore.labels"
                    :key="label.id" :value="label.id"
                    size="x-small"
                    :style="{ backgroundColor: label.color.toLowerCase() }">
                {{ label.name.toUpperCase() }}
            </v-chip>
        </v-chip-group>
        <!--                </v-expansion-panel-text>-->
        <!--            </v-expansion-panel>-->
        <!--        </v-expansion-panels>-->
        <v-row class="d-flex justify-center mt-10 mb-5">
            <v-btn v-if="userStore.isAdmin"
                  icon="mdi-plus"
                   @click="productCatalogStore.openAdminDialog(null)">
            </v-btn>
        </v-row>


        <v-dialog
            v-model="productCatalogStore.adminDialog"
            persistent
            max-width="600px">
            <AdminDialog/>
        </v-dialog>

        <v-row class="ma-0 pa-0">
            <v-col v-for="product in filteredProducts" class="ma-0 pa-1" cols="4">
                <Product class="ma-0 pa-0" cols="4" :key="product.id" :product="product"/>

            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import {useProductCatalogStore} from '@/stores/useProductCatalogStore.js';
import {useShoppingCartStore} from "@/stores/useShoppingCartStore.js";

import {computed, onMounted, ref} from "vue";
import Product from "@/components/Product.vue";
import AdminDialog from "@/views/AdminDialog.vue";
import {useUserStore} from "@/stores/userStore.js";

const userStore = useUserStore();
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
