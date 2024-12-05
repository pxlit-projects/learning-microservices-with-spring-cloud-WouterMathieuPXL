<template>
    <header>

        <div class="wrapper">

            <nav>
                <RouterLink to="/">Admin</RouterLink>
                <RouterLink to="/user">User</RouterLink>
            </nav>
        </div>
    </header>
    <br/>
    <RouterView/>
</template>

<script setup>
import {RouterLink, RouterView} from 'vue-router'
</script>
