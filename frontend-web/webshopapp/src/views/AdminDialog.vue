<template>
    <v-card>
        <v-card-title>{{ isEditMode ? "Edit" : "Add" }} Product</v-card-title>

        <v-card-item>

            <form @submit.prevent="submitProduct">
                <v-text-field type="text" v-model="product.name" placeholder="Name"/>
                <v-text-field type="text" v-model="product.description" placeholder="Description"/>
                <v-text-field type="number" v-model="product.price" placeholder="Price" @input="validatePrice"/>
                <v-select placeholder="Category"
                          :items="productCatalogStore.categories"
                          item-value="key"
                          item-title="value"
                          v-model="product.category"
                          hide-details
                ></v-select>
                <!--                <v-btn @click="deleteLabelMode = !deleteLabelMode">-->
                <!--                    {{ deleteLabelMode ? 'Select labels' : 'Manage labels' }}-->
                <!--                </v-btn>-->

                <v-expansion-panels variant="accordion" v-model="openPanels" class="my-6">
                    <v-expansion-panel>
                        <v-expansion-panel-title>
                            Select labels
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
                                        size="x-small"
                                        :style="{ backgroundColor: label.color.toLowerCase() }">
                                    {{ label.name.toUpperCase() }}
                                </v-chip>
                            </v-chip-group>
                        </v-expansion-panel-text>

                    </v-expansion-panel>

                    <v-expansion-panel>
                        <v-expansion-panel-title>
                            Manage labels
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>


                            <v-chip-group
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

                            <v-row class="mx-0 mb-2 mt-8 pa-0 d-flex align-center">
                                <v-text-field class="ma-0 pa-0" cols="7" type="text" v-model="newLabel.name"
                                              placeholder="Label Name"
                                              dense hide-details outlined/>
                                <v-col cols="1"/>
                                <v-btn prepend-icon="mdi-plus" variant="flat"
                                       cols="4" @click="createLabel">Create label
                                </v-btn>
                            </v-row>
                            <v-row class="ma-0 pa-0">
                                <v-chip-group class="ma-0 pa-0"
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
                            </v-row>
                        </v-expansion-panel-text>
                    </v-expansion-panel>
                </v-expansion-panels>

                <v-file-input v-model="product.image" label="Image" show-size/>
                <v-row class="d-flex justify-center my-4">
                    <v-btn prepend-icon="mdi-plus" color="blue" class="rounded-pill mx-3" variant="flat"
                           @click="submitProduct()" :disabled="!isFormValid">{{ isEditMode ? "Edit" : "Add" }} product
                    </v-btn>
                    <v-btn prepend-icon="mdi-cancel" class="rounded-pill mx-3" variant="flat" color="red"
                           @click="productCatalogStore.closeAdminDialog()">Cancel
                    </v-btn>
                </v-row>
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

const isEditMode = ref(!!productCatalogStore.selectedProduct);


const newLabel = reactive({
    name: 'NEW',
    color: 'WHITE'
})
const openPanels = ref([0]);
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
    if (product.image) {
        formData.append('image', product.image);
    }

    console.log(!!productCatalogStore.selectedProduct)

    if (isEditMode.value) {
        await productCatalogStore.editProduct(productCatalogStore.selectedProduct.id, formData);
    } else {
        await productCatalogStore.createProduct(formData);
    }
    await productCatalogStore.closeAdminDialog();
}
const isFormValid = computed(() => {
    return product.name && product.description && product.price && product.category;
});
const validatePrice = (event) => {
    product.price = event.target.value.replace(/[^0-9.]/g, ''); // Alleen cijfers en punt (.) toestaan
};
const createLabel = async () => {
    console.log(newLabel);
    await productCatalogStore.createLabel(newLabel);
    newLabel.name = 'NEW';
    newLabel.color = 'WHITE';
}

const deleteLabelMode = ref(false);

</script>
