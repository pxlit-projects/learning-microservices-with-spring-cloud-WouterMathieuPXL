import {defineStore} from 'pinia'

export const useUserStore = defineStore('user', {

    state: () => ({
        error: "",
        isAdmin: false,
    }),


    actions: {
        async toggleAdmin() {
            this.isAdmin = !(this.isAdmin);
            console.log(this.isAdmin)
        }
    }
});
