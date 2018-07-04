<template>
    <div>
        <div>
            <el-row>
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>活动管理</el-breadcrumb-item>
                        <el-breadcrumb-item>活动列表</el-breadcrumb-item>
                        <el-breadcrumb-item>活动详情</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
            <el-row class="query-form">
                <el-col :span="3">
                    <el-select v-model="currentField" placeholder="请选择">
                        <el-option v-for="item in fieldList" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6" :offset="1">
                    <el-input v-model="currentValue" placeholder="请输入内容" @change="onSearch"></el-input>
                </el-col>
                <el-col :span="2" :offset="1">
                    <el-button type="primary" @click="onSearch">搜索</el-button>
                </el-col>
                <el-button type="primary" fixed="right" @click="addSlb" class="add_button">添加SLB</el-button>
            </el-row>
        </div>
        <br/>

        <div>
            <el-table :data="slbs" fit style="width: 100%" empty-text="暂无数据">
                <el-table-column label="ID" prop="id" align="center" sortable></el-table-column>
                <el-table-column label="集群名" prop="name" align="center"></el-table-column>
                <el-table-column label="nginx_conf" prop="nginxConf" align="center"></el-table-column>
                <el-table-column label="nginx_sbin" prop="nginxSbin" align="center"></el-table-column>
                <el-table-column label="SLB服务器" align="center">
                    <template scope="props">
                        {{props.row.slbServers.length}}
                        <router-link :to="{name: 'slbserverlist', query: { slbId: props.row.id }}">查看</router-link>
                    </template>
                </el-table-column>
                <el-table-column label="站点" align="center">
                    <template scope="props">
                        <router-link :to="{name: 'sitelist', query: { slbId: props.row.id }}">查看</router-link>
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="250">
                    <template scope="props">
                        <el-button @click="handleEdit(props.row)"  size="small">编辑</el-button>
                        <el-button @click="deleteSlb(props.row.id)" type="danger" size="small">删除</el-button>
                    </template>
                </el-table-column>

                <!--<template slot="actions" scope="item">
                    <b-btn size="sm" variant="primary" @click="editSlb(item.item)">编辑</b-btn>
                    <b-btn size="sm" variant="primary" @click="deleteSlbById(item.item.id)">删除</b-btn>
                </template>

                <el-table-column fixed="right" label="操作" width="100">
                    <template scope="scope">
                        <el-button @click="handleView" type="text" size="small">查看</el-button>
                        <br/>
                        <el-button @click="handleEdit(scope)" type="text" size="small">编辑</el-button>
                    </template>
                </el-table-column>-->
            </el-table>
        </div>

        <div align='center' style="margin-top: 10px">
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[10, 20, 30, 40]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
            </el-pagination>
        </div>

        <el-dialog title="SLB" :visible.sync="dialogVisible">
            <el-form label-width="80px" label-position="top">
                <el-form-item label="名字">
                    <el-input v-model="inEditSlb.name"></el-input>
                </el-form-item>
                <el-form-item label="nginx conf">
                    <el-input v-model="inEditSlb.nginxConf"></el-input>
                </el-form-item>
                <el-form-item label="nginx sbin">
                    <el-input v-model="inEditSlb.nginxSbin"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit">保存</el-button>
                    <el-button @click="resetForm" v-if="inEditSlb.id==''">重置</el-button>
                    <el-button @click="onClose" class="close-button">关闭</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex';

    export default {

        methods:{
            handleDelete() {

            },
            handleEdit(slb){
                console.log(slb);
                this.inEditSlb = {
                    id: slb.id,
                    name: slb.name,
                    nginxConf: slb.nginxConf,
                    nginxSbin: slb.nginxSbin
                };
                this.dialogVisible= true;
            },
            handleSizeChange(){
                this.$store.dispatch('queryDeploy',{
                    pageSize:this.pageSize,
                    page:this.currentPage,
                    field:this.currentField,
                    keyword:this.currentValue
                });
            },
            handleCurrentChange(){
                this.$store.dispatch('queryDeploy',{
                    pageSize:this.pageSize,
                    page:this.currentPage,
                    field:this.currentField,
                    keyword:this.currentValue
                });
            },
            handleClose(done) {
                this.$confirm('确认关闭？')
                    .then(_ => {
                        done();
                    })
                    .catch(_ => {});
            },
            addSlb(){
                console.log(this.inEditSlb)
                this.inEditSlb={
                    id: '',
                    name: '',
                    nginxConf:'',
                    nginxSbin:'',
                };
                this.dialogVisible= true;
            },
            onSubmit(){
                console.log(this.inEditSlb);
                this.dialogVisible= false;
                let data = {
                    slb: this.inEditSlb
                };
                // this.inEditSlb.id为所要更新的SLB ID
                // 如果 this.inEditSlb.id 为空，表示新增SLB，否则为更新SLB
                if (this.inEditSlb.id) {
                    this.$store.dispatch('updateSlb', data);
                } else {
                    this.$store.dispatch('createSlb', data);
                }
            },
            onClose(){
                this.dialogVisible= false;
            },
            deleteSlb(slbId){
                let data = {
                    slbId: slbId
                };
                this.$store.dispatch('deleteSlb', data);
            },
            onSearch(){
                console.log(this.currentValue);
                this.$store.dispatch('queryDeploy',{
                    pageSize:this.pageSize,
                    page:this.currentPage,
                    field:this.currentField,
                    keyword:this.currentValue
                });
            },
            resetForm() {
                this.inEditSlb={
                    id: '',
                    name: '',
                    nginxConf:'',
                    nginxSbin:'',
                };
            },
            refershList(){
                this.$store.dispatch('fetchSlbs');
            },
        },

        data: function(){
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
                total: 50,
                pageSize: 10,
                inEditSlb: {
                    id: '',
                    name: '',
                    nginxConf: '',
                    nginxSbin: ''
                }
            }
        },

        computed:{
            ...mapGetters({
                slbs: 'getSlbs'
            })
        },
        created (){
            this.$store.dispatch('fetchSlbs');
        }

    }

</script>

<style>
    .close-button {
        float: right;
    }
    .add_button {
        float: right;
    }
</style>

