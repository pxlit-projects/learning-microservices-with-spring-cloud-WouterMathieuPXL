import {defineStore} from 'pinia'
import axios from "axios";

const url = 'http://localhost:8084/logbook/api/logbook';

export const useLogBookStore = defineStore('logBook', {

    state: () => ({
        error: "",
        loading: false,
        auditLogs: [],
    }),

    actions: {
        async getAuditLogs() {
            this.error = "";
            this.loading = true;
            try {
                const response = await axios.get(url);
                this.auditLogs = response.data;
                this.auditLogs.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
                console.log(this.auditLogs);
            } catch (error) {
                console.log(error);
                this.error = error.message || 'Failed to fetch data';
            } finally {
                this.loading = false;
            }
        }
    }
});
