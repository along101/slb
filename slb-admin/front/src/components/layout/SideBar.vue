<template>
    <div class="sidebar">
        <el-menu theme="dark" router>
            <el-menu-item index="/slbs">
                <i class="el-icon-setting"></i>SLB列表
            </el-menu-item>
            <el-menu-item index="/slb/sites">
                <i class="el-icon-setting"></i>站点管理
            </el-menu-item>
            <el-menu-item index="/dashboard">
                <i class="el-icon-setting"></i>数据看板
            </el-menu-item>
        </el-menu>
    </div>
</template>
<script>
    export default {

    }
</script>
<style scoped>
    .sidebar {
        display: block;
        position: absolute;
        width: 250px;
        left: 0;
        top: 70px;
        bottom: 0;
        background: #2E363F;
    }
    .sidebar > ul {
        height: 100%;
    }
    .sidebar i {
        margin-top: -3px;
    }
</style>
