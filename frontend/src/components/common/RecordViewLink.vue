<template>
    <router-link :to="to" custom v-slot="{ href, navigate }">
        <a :href="href" class="record-view-link" @click.stop="navigate">
            <slot>查看</slot>
        </a>
    </router-link>
</template>

<script setup>
defineProps({
    to: { type: [String, Object], required: true }
})
</script>

<style scoped>
.record-view-link {
    display: inline-flex;
    align-items: center;
    padding: 0 8px;
    color: var(--el-color-primary);
    font-weight: 600;
    line-height: 1.5;
    text-decoration: none;
    cursor: pointer;
}

.record-view-link:hover {
    color: var(--el-color-primary-light-3);
}
</style>
