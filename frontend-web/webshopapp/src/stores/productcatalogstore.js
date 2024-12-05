import {ref, computed} from 'vue'
import {defineStore} from 'pinia'
import axios from "axios";

const url = 'http://localhost:8081/api/productcatalog';

export const productCatalogStore = defineStore('productCatalog', {

    state: () => ({
        error: "",
        loading: false,
        products: [],
    }),

    actions: {
        async getProductCatalog() {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.get(url);
                this.products = response.data;
                console.log("sgsg");
                console.log(response.data);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        }
    }
})
