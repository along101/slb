<template>
    <div>
        <div>
            <el-row>
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{name: 'Home'}">首页</el-breadcrumb-item>
                        <el-breadcrumb-item :to="{name: 'SlbList'}">SLB列表</el-breadcrumb-item>
                        <el-breadcrumb-item>SLB</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>

            <el-row>
                <el-col>
                    <el-button @click="editSlb" class="edit-button" type="primary" size="small">编辑</el-button>
                </el-col>
            </el-row>
            <el-row class="detail-form">
                <el-col :span="2">
                    <el-tag class="">ID</el-tag>
                </el-col>
                <el-col :span="10">
                    <el-tag type="gray">{{this.slbStatus.id}}</el-tag>
                </el-col>
                <el-col :span="2">
                    <el-tag>集群名</el-tag>
                </el-col>
                <el-col :span="10">
                    <el-tag type="gray">{{this.slbStatus.name}}</el-tag>
                </el-col>
                <el-col :span="2">
                    <el-tag>nginx conf</el-tag>
                </el-col>
                <el-col :span="10">
                    <el-tag type="gray">{{this.slbStatus.nginxConf}}</el-tag>
                </el-col>
                <el-col :span="2">
                    <el-tag>nginx sbin</el-tag>
                </el-col>
                <el-col :span="10">
                    <el-tag type="gray">{{this.slbStatus.nginxSbin}}</el-tag>
                </el-col>
            </el-row>
        </div>
        <br/>

        <el-row>
            <el-col :span="12">
                <div class="table-title">服务器列表</div>
            </el-col>
            <el-col :span="12">
                <el-button @click="addSlbServer" class="add-button" type="primary" size="small">添加服务器</el-button>
            </el-col>
        </el-row>

        <div>
            <el-table :data="slbServers" style="width: 100%" empty-text="暂无数据" border fit>
                <el-table-column label="ID" prop="id" align="center" sortable></el-table-column>
                <el-table-column label="IP" prop="ip" align="center"></el-table-column>
                <el-table-column label="hostName" prop="hostName" align="center"></el-table-column>
                <el-table-column label="操作" align="center">
                    <template scope="props">
                        <el-button size="small">编辑</el-button>
                        <el-button @click="deleteSlbServer(props.row.id)" type="danger" size="small">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <el-dialog title="SLB" :visible.sync="slbDialogVisible">
            <el-form label-width="80px" label-position="top" :model="inEditSlb" ref="inEditSlbForm" :rules="slbRules">
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
                    <el-button type="primary" @click="onSlbSubmit('inEditSlbForm')">保存</el-button>
                    <el-button @click="onCloseSlbDialog" class="close-button">关闭</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog title="SLB Server" :visible.sync="serverDialogVisible">
            <el-form label-width="80px" label-position="top" :model="inEditSlbServer" ref="inEditSlbServerForm" :rules="serverRules">
                <el-form-item label="ip" prop="ip">
                    <el-input v-model="inEditSlbServer.ip"></el-input>
                </el-form-item>
                <el-form-item label="host name" prop="hostName">
                    <el-input v-model="inEditSlbServer.hostName"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onServerSubmit('inEditSlbServerForm')">保存</el-button>
                    <el-button @click="resetForm">重置</el-button>
                    <el-button @click="onCloseServerDialog" class="close-button">关闭</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex';

    export default {

        methods:{
            editSlb() {
                this.inEditSlb = {
                    id: this.slbStatus.id,
                    name: this.slbStatus.name,
                    nginxConf: this.slbStatus.nginxConf,
                    nginxSbin: this.slbStatus.nginxSbin
                };
                this.slbDialogVisible = true;
            },
            addSlbServer(){
                this.inEditSlbServer = {
                    id: null,
                    ip: '',
                    hostName: ''
                };
                this.serverDialogVisible = true;
            },
            deleteSlbServer(slbServerId) {
                this.$confirm('确认删除？')
                    .then(() => {
                        let data = {
                            slbId: this.slbId,
                            slbServerId: slbServerId
                        };
                        this.$store.dispatch('deleteSlbServer', data);
                    })
                    .catch(() => {
                        this.$message.error('删除失败!');
                    });
            },
            onSlbSubmit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.slbDialogVisible = false;
                        let data = {
                            slb: this.inEditSlb
                        };
                        this.$store.dispatch('updateSlb', data);
                    } else {
                        return false;
                    }
                });
            },
            onServerSubmit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.serverDialogVisible = false;
                        let data = {
                            slbServer: this.inEditSlbServer,
                            slbId: this.slbId
                        };
                        this.$store.dispatch('createSlbServer', data);
                    } else {
                        return false;
                    }
                });
            },
            onCloseSlbDialog() {
                this.slbDialogVisible = false;
            },
            onCloseServerDialog() {
                this.serverDialogVisible = false;
            },
            resetForm() {
                this.inEditSlbServer = {
                    id: null,
                    ip: '',
                    hostName: ''
                };
            }
        },

        data: function() {
            return {
                slbDialogVisible: false,
                serverDialogVisible: false,
                currentPage: 1,
                pageSize: 10,
                inEditSlbServer: {
                    id: null,
                    ip: '',
                    hostName: ''
                },
                inEditSlb: {
                    id: null,
                    name: '',
                    nginxConf: '',
                    nginxSbin: ''
                },
                slbRules: {
                    name: [
                        {required: true, message: '名字不能为空', trigger: 'blur'}
                    ]
                },
                serverRules: {
                    ip: [
                        {required: true, message: 'ip不能为空', trigger: 'blur'}
                    ]
                }
            }
        },

        computed: {
            ...mapGetters({
                slbServers: 'getSlbServers',
                slbStatus: 'getSlbStatus'
            }),
            total() {
                return this.slbServers.length;
            },
            slbId() {
                return this.$route.query.slbId;
            }
        },
        created() {
            this.$store.dispatch('fetchSlbStatus', this.slbId);
        }

    }

</script>

<style>
    .detail-form {
        padding: 25px;
        background-color: #eef1f6;
    }
    .close-button {
        float: right;
    }
    .detail-form button {
        float: right;
        margin-left: 10px;
        margin-top: 16px;
    }
    .detail-form .el-tag {
        margin: 5px 0;
    }
    .table-title {
        color: #48576a;
        padding-top: 15px;
        float: left;
    }
    .add-button, .edit-button {
        float: right;
        margin: 5px;
    }
</style>

