<template>
    <div>
        <el-row>
            <el-col :span="12">
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                    <el-breadcrumb-item :to="{ name: 'SiteList' }">站点</el-breadcrumb-item>
                    <el-breadcrumb-item>站点修改</el-breadcrumb-item>
                    <el-breadcrumb-item>{{ $route.params.id }}</el-breadcrumb-item>
                </el-breadcrumb>
            </el-col>
            <el-col :span="12">
                <div style="float: right">
                    <el-button size="small" @click="onRefresh()">刷新</el-button>
                    <el-button size="small" @click="onSiteManage()">管理</el-button>
                    <el-button type="primary" size="small" @click="onPreviewNginxConf()">预览nginx配置</el-button>
                    <el-button type="primary" size="small" @click="onRevertOnline()">还原</el-button>
                    <el-button type="primary" size="small" @click="onDeploy()">提交</el-button>
                </div>
            </el-col>
        </el-row>
        <el-row>
            <el-form ref="form" :model="site" label-position="left" label-width="150px" inline class="form">
                <el-form-item label="站点ID">
                    <span>{{ site.id }}</span>
                </el-form-item>
                <el-form-item label="线上版本/修改版本">
                    <span>{{ site.onlineVersion }}/{{site.offlineVersion}}</span>
                </el-form-item>
                <el-form-item label="站点名称">
                    <el-input v-model="site.name" size="small"></el-input>
                </el-form-item>
                <el-form-item label="AppId">
                    <el-input v-model="site.appId" size="small"></el-input>
                </el-form-item>
                <el-form-item label="slb集群">
                    <el-input v-model="site.slbId" size="small"></el-input>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="site.status" size="small" placeholder="请选择">
                        <el-option
                                v-for="item in statusOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="域名">
                    <el-input v-model="site.domain" size="small"></el-input>
                </el-form-item>
                <el-form-item label="端口">
                    <el-input v-model="site.port" size="small"></el-input>
                </el-form-item>
                <el-form-item label="访问路径">
                    <el-input v-model="site.path" size="small"></el-input>
                </el-form-item>
                <!--<el-form-item label="负载均衡策略">-->
                <!--<el-input v-model="site.loadbalance" size="small"></el-input>-->
                <!--</el-form-item>-->
                <el-form-item label="健康检查Uri">
                    <el-input v-model="site.healthUri" size="small"></el-input>
                </el-form-item>
                <el-form-item label="说明">
                    <el-input v-model="site.note" size="small"></el-input>
                </el-form-item>
                <div style="text-align:center;width: 100%">
                    <el-button type="primary" size="small" @click="onSave()">保存</el-button>
                    <el-button type="primary" size="small" @click="onReset()">重置</el-button>
                </div>
            </el-form>
        </el-row>
        <el-row>
            <el-col :span="12">
                <div style="padding-top: 15px">站点服务器列表</div>
            </el-col>
            <el-col :span="12">
                <div style="float: right;margin: 5px">
                    <el-button type="primary" size="small" @click="onAddSiteServer()">增加</el-button>
                    <el-button type="danger" size="small" @click="onBatchDeleteSiteServers">删除</el-button>
                    <el-dropdown @command="(command)=>{this.onBatchOperate('ops_pull',command)}" trigger="click">
                        <el-button type="primary" size="small">
                            发布状态<i class="el-icon-caret-bottom el-icon--right"></i>
                        </el-button>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="1">拉入</el-dropdown-item>
                            <el-dropdown-item command="0">拉出</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                    <el-dropdown @command="(command)=>{this.onBatchOperate('ops_health_check',command)}"
                                 trigger="click">
                        <el-button type="primary" size="small">
                            健康检查状态<i class="el-icon-caret-bottom el-icon--right"></i>
                        </el-button>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="1">拉入</el-dropdown-item>
                            <el-dropdown-item command="0">拉出</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                    <el-dropdown @command="(command)=>{this.onBatchOperate('ops_manual',command)}" trigger="click">
                        <el-button type="primary" size="small">
                            手工拉入状态<i class="el-icon-caret-bottom el-icon--right"></i>
                        </el-button>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="1">拉入</el-dropdown-item>
                            <el-dropdown-item command="0">拉出</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>
            </el-col>
        </el-row>
        <el-table ref="multipleTable" :data="site.appSiteServers" style="width: 100%"
                  @selection-change="onSelectionChange" emptyText="暂无数据" border fit>
            <el-table-column type="selection" align="center" width="55"></el-table-column>
            <el-table-column type="expand" width="55">
                <template scope="props">
                    <el-form labelPosition="left" inline class="table-expand">
                        <el-form-item label="主机名">
                            <span>{{ props.row.hostName }}</span>
                        </el-form-item>
                        <el-form-item label="权重">
                            <span>{{ props.row.weight }}</span>
                        </el-form-item>
                        <el-form-item label="最大失败数">
                            <span>{{ props.row.maxFails }}</span>
                        </el-form-item>
                        <el-form-item label="超时时间">
                            <span>{{ props.row.failTimeout }}</span>
                        </el-form-item>
                    </el-form>
                </template>
            </el-table-column>
            <el-table-column label="ID" prop="id" align="center" width="60"></el-table-column>
            <el-table-column label="名称" prop="name" align="center"></el-table-column>
            <el-table-column label="标签" prop="tag" align="center"></el-table-column>
            <el-table-column label="ip" prop="ip" align="center"></el-table-column>
            <el-table-column label="端口" prop="port" align="center"></el-table-column>
            <el-table-column label="终状态" prop="status" align="center">
                <template scope="props">
                    <el-tag color="#13CE66"
                            v-if="props.row.statusDetail.ops_pull == 1&& props.row.statusDetail.ops_health_check == 1 && props.row.statusDetail.ops_manual== 1">
                        up
                    </el-tag>
                    <el-tag color="#FF4949" v-else>down</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="发布状态" prop="statusDetail.ops_pull" align="center">
                <template scope="props">
                    <el-tag color="#13CE66" v-if="props.row.statusDetail.ops_pull == 1">up</el-tag>
                    <el-tag color="#FF4949" v-else>down</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="健康检查状态" prop="statusDetail.ops_health_check" align="center">
                <template scope="props">
                    <el-tag color="#13CE66" v-if="props.row.statusDetail.ops_health_check == 1">up</el-tag>
                    <el-tag color="#FF4949" v-else>down</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="手工拉入状态" prop="statusDetail.ops_manual" align="center">
                <template scope="props">
                    <el-tag color="#13CE66" v-if="props.row.statusDetail.ops_manual == 1">up</el-tag>
                    <el-tag color="#FF4949" v-else>down</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
                <template scope="props">
                    <el-button @click="onEditSiteServer(props.row)" size="small">修改</el-button>
                    <el-button @click="onDeleteSiteServer(props.row)" type="danger" size="small">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="site server" :visible.sync="siteServerDialogVisible">
            <el-form labelWidth="150px" labelPosition="left" :model="siteServerEdit" ref="siteServerForm">
                <el-form-item label="ID">
                    <el-input v-model="siteServerEdit.id" disabled></el-input>
                </el-form-item>
                <el-form-item label="名称">
                    <el-input v-model="siteServerEdit.name"></el-input>
                </el-form-item>
                <el-form-item label="主机名">
                    <el-input v-model="siteServerEdit.hostName"></el-input>
                </el-form-item>
                <el-form-item label="标签">
                    <el-input v-model="siteServerEdit.tag"></el-input>
                </el-form-item>
                <el-form-item label="IP">
                    <el-input v-model="siteServerEdit.ip"></el-input>
                </el-form-item>
                <el-form-item label="端口">
                    <el-input v-model="siteServerEdit.port"></el-input>
                </el-form-item>
                <el-form-item label="权重">
                    <el-input v-model="siteServerEdit.weight"></el-input>
                </el-form-item>
                <el-form-item label="最大失败数">
                    <el-input v-model="siteServerEdit.maxFails"></el-input>
                </el-form-item>
                <el-form-item label="超时时间">
                    <el-input v-model="siteServerEdit.failTimeout"></el-input>
                </el-form-item>
                <el-form-item label="上线状态">
                    <el-checkbox :checked="siteServerEdit.statusDetail.ops_pull===1"
                                 @change="siteServerEdit.statusDetail.ops_pull=$event.target.checked?1:0">发布
                    </el-checkbox>
                    <el-checkbox :checked="siteServerEdit.statusDetail.ops_health_check===1"
                                 @change="siteServerEdit.statusDetail.ops_health_check=$event.target.checked?1:0">健康检查
                    </el-checkbox>
                    <el-checkbox :checked="siteServerEdit.statusDetail.ops_manual===1"
                                 @change="siteServerEdit.statusDetail.ops_manual=$event.target.checked?1:0">手工拉入
                    </el-checkbox>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onServerSubmit('siteServerForm')" size="small">
                        {{isAddServer ? '增加' : '保存'}}
                    </el-button>
                    <el-button @click="siteServerDialogVisible=false" class="close-button" size="small">关闭</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
        <el-dialog title="nginx配置" :visible.sync="nginxConfDialogVisible">
            <pre>{{nginxConf}}</pre>
        </el-dialog>
    </div>
</template>

<script>
    import {api} from '../../api'

    export default {
        data() {
            return {
                siteServerDialogVisible: false,
                nginxConfDialogVisible: false,
                nginxConf: "",
                isAddServer: false,
                statusOptions: [{
                    value: 0,
                    label: "down"
                }, {
                    value: 1,
                    label: "up"
                }],
                site: {},
                siteServerEdit: {},
                selectedSiteServers: [],
            }
        },
        computed: {},
        methods: {
            initSiteServerEdit() {
                this.siteServerEdit = {
                    id: "",
                    name: "",
                    hostName: "",
                    ip: "",
                    port: "",
                    weight: "",
                    maxFails: "",
                    failTimeout: "",
                    tag: "",
                    statusDetail: {'ops_pull': 1, 'ops_health_check': 1, 'ops_manual': 1}
                };
            },
            onRefresh() {
                this.fetchData(this.$route.params.id).then(res => this.$success("刷新成功"));
            },
            onSiteManage() {
                this.$router.push({name: "SiteManage", params: {id: this.site.id}})
            },
            onPreviewNginxConf() {
                console.log("onPreviewNginxConf");
                api.siteService.previewEditNginxConf(this.site.id).then(res => {
                    this.nginxConf = res.data.detail;
                    this.nginxConfDialogVisible = true;
                }).catch(err => this.$fail(err.message));
            },
            onRevertOnline() {
                console.log("onRevertOnline")
                api.siteService.revertSiteEdit(this.site.id).then(res => {
                    this.site = res.data.detail;
                    this.$success("还原成功");
                }).catch(err => this.$fail(err.message));
                ;
            },
            onDeploy() {
                console.log("onDeploy")
                api.siteService.deploySiteEdit(this.site.id).then(res => {
                    this.site = res.data.detail;
                    this.$success("发布成功");
                }).catch(err => this.$fail(err.message));
            },
            onSave() {
                console.log("onSave")
                api.siteService.updateSiteEdit(this.site).then(res => {
                    this.site = res.data.detail;
                    this.$success("保存成功");
                }).catch(err => this.$fail(err.message));
            },
            onReset() {
                console.log("onReset");
                api.siteService.getSiteEdit(this.site.id).then(res => {
                    this.site = res.data.detail;
                }).catch(err => this.$fail(err.message));
            },
            onAddSiteServer() {
                console.log("onAddSiteServer");
                this.initSiteServerEdit();
                this.siteServerDialogVisible = true;
                this.isAddServer = true;
            },
            onEditSiteServer(siteServer) {
                console.log("onEditSiteServer")
                this.siteServerEdit = siteServer;
                this.siteServerDialogVisible = true;
                this.isAddServer = false;
            },
            onServerSubmit(formName) {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        let siteServer = this.siteServerEdit;
                        let siteServers = this.site.appSiteServers.map(x => x.$update ? siteServer : x);
                        siteServer.$update = undefined;
                        if (this.isAddServer) {
                            siteServers.push(siteServer);
                        }
                        api.siteService.updateSiteEditServers(this.site.id, siteServers).then(res => {
                            this.site = res.data.detail;
                            this.siteServerDialogVisible = false;
                            this.$success("操作成功");
                        }).catch(err => this.$fail(err.message));
                    }
                });
            },
            onBatchDeleteSiteServers() {
                console.log("onBatchDeleteSiteServers")
                if (this.selectedSiteServers.length === 0) {
                    this.$message("请勾选后操作");
                    return;
                }
                this.$confirm('确认删除？').then(() => {
                    let siteServers = this.site.appSiteServers.filter(x => {
                        return !this.selectedSiteServers.includes(x);
                    });
                    api.siteService.updateSiteEditServers(this.site.id, siteServers).then(res => {
                        this.site = res.data.detail;
                        this.siteServerDialogVisible = false;
                        this.$success("删除成功");
                    }).catch(err => this.$fail(err.message));
                });
            },
            onBatchOperate(ops, command) {
                console.log(ops);
                console.log(command);
                if (this.selectedSiteServers.length === 0) {
                    this.$message("请勾选后操作");
                    return;
                }
                this.selectedSiteServers.forEach(x => {
                    x.statusDetail[ops] = command;
                });
                api.siteService.updateSiteEditServers(this.site.id, this.site.appSiteServers).then(res => {
                    this.site = res.data.detail;
                    this.$success("操作成功");
                }).catch(err => {
                    this.fetchData(this.site.id);
                    this.$fail(err.message);
                });
            },
            onDeleteSiteServer(server) {
                console.log("onDeleteSiteServer");
                this.$confirm('确认删除？').then(() => {
                    let siteServers = this.site.appSiteServers.filter(x => server !== x);
                    api.siteService.updateSiteEditServers(this.site.id, siteServers).then(res => {
                        this.site = res.data.detail;
                        this.$success("操作成功");
                    }).catch(err => this.$fail(err.message));
                });
            },
            onSelectionChange(selected) {
                console.log(selected);
                this.selectedSiteServers = selected;
            },
            fetchData(id) {
                return api.siteService.getSiteEdit(id).then(res => {
                    this.site = res.data.detail;
                }).catch(err => this.$fail(err.message));
            }
        },
        created() {
            this.fetchData(this.$route.params.id);
            this.initSiteServerEdit();
        }
    }

</script>

<style>


</style>

