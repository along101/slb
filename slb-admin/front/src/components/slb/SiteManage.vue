<template>
    <div>
        <el-row>
            <el-col :span="12">
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                    <el-breadcrumb-item :to="{ name: 'SiteList' }">站点</el-breadcrumb-item>
                    <el-breadcrumb-item>站点管理</el-breadcrumb-item>
                    <el-breadcrumb-item>{{ $route.params.id }}</el-breadcrumb-item>
                </el-breadcrumb>
            </el-col>
            <el-col :span="12">
                <div style="float: right">
                    <el-button type="primary" size="small" @click="onSiteRefresh()">刷新</el-button>
                    <el-button type="primary" size="small" @click="onSiteEdit()">修改</el-button>
                    <el-button v-if="this.site.status == '1'" size="small" type="danger" @click="onUnDeploySite">下线
                    </el-button>
                    <el-button v-else size="small" type="danger" @click="onDeploySite">上线</el-button>
                    <el-button type="primary" size="small" @click="onGetNginxConf()">nginx配置</el-button>
                </div>
            </el-col>
        </el-row>
        <el-form :model="site" label-position="left" label-width="150px" inline class="form-read">
            <el-form-item label="站点ID">
                <span>{{ site.id }} </span>
            </el-form-item>
            <el-form-item label="版本号">
                <span>{{site.onlineVersion}}</span>
            </el-form-item>
            <el-form-item label="站点名称">
                <span>{{ site.name }}</span>
            </el-form-item>
            <el-form-item label="AppId">
                <span>{{ site.appId }}</span>
            </el-form-item>
            <el-form-item label="slb集群ID">
                <span>{{ site.slbId }}</span>
            </el-form-item>
            <el-form-item label="状态">
                <span>{{ site.status == 1 ? "up" : "down" }}</span>
            </el-form-item>
            <el-form-item label="域名">
                <span>{{ site.domain }}</span>
            </el-form-item>
            <el-form-item label="端口">
                <span>{{ site.port }}</span>
            </el-form-item>
            <el-form-item label="访问路径">
                <span>{{ site.path }}</span>
            </el-form-item>
            <!--<el-form-item label="负载均衡策略">-->
            <!--<span>{{ site.loadbalance }}</span>-->
            <!--</el-form-item>-->
            <el-form-item label="健康检查Uri">
                <span>{{site.healthUri}}</span>
            </el-form-item>
            <el-form-item label="说明">
                <span>{{site.note}}</span>
            </el-form-item>
        </el-form>

        <el-row>
            <el-col :span="12">
                <div style="padding-top: 15px;font-weight: bold">站点服务器列表</div>
            </el-col>
            <el-col :span="12">
                <div style="float: right;margin: 5px">
                    <el-button type="danger" size="small" @click="onBatchDelServers()">删除</el-button>
                    <el-dropdown @command="onBatchOpsPull" trigger="click">
                        <el-button type="primary" size="small">
                            发布状态<i class="el-icon-caret-bottom el-icon--right"></i>
                        </el-button>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="1">拉入</el-dropdown-item>
                            <el-dropdown-item command="0">拉出</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                    <el-dropdown @command="onBatchOpsHealCheck" trigger="click">
                        <el-button type="primary" size="small">
                            健康检查状态<i class="el-icon-caret-bottom el-icon--right"></i>
                        </el-button>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="1">拉入</el-dropdown-item>
                            <el-dropdown-item command="0">拉出</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                    <el-dropdown @command="onBatchOpsManual" trigger="click">
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
        <el-table ref="multipleTable" :data="site.appSiteServers" @selection-change="handleSelectionChange"
                  style="width: 100%" empty-text="暂无数据" border fit>
            <el-table-column type="selection" width="55" align="center"></el-table-column>
            <el-table-column type="expand" width="55">
                <template scope="props">
                    <el-form label-position="left" inline class="table-expand">
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
            <el-table-column label="ID" prop="id" align="center" sortable></el-table-column>
            <el-table-column label="名称" prop="name" align="center"></el-table-column>
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
                    <v-ops opsType="ops_pull" :row="props.row" :onClick="onOperateServer"></v-ops>
                </template>
            </el-table-column>
            <el-table-column label="健康检查状态" prop="statusDetail.ops_health_check" align="center">
                <template scope="props">
                    <v-ops opsType="ops_health_check" :row="props.row" :onClick="onOperateServer"></v-ops>
                </template>
            </el-table-column>
            <el-table-column label="手工拉入状态" prop="statusDetail.ops_manual" align="center">
                <template scope="props">
                    <v-ops opsType="ops_manual" :row="props.row" :onClick="onOperateServer"></v-ops>
                </template>
            </el-table-column>
        </el-table>
        <br>
        <el-row :gutter="20">
            <el-col :span="12">
                <el-row>
                    <el-col :span="12">
                        <div style="padding-top: 15px;font-weight: bold">nginx配置更新状态</div>
                    </el-col>
                    <el-col :span="12">
                        <div style="float: right;margin: 5px">
                            <el-button type="primary" size="small" @click="onAgentStatusRefresh()">刷新</el-button>
                        </div>
                    </el-col>
                </el-row>
                <el-table :data="agentSiteStatus" style="width: 100%" empty-text="暂无数据" border fit>
                    <el-table-column label="更新时间" prop="updateTime" align="center" width="150"
                                     sortable></el-table-column>
                    <el-table-column label="nginx ip" prop="agentIp" align="center"></el-table-column>
                    <el-table-column label="版本号" prop="agentAppSiteVersion" align="center"></el-table-column>
                    <el-table-column label="执行任务ID" prop="currentTaskId" align="center"></el-table-column>
                </el-table>
            </el-col>
            <el-col :span="12">
                <el-row>
                    <el-col :span="12">
                        <div style="padding-top: 15px;font-weight: bold">nginx配置更新日志</div>
                    </el-col>
                    <el-col :span="12">
                        <div style="float: right;margin: 5px">
                            <el-button type="primary" size="small" @click="onAgentTaskResultsRefresh()">刷新</el-button>
                        </div>
                    </el-col>
                </el-row>
                <el-table :data="agentTaskResults.content" style="width: 100%" empty-text="暂无数据" border fit>
                    <el-table-column label="更新时间" prop="updateTime" align="center" sortable
                                     width="150"></el-table-column>
                    <el-table-column label="nginx ip" prop="agentIp" align="center"></el-table-column>
                    <el-table-column label="版本号" prop="appSiteVersion" align="center"></el-table-column>
                    <el-table-column label="执行任务ID" prop="taskId" align="center"></el-table-column>
                    <el-table-column label="状态" prop="status" align="center"
                                     :formatter="formatTaskResultStatus"></el-table-column>
                    <el-table-column label="执行结果" prop="msg" align="center"></el-table-column>
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
            </el-col>
        </el-row>

        <el-dialog title="nginx配置" :visible.sync="dialogVisible">
            <pre>{{nginxConf}}</pre>
        </el-dialog>
    </div>
</template>
<script>
    import {mapState, mapGetters, mapActions, mapMutations} from 'vuex';

    export default {
        components: {
            vOps: {
                props: ["opsType", "row", "onClick"],
                template: `<div>
                                <div v-if="row.statusDetail[opsType] == 1">
                                    <el-tag color="#13CE66">up</el-tag>
                                    <el-button size="small" @click="onClick(opsType,row)">拉出</el-button>
                                </div>
                                <div v-else>
                                    <el-tag color="#FF4949">down</el-tag>
                                    <el-button size="small" @click="onClick(opsType,row)">拉入</el-button>
                                </div>
                           </div>`
            }
        },
        data() {
            return {
                taskResultStatus: {
                    "0": "无效",
                    "1": "成功",
                    "2": "执行中",
                    "3": "失败",
                    "4": "超时"
                },
                multipleSelection: [],
                dialogVisible: false,
                nginxConf: '',
                selectedServerId: [],
                currentPage: 1,
                pageSize: 10,
            }
        },
        computed: {
            ...mapGetters("siteManage", {
                site: 'getSite',
                agentSiteStatus: 'getAgentSiteStatus',
                agentTaskResults: 'getAgentTaskResults',
            }),
            total() {
                return this.agentTaskResults.numberOfElements;
            }
        },
        methods: {
            ...mapActions("siteManage", {
                fetchSiteById: 'fetchSiteById',
                fetchAgentSiteStatus: 'fetchAgentSiteStatus',
                fetchAgentTaskResults: 'fetchAgentTaskResults',
                unDeploySite: 'unDeploySite',
                deploySite: 'deploySite',
                operateServer: 'operateServer',
                fetchNginxConf: 'fetchNginxConf'
            }),
            ...mapMutations("siteManage", {
                init: 'init',
            }),
            handleSizeChange(data) {
                this.pageSize = data;
                console.log(this.pageSize)
                this.fetchAgentTaskResults(this.$route.params.id, this.currentPage - 1, this.pageSize);
            },
            handleCurrentChange(data) {
                this.currentPage = data;
                console.log(this.currentPage)
                this.fetchAgentTaskResults(this.$route.params.id, this.currentPage - 1, this.pageSize);
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
                console.log(this.multipleSelection)
                let serverId = [];
                if (this.multipleSelection.length > 0) {
                    this.multipleSelection.forEach(function (value, index) {
                        console.log(index + ':' + value)
                        serverId.push(value.id);
                    })
                }
                this.selectedServerId = serverId;
            },
            onSiteRefresh() {
                this.fetchSiteById(this.$route.params.id);
            },
            onUnDeploySite() {
                this.unDeploySite(this.site.id).then(function (res) {
                    this.fetchSiteById(this.site.id);
                    this.$message({
                        message: '站点下线成功',
                        type: 'success'
                    });
                }.bind(this)).catch(function (err) {
                    this.$message.error('站点下线失败');
                }.bind(this));
            },
            onDeploySite() {
                this.deploySite(this.site.id).then(function (res) {
                    this.fetchSiteById(this.site.id);
                    this.$message({
                        message: '站点上线成功',
                        type: 'success'
                    });
                }.bind(this)).catch(function (err) {
                    this.$message.error('站点上线失败');
                }.bind(this));
            },
            onSiteEdit() {
                this.$router.push({name: "SiteEdit", params: {id: this.site.id}})
            },
            onSiteOperation() {
                console.log('onSiteOperation');
            },
            onGetNginxConf() {
                console.log('onGetNginxConf');
                let self = this;
                this.dialogVisible = true;
                this.fetchNginxConf(this.site.id).then(function (res) {
                    console.log(res.data.detail)
                    self.nginxConf = res.data.detail;
                })
            },
            onBatchDelServers() {
                console.log('onBatchDelServers');

            },
            onBatchOpsPull(ops) {
                console.log('onBatchOpsPull');
                console.log(ops);
                if (this.selectedServerId.length > 0) {
                    let action = ops == 1 ? 'pullIn' : 'pullOut';
                    let data = {
                        action: action,
                        siteId: this.site.id,
                        serverId: this.selectedServerId
                    }
                    this.operateServer(data).then(function (res) {
                        this.fetchSiteById(this.site.id);
                        this.$message({
                            message: '站点服务器操作成功',
                            type: 'success'
                        });
                    }.bind(this)).catch(function (err) {
                        this.$message.error('站点服务器操作失败');
                    }.bind(this));
                }
            },
            onBatchOpsHealCheck(ops) {
                console.log('onBatchOpsHealCheck');
                console.log(ops);
                if (this.selectedServerId.length > 0) {
                    let action = ops == 1 ? 'healthCheckUp' : 'healthCheckDown';
                    let data = {
                        action: action,
                        siteId: this.site.id,
                        serverId: this.selectedServerId
                    }
                    this.operateServer(data).then(function (res) {
                        this.fetchSiteById(this.site.id);
                        this.$message({
                            message: '站点服务器操作成功',
                            type: 'success'
                        });
                    }.bind(this)).catch(function (err) {
                        this.$message.error('站点服务器操作失败');
                    }.bind(this));
                }
            },
            onBatchOpsManual(ops) {
                console.log('onBatchOpsManual');
                console.log(ops);
                if (this.selectedServerId.length > 0) {
                    let action = ops == 1 ? 'manualUp' : 'manualDown';
                    let data = {
                        action: action,
                        siteId: this.site.id,
                        serverId: this.selectedServerId
                    }
                    this.operateServer(data).then(function (res) {
                        this.fetchSiteById(this.site.id);
                        this.$message({
                            message: '站点服务器操作成功',
                            type: 'success'
                        });
                    }.bind(this)).catch(function (err) {
                        this.$message.error('站点服务器操作失败');
                    }.bind(this));
                }
            },
            onOperateServer(type, siteServer) {
                console.log('onOperateServer');
                console.log(type);
                console.log(siteServer);
                let action = '';
                let serverId = [];
                serverId.push(siteServer.id);
                if (type == 'ops_pull') {
                    action = siteServer.statusDetail.ops_pull == 1 ? 'pullOut' : 'pullIn';
                } else if (type == 'ops_health_check') {
                    action = siteServer.statusDetail.ops_health_check == 1 ? 'healthCheckDown' : 'healthCheckUp';
                } else if (type == 'ops_manual') {
                    action = siteServer.statusDetail.ops_manual == 1 ? 'manualDown' : 'manualUp';
                }
                let data = {
                    action: action,
                    siteId: this.site.id,
                    serverId: serverId
                }
                this.operateServer(data).then(function (res) {
                    this.fetchSiteById(this.site.id);
                    this.$message({
                        message: '站点服务器操作成功',
                        type: 'success'
                    });
                }.bind(this)).catch(function (err) {
                    this.$message.error('站点服务器操作失败');
                }.bind(this));
            },
            onAgentStatusRefresh() {
                console.log('onAgentStatusRefresh');
                this.fetchAgentSiteStatus(this.$route.params.id);
            },
            onAgentTaskResultsRefresh() {
                console.log('onAgentTaskResultsRefresh');
                this.fetchAgentTaskResults(this.$route.params.id, this.currentPage - 1, this.pageSize);
            },
            formatTaskResultStatus(row, column, cellValue) {
                return this.taskResultStatus[row.status + ""];
            }
        },
        created() {
            this.init();
            this.fetchSiteById(this.$route.params.id);
            this.fetchAgentSiteStatus(this.$route.params.id);
            this.fetchAgentTaskResults(this.$route.params.id, this.currentPage - 1, this.pageSize);
        }
    }

</script>

<style>
    .close-button {
        float: right;
    }
</style>

