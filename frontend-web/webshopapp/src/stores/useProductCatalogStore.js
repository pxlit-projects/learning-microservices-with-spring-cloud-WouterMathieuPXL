import {defineStore} from 'pinia'
import axios from "axios";

const url = 'http://localhost:8084/productcatalog/api/productcatalog';

export const useProductCatalogStore = defineStore('productCatalog', {

    state: () => ({
        error: "",
        loading: false,
        products: [],
        categories: [],
        labels: []
    }),

    actions: {
        async getProductCatalog() {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.get(url);
                this.products = response.data;
                console.log(response.data);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async getCategories() {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.get(`${url}/categories`);
                this.categories = Object.entries(response.data).map(([key, value]) => ({ key, value }));
                console.log(this.categories);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async getLabels() {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.get(`${url}/labels`);
                this.labels = response.data;
                console.log(response.data);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async createProduct(formData) {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.post(url,
                    formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                        },
                    });

                console.log(response.data);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        }
    }
});
