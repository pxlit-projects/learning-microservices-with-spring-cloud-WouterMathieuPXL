import {defineStore} from 'pinia'

export const useUserStore = defineStore('user', {

    state: () => ({
        isAdmin: JSON.parse(localStorage.getItem('isAdmin') || 'false'),
        adminDialog: false,
    }),

    actions: {
        toggleAdmin() {
            localStorage.setItem('isAdmin', JSON.stringify(this.isAdmin));
        },
        setAdminDialog(value) {
            this.adminDialog = value;
        }
    }
});
