import {defineStore} from 'pinia'
import axios from "axios";

const url = 'http://localhost:8084/productcatalog/api/productcatalog';

export const useProductCatalogStore = defineStore('productCatalog', {

    state: () => ({
        error: "",
        loading: false,
        products: [],
        categories: [],
        labels: [],
        labelColors: [],
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
                this.categories = Object.entries(response.data).map(([key, value]) => ({key, value}));
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
        async getLabelColors() {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.get(`${url}/labels/colors`);
                this.labelColors = response.data;
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
                this.products.push(response.data);
                console.log(response.data);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async deleteProduct(id) {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.delete(`${url}/${id}`);
                console.log(response.data);
                this.products = this.products.filter(product => product.id !== id);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async createLabel(label) {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.post(`${url}/labels`, label);
                this.labels.push(response.data);
                console.log(response.data);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async deleteLabel(id) {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.delete(`${url}/labels/${id}`);
                this.labels = this.labels.filter(labels => labels.id !== id);
                console.log(response.data);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
    }
});
