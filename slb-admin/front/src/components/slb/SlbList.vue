<template>
    <div>
        <div>
            <el-row>
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>SLB列表</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
            <el-row class="query-form">
                <el-col :span="2">
                    <el-select v-model="currentField" placeholder="请选择">
                        <el-option v-for="item in fieldList" :key="item.value" :label="item.label"
                                   :value="item.value"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6" :offset="1">
                    <el-input v-model="currentValue" placeholder="请输入内容"></el-input>
                </el-col>
                <el-col :span="2" :offset="1">
                    <el-button type="primary" @click="onSearch">搜索</el-button>
                </el-col>
                <el-button type="primary" fixed="right" @click="addSlb" class="add_button">添加SLB</el-button>
            </el-row>
        </div>
        <br/>

        <div>
            <el-table :data="slbs" style="width: 100%" empty-text="暂无数据" border fit>
                <el-table-column label="ID" prop="id" align="center" sortable></el-table-column>
                <el-table-column label="集群名" prop="name" align="center"></el-table-column>
                <el-table-column label="nginx conf" prop="nginxConf" align="center"></el-table-column>
                <el-table-column label="nginx sbin" prop="nginxSbin" align="center"></el-table-column>
                <el-table-column label="SLB服务器" align="center">
                    <template scope="props">
                        {{props.row.slbServers.length}}
                    </template>
                </el-table-column>
                <el-table-column label="站点" align="center">
                    <template scope="props">
                        <router-link :to="{name: 'SiteList', query: { slbId: props.row.id }}">查看</router-link>
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center">
                    <template scope="props">
                        <el-button @click="handleManage(props.row.id)" size="small">管理</el-button>
                        <el-button @click="handleDelete(props.row.id)" type="danger" size="small">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <div align='center' style="margin-top: 10px">
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[10, 20, 30, 40]"
                    :page-size.sync="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
            </el-pagination>
        </div>

        <el-dialog title="SLB" :visible.sync="dialogVisible">
            <el-form label-width="80px" label-position="top" :model="inEditSlb" ref="inEditSlbForm" :rules="rules">
                <el-form-item label="名字" prop="name">
                    <el-input v-model="inEditSlb.name"></el-input>
                </el-form-item>
                <el-form-item label="nginx conf" prop="nginxConf">
                    <el-input v-model="inEditSlb.nginxConf"></el-input>
                </el-form-item>
                <el-form-item label="nginx sbin" prop="nginxSbin">
                    <el-input v-model="inEditSlb.nginxSbin"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit('inEditSlbForm')">保存</el-button>
                    <el-button @click="resetForm" v-if="!inEditSlb.id">重置</el-button>
                    <el-button @click="onClose" class="close-button">关闭</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex';

    export default {

        methods: {
            handleManage(slbId) {
                this.$router.push({name: 'Slb', query: { slbId: slbId }});
            },
            handleDelete(slbId) {
                this.$confirm('确认删除？')
                    .then(() => {
                        let data = {
                            slbId: slbId
                        };
                        this.$store.dispatch('deleteSlb', data);
                    })
                    .catch(() => {
                        this.$message.error('删除失败!');
                    });
            },
            handleSizeChange(data) {
                this.pageSize = data;
                this.$store.dispatch('querySlbs', {
                    pageSize: this.pageSize,
                    page: this.currentPage,
                    field: this.currentField,
                    keyword: this.currentValue
                });
            },
            handleCurrentChange(data) {
                this.currentPage = data;
                this.$store.dispatch('querySlbs', {
                    pageSize: this.pageSize,
                    page: this.currentPage,
                    field: this.currentField,
                    keyword: this.currentValue
                });
            },
            addSlb() {
                console.log(this.inEditSlb)
                this.inEditSlb = {
                    id: null,
                    name: '',
                    nginxConf: '',
                    nginxSbin: ''
                };
                this.dialogVisible = true;
            },
            onSubmit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        let self = this;
                        this.dialogVisible = false;
                        let data = {
                            slb: self.inEditSlb,
                            resolve: function(slb) {
                                self.$router.push({name: 'Slb', query: { slbId: slb.id }});
                            }
                        };
                        this.$store.dispatch('createSlb', data);
                    } else {
                        return false;
                    }
                });
            },
            onClose() {
                this.dialogVisible = false;
            },
            onSearch() {
                console.log(this.pageSize + this.currentPage + this.currentField + this.currentValue);
                this.$store.dispatch('querySlbs', {
                    pageSize: this.pageSize,
                    page: this.currentPage,
                    field: this.currentField,
                    keyword: this.currentValue
                });
            },
            resetForm() {
                this.inEditSlb = {
                    id: null,
                    name: '',
                    nginxConf: '',
                    nginxSbin: ''
                }
            }
        },

        data: function () {
            return {
                dialogVisible: false,
                currentField: 'id',
                currentValue: '',
                fieldList: [{
                    value: 'id',
                    label: 'ID'
                }, {
                    value: 'name',
                    label: '集群名'
                }],
                currentPage: 1,
                pageSize: 10,
                inEditSlb: {
                    id: null,
                    name: '',
                    nginxConf: '',
                    nginxSbin: ''
                },
                rules: {
                    name: [
                        {required: true, message: '名字不能为空', trigger: 'blur'}
                    ]
                }
            }
        },

        computed: {
            ...mapGetters({
                slbs: 'getSlbs',
                total: 'getTotalSlbs'
            })
        },
        created (){
            this.$store.dispatch('fetchSlbs');
        }

    }

</script>

<style>
    .query-form {
        margin-top: 20px;
        padding: 25px;
        background-color: #eef1f6;
    }
    .close-button {
        float: right;
    }

    .add_button {
        float: right;
    }
</style>

