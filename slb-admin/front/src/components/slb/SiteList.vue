<template>
    <div>
        <el-row>
            <el-col>
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                    <el-breadcrumb-item>站点</el-breadcrumb-item>
                </el-breadcrumb>
            </el-col>
        </el-row>
        <el-row>
            <el-form ref="queryForm" :model="queryForm" label-position="left" label-width="150px" inline class="form">
                <el-form-item label="slb集群" prop="slbId">
                    <el-select size="small" v-model="queryForm.slbId" placeholder="请选择">
                        <el-option v-for="item in slbOptions"
                                   :key="item.value"
                                   :label="item.label"
                                   :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="站点名称" prop="name">
                    <el-input v-model="queryForm.name" size="small"></el-input>
                </el-form-item>
                <el-form-item label="AppId" prop="appId">
                    <el-input v-model="queryForm.appId" size="small"></el-input>
                </el-form-item>
                <el-form-item label="域名" prop="domain">
                    <el-input v-model="queryForm.domain" size="small"></el-input>
                </el-form-item>
                <div style="text-align:center;width: 100%">
                    <el-button type="primary" size="small" @click="onSearchSite">查询</el-button>
                    <el-button type="primary" size="small" @click="resetForm('queryForm')">重置</el-button>
                </div>
            </el-form>
        </el-row>
        <el-row>
            <el-col :span="12">
                <div style="padding-top: 15px"></div>
            </el-col>
            <el-col :span="12">
                <div style="float: right;margin: 5px">
                    <el-button size="small" type="primary" fixed="right" @click="addSite" class="add_button">
                        添加站点
                    </el-button>
                </div>
            </el-col>
        </el-row>
        <el-table :data="sites" style="width: 100%" empty-text="暂无数据" border fit>
            <el-table-column type="expand">
                <template scope="props">
                    <el-form label-position="left" inline class="table-expand">
                        <el-form-item label="域名">
                            <span>{{ props.row.domain }}</span>
                        </el-form-item>
                        <el-form-item label="端口">
                            <span>{{ props.row.port }}</span>
                        </el-form-item>
                        <el-form-item label="path">
                            <span>{{ props.row.path }}</span>
                        </el-form-item>
                        <!--<el-form-item label="负载均衡策略">-->
                            <!--<span>{{ props.row.loadbalance }}</span>-->
                        <!--</el-form-item>-->
                        <el-form-item label="健康检查">
                            <span>{{ props.row.healthUri }}</span>
                        </el-form-item>
                    </el-form>
                </template>
            </el-table-column>
            <el-table-column label="ID" prop="id" align="center" sortable></el-table-column>
            <el-table-column label="站点名" prop="name" align="center"></el-table-column>
            <el-table-column label="SLB" prop="slbId" align="center"></el-table-column>
            <el-table-column label="所在组" prop="group" align="center"></el-table-column>
            <el-table-column label="AppId" prop="appId" align="center"></el-table-column>
            <el-table-column label="在线" align="center">
                <template scope="props">
                    <label v-if="props.row.status == '1'">是</label>
                    <label v-else>否</label>
                </template>
            </el-table-column>
            <el-table-column label="在线版本" prop="onlineVersion" align="center"></el-table-column>

            <el-table-column label="服务器数" align="center">
                <template scope="props">
                    {{props.row.appSiteServers.length}}
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="200">
                <template scope="props">
                    <el-button @click="handleManage(props.row)" size="small">管理</el-button>
                    <el-button @click="handleEdit(props.row)" size="small">修改</el-button>
                    <el-button @click="deleteSite(props.row.id)" type="danger" size="small">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

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

        <el-dialog title="Site" :visible.sync="dialogVisible">
            <el-form label-width="80px" label-position="left" :model="inEditSite" ref="inEditSiteForm" :rules="rules">
                <el-form-item label="站点名" prop="name">
                    <el-input v-model="inEditSite.name"></el-input>
                </el-form-item>
                <el-form-item label="所在组" prop="group">
                    <el-input v-model="inEditSite.group"></el-input>
                </el-form-item>
                <el-form-item label="SLB" prop="slbId">
                    <el-select v-model="inEditSite.slbId" placeholder="请选择">
                        <el-option v-for="item in slbOptions"
                                   :key="item.value"
                                   :label="item.label"
                                   :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="appId" prop="appId">
                    <el-input v-model="inEditSite.appId"></el-input>
                </el-form-item>
                <el-form-item label="域名" prop="domain">
                    <el-input v-model="inEditSite.domain"></el-input>
                </el-form-item>
                <el-form-item label="端口" prop="port">
                    <el-input v-model="inEditSite.port"></el-input>
                </el-form-item>
                <el-form-item label="PATH" prop="path">
                    <el-input v-model="inEditSite.path"></el-input>
                </el-form-item>
                <el-form-item label="健康检查路径" prop="healthUri">
                    <el-input v-model="inEditSite.healthUri"></el-input>
                </el-form-item>
                <!--<el-form-item label="负载均衡策略" prop="loadbalance">-->
                    <!--<el-select v-model="inEditSite.loadbalance" placeholder="请选择">-->
                        <!--<el-option v-for="item in loadbalanceOptions"-->
                                   <!--:key="item.value"-->
                                   <!--:label="item.label"-->
                                   <!--:value="item.value">-->
                        <!--</el-option>-->
                    <!--</el-select>-->
                <!--</el-form-item>-->
                <el-form-item>
                    <el-button type="primary" @click="onSubmit('inEditSiteForm')">保存</el-button>
                    <el-button @click="resetForm('inEditSiteForm')" v-if="!inEditSite.id">重置</el-button>
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

            ...mapActions("siteList", {
                fetchSites: 'fetchSites',
                deleteSiteById: 'deleteSite',
                updateSiteById: 'updateSite',
                createSite: 'createSite',
                fetchSlbs: 'fetchSlbs',
                searchSites: 'searchSites'
            }),

            onSearchSite(queryForm) {
                let data = {
                    queryForm: this.queryForm,
                    size: this.pageSize,
                    page: this.currentPage,
                };

                this.searchSites(data);

            },

            deleteSite(siteId) {
                this.$confirm('确认删除？')
                    .then(() => {
                        let data = {
                            siteId: siteId,
                            size: this.pageSize,
                            page: this.currentPage,
                        };
                        this.deleteSiteById(data).then(function (res) {
                            this.fetchSites(data);
                        }.bind(this));

                    })
                    .catch(() => {
                        this.$message.error('删除失败!');
                    });
            },
            handleManage(site) {
                console.log(site);
                this.$router.push({name: "SiteManage", params: {id: site.id}})
            },
            handleEdit(site) {
                console.log(site);
                this.$router.push({name: "SiteEdit", params: {id: site.id}})
            },
            handleSizeChange(data) {
                this.pageSize = data;
                console.log(this.pageSize)
                this.fetchSites({
                    size: this.pageSize,
                    page: this.currentPage
                });
            },
            handleCurrentChange(data) {
                this.currentPage = data;
                console.log(this.currentPage)
                this.fetchSites( {
                    size: this.pageSize,
                    page: this.currentPage
                });
            },
            addSite() {
                console.log(this.inEditSite)
                this.dialogVisible = true;
            },
            onSubmit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        console.log(this.inEditSite);
                        this.dialogVisible = false;

                        let data = {
                            siteList: this.inEditSite,
                            size: this.pageSize,
                            page: this.currentPage,
                        };

                        if (this.inEditSite.id) {
                            this.updateSiteById( data).then(function (res) {
                                console.log(res.data.detail)
                                this.fetchSites(data);
                            }.bind(this));
                        } else {
                            this.createSite( data).then(function (res) {
                                this.fetchSites(data)
                            }.bind(this));
                        }

                    } else {
                        return false;
                    }
                });
            },
            resetForm(formName) {
                console.log(1);
                this.$refs[formName].resetFields();
            },
            onClose() {
                this.inEditSite= {
                    id: null,
                        name: '',
                        group: '',
                        slbId: null,
                        appId: null,
                        domain: '',
                        port: '',
                        path: '',
                        healthUri: 'hs',
                        loadbalance: 'round_robin'
                },
                this.dialogVisible = false;
            },
            onSearch() {
                console.log(this.currentValue);
                this.$store.dispatch('querySites', {
                    pageSize: this.pageSize,
                    page: this.currentPage,
                    field: this.currentField,
                    keyword: this.currentValue
                });
            }
        },

        data: function () {
            return {
                queryForm: {
                    slbId: '',
                    name: '',
                    appId: '',
                    domain: ''
                },
                dialogVisible: false,
                currentField: 'id',
                currentValue: '',
                fieldList: [
                    {
                        value: 'id',
                        label: 'ID'
                    },
                    {
                        value: 'slbId',
                        label: '集群ID'
                    },
                    {
                        value: 'appId',
                        label: 'AppId'
                    },
                    {
                        value: 'name',
                        label: '站点名'
                    }
                ],
                currentPage: 1,
                pageSize: 10,
                inEditSite: {
                    id: null,
                    name: '',
                    group: '',
                    slbId: null,
                    appId: null,
                    domain: '',
                    port: '',
                    path: '',
                    healthUri: 'hs',
                    loadbalance: 'round_robin'
                },
                loadbalanceOptions: [{
                    text: 'round robin',
                    value: 'round_robin'
                }, {
                    text: 'ip hash',
                    value: 'ip_hash'
                }, {
                    text: 'response time',
                    value: 'resp_time'
                }],
                rules: {
                    name: [
                        {required: true, message: '站点名不能为空', trigger: 'blur'}
                    ],
                    slbId: [
                        {type: 'number', required: true, message: '请选择SLB', trigger: 'change'}
                    ],
                    appId: [
                        {required: true, message: 'AppId不能为空', trigger: 'blur'}
                    ],
                    domain: [
                        {required: true, message: '域名不能为空', trigger: 'blur'}
                    ],
                    port: [
                        {required: true, message: '端口不能为空', trigger: 'blur'}
                    ],
                    path: [
                        {required: true, message: 'PATH不能为空', trigger: 'blur'}
                    ]
                }
            }
        },

        computed: {
            ...mapGetters("siteList",{
                sites: 'getSites',
                slbs: 'getSlbs',
                total: 'getTotalSites'
            }),
            slbOptions: function () {
                let options = [];
                this.slbs.forEach(function (slb) {
                    let option = {label: slb.name, value: slb.id};
                    options.push(option);
                });
                if (!this.inEditSite.slbId && this.slbs && this.slbs.length > 0) {
                    this.inEditSite.slbId = this.slbs[0].id
                }
                return options;
            },
            siteOptions: function () {
                let options = [];
                this.sites.forEach(function (site) {
                    let option = {label: site.name, value: site.slbId};
                    options.push(option);
                });
                if (!this.inEditSite.slbId && this.sites && this.sites.length > 0) {
                    this.inEditSite.slbId = this.sites[0].slbId
                }
                return options;
            }
        },
        created() {

            this.fetchSites({
                size: this.pageSize,
                page: this.currentPage
            });

            this.fetchSlbs();
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
    .form .el-form-item {
        margin: 0;
    }

</style>

