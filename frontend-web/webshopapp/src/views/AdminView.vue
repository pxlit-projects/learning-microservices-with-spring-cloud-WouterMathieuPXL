<template>
    <main>






        <form @submit.prevent="submitProduct">
            <v-text-field type="text" v-model="product.name" placeholder="Product Name" required/>
            <v-text-field type="text" v-model="product.description" placeholder="Description" required/>
            <v-text-field type="number" v-model="product.price" placeholder="Price" required/>
            <v-select
                :items="productCatalogStore.categories"
                item-value="key"
                item-title="value"
                v-model="product.category"
                hide-details
            ></v-select>
            <v-btn @click="deleteLabelMode = !deleteLabelMode">
                {{ deleteLabelMode ? 'Select labels' : 'Manage labels' }}
            </v-btn>
            <v-chip-group
                v-if="!deleteLabelMode"
                column
                multiple
                v-model="selectedLabels"
            >
                <v-chip filter v-for="label in productCatalogStore.labels"
                        :key="label.id"
                        :value="label.id"
                        size="x-small"
                        :style="{ backgroundColor: label.color.toLowerCase() }">
                    {{ label.name.toUpperCase() }}
                </v-chip>
            </v-chip-group>

            <v-chip-group
                v-if="deleteLabelMode"

                column
            >
                <v-chip filter v-for="label in productCatalogStore.labels"
                        :key="label.id"
                        :value="label.id"
                        size="x-small"
                        :style="{ backgroundColor: label.color.toLowerCase() }"
                        closable
                        @click:close="() => productCatalogStore.deleteLabel(label.id)">
                    {{ label.name.toUpperCase() }}
                </v-chip>
            </v-chip-group>

            <div v-if="deleteLabelMode">
                NEW LABEL
                <v-text-field type="text" v-model="newLabel.name" placeholder="Label Name" required/>
                <v-chip-group
                    column
                    v-model="newLabel.color"
                >
                    <v-chip filter v-for="color in productCatalogStore.labelColors"
                            :key="color"
                            :value="color"
                            size="x-small"
                            :style="{ backgroundColor: color.toLowerCase() }">
                        {{ newLabel.name.toUpperCase() }}
                    </v-chip>
                </v-chip-group>
                <v-btn @click="createLabel">Create Label</v-btn>
            </div>


            <v-file-input v-model="product.image" label="File input" show-size/>
            <v-btn @click="submitProduct()">Create Product</v-btn>
        </form>
    </main>
</template>

<script setup>

import {useProductCatalogStore} from "@/stores/useProductCatalogStore.js";
import {onMounted, reactive, ref} from "vue";

const productCatalogStore = useProductCatalogStore();

onMounted(async () => {
    await productCatalogStore.getProductCatalog();
    await productCatalogStore.getLabels();
    await productCatalogStore.getLabelColors();
    await productCatalogStore.getCategories();

    console.log("Categories in Component:", productCatalogStore.categories);
});

const selectedLabels = ref([]);

const product = reactive({
    name: 'wgr',
    description: 'wgrg',
    price: null,
    category: null,
    labelIds: 2,
    image: null
});

const newLabel = reactive({
    name: 'NEW',
    color: 'WHITE'
})

const submitProduct = async () => {
    const formData = new FormData();
    formData.append('name', product.name);
    formData.append('description', product.description);
    formData.append('price', product.price);
    formData.append('category', product.category);
    selectedLabels.value.forEach(labelId => {
        formData.append('labelIds', labelId);
    });
    formData.append('labelIds', product.labelIds);
    formData.append('image', product.image);

    await productCatalogStore.createProduct(formData);
}

const createLabel = async () => {
    console.log(newLabel);
    await productCatalogStore.createLabel(newLabel);
    newLabel.name = 'NEW';
    newLabel.color = 'WHITE';
}

const deleteLabelMode = ref(false);

</script>
