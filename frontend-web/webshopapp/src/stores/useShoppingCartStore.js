import {defineStore} from 'pinia'
import axios from "axios";

const url = 'http://localhost:8082/api/shoppingcart';

export const useShoppingCartStore = defineStore('shoppingCart', {

    state: () => ({
        error: "",
        loading: false,
        shoppingCartId: 1,
        shoppingCart: {},
        totalQuantity: 0,
        totalPrice: 0,
        order: {},
        orderPrice: 0
    }),

    actions: {
        async getShoppingCart() {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.get(`${url}/${this.shoppingCartId}`);
                this.shoppingCart = response.data;
                this.shoppingCart.shoppingCartItems = this.shoppingCart.shoppingCartItems.sort((a, b) =>
                    a.product.id - b.product.id);
                await this.calculateTotalQuantity();
                await this.calculateTotalPrice();
                console.log(this.shoppingCart.shoppingCartItems);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async editShoppingCart(productId, quantity) {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.put(
                    `${url}/${this.shoppingCartId}/products/${productId}?quantity=${quantity}`);
                this.shoppingCart = response.data;
                this.shoppingCart.shoppingCartItems = this.shoppingCart.shoppingCartItems.sort((a, b) =>
                    a.product.id - b.product.id);
                await this.calculateTotalQuantity();
                await this.calculateTotalPrice();
                console.log(response.data);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async plusProduct(productId) {
            let currentQuantity = this.shoppingCart.shoppingCartItems.find(
                item => item.product.id === productId)?.quantity || 0;
            await this.editShoppingCart(productId, ++currentQuantity);
        },
        async minusProduct(productId) {
            let currentQuantity = this.shoppingCart.shoppingCartItems.find(
                item => item.product.id === productId)?.quantity || 0;
            await this.editShoppingCart(productId, --currentQuantity);
        },
        async deleteProduct(productId) {
            await this.editShoppingCart(productId, 0);
        },
        async calculateTotalQuantity() {
            this.totalQuantity = this.shoppingCart.shoppingCartItems.reduce((total, item) => {
                return total + item.quantity;
            }, 0);
            console.log("Total quantity:", this.totalQuantity);
        },
        async calculateTotalPrice() {
            this.totalPrice = this.shoppingCart.shoppingCartItems.reduce((total, item) => {
                return total + (item.product.price * item.quantity);
            }, 0);
            console.log("Total price:", this.totalPrice);
        },
        async checkOut() {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.post(`${url}/${this.shoppingCartId}/checkout`);
                this.order = response.data;
                console.log(response.data);
                await this.calculateOrderPrice();
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        },
        async calculateOrderPrice() {
            this.orderPrice = this.order.orderItems.reduce((total, item) => {
                return total + (item.price * item.quantity);
            }, 0);
            console.log("Total price:", this.orderPrice);
        }
    }
});
