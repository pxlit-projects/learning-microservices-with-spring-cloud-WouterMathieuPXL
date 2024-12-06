import {defineStore} from 'pinia'

export const useUserStore = defineStore('user', {

    state: () => ({
        isAdmin: JSON.parse(localStorage.getItem('isAdmin') || 'false'),
    }),

    getters: {
        role: (state) => (state.isAdmin ? 'ADMIN' : 'USER'),
    },


    actions: {
        toggleAdmin() {
            localStorage.setItem('isAdmin', JSON.stringify(this.isAdmin));
        }
    }
});
