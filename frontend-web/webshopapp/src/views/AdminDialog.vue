<template>
    <v-card>
        <v-card-title>{{ isEditMode ? "Edit" : "Add" }} Product</v-card-title>

        <v-card-item>

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
                <v-btn @click="productCatalogStore.closeAdminDialog()">Cancel</v-btn>
            </form>
        </v-card-item>
    </v-card>
</template>

<script setup>

import {useProductCatalogStore} from "@/stores/useProductCatalogStore.js";
import {computed, onMounted, reactive, ref} from "vue";
import {useUserStore} from "@/stores/userStore.js";

const productCatalogStore = useProductCatalogStore();
const userStore = useUserStore();

const selectedLabels = ref([]);

const props = defineProps({
    isEditMode: {
        type: Boolean,
        default: false
    }
});
const product = reactive({
    name: "",
    description: "",
    price: null,
    category: null,
    labelIds: [],
    image: null,
});
onMounted(() => {
    if (productCatalogStore.selectedProduct) {
        Object.assign(product, productCatalogStore.selectedProduct);
        selectedLabels.value = productCatalogStore.selectedProduct.labels.map(label => label.id);

        console.log(productCatalogStore.selectedProduct);
        console.log(product);
    } else {
        Object.assign(product, {
            name: "",
            description: "",
            price: null,
            category: null,
            labelIds: [],
            image: null,
        });
    }
});
const isEditMode = computed(() => !!productCatalogStore.selectedProduct);

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

    if (isEditMode) {
        await productCatalogStore.editProduct(productCatalogStore.selectedProduct.id, formData);
    } else {
        await productCatalogStore.createProduct(formData);
    }
}

const createLabel = async () => {
    console.log(newLabel);
    await productCatalogStore.createLabel(newLabel);
    newLabel.name = 'NEW';
    newLabel.color = 'WHITE';
}

const deleteLabelMode = ref(false);

</script>
