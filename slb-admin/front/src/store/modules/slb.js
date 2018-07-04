/**
 * Created by lizhiming on 2017/8/22.
 */
import * as types from '../mutation-types'
import {api} from '../../api'


/**
 *
 * Created by huangyinhuang on 2017/8/4.
 *
 * 存储SLB所有元数据，用于UI对这些数据信息的增删改查等编辑操作
 *
 * slbs: 存储所有SLB列表信息
 * sites: 存储所有站点列表信息
 * slb_servers: 存储指定SLB服务器列表信息
 * site_server: 存储指定站点服务器列表信息
 *
 */

// initial state
const state = {
    slbs: [],
    slbStatus: [],
    slbServers: [],
    sites: [],
    siteServers: [],
    totalSites: 0,
    totalSlbs: 0
};

// getters
const getters = {
    getSlbs: state => state.slbs,
    getSlbStatus: state => state.slbStatus,
    getSites: state => state.sites,
    getSiteServers: state => state.siteServers,
    getSlbServers: state => state.slbServers,
    getTotalSlbs: state => state.totalSlbs,
    getTotalSites: state => state.totalSites
};

// actions
const actions = {

    /**
     * 发送请求到后端服务，获取SLB列表
     * @param commit    store state更新提交者
     */
    fetchSlbs({commit}){
        api.slbService.getSLBs().then(function (res) {
            console.log(res.data.detail)
            commit(types.UPDATE_SLB_CLUSTER, res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    querySlbs({commit}, data){
        api.slbService.getSLBs().then(function (res) {
            console.log(res.data.detail)
            commit(types.UPDATE_SLB_CLUSTER, res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    querySlbServers({commit}, data){
        api.slbService.getSLBs().then(function (res) {
            console.log(res.data.detail)
            commit(types.UPDATE_SLB_CLUSTER, res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    querySites({commit}, data){
        api.slbService.getSLBs().then(function (res) {
            console.log(res.data.detail)
            commit(types.UPDATE_SLB_CLUSTER, res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取站点列表
     * @param commit    store state更新提交者
     */
    fetchSites({commit}, data){
        api.slbService.getSites(data).then(function (res) {
            console.log(res)
            commit(types.UPDATE_SITE_LIST, res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取指定SLB的服务器列表
     * @param commit    store state更新提交者
     * @param slbId     指定的SLB ID
     */
    fetchSlbStatus ({commit}, data){
        api.slbService.getSlbStatus(data).then(function (res) {
            console.log(res)
            commit(types.UPDATE_SLB_STATUS, res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },
    /**
     * 发送请求到后端服务，获取指定站点的服务器列表
     * @param commit    store state更新提交者
     * @param siteId     指定的站点ID
     */
    fetchSiteServers ({commit}, siteId){
        api.slbService.getSiteServers({siteId: siteId}).then(function (res) {
            commit(types.UPDATE_SITE_SERVER_LIST, res.data.details);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，创建新的SLB
     * @param dispatch  store action分发者
     * @param data      附带SLB信息的数据对象，data格式为 {slb: ...}
     */
    createSlb({dispatch}, data){
        api.slbService.createSlb(data).then(function (res) {
            dispatch("fetchSlbs");
            data.resolve(res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },
    /**
     * 发送请求到后端服务，更新指定SLB信息
     * @param dispatch  store action分发者
     * @param data      附带SLB信息的数据对象，data格式为 {slb: ...}, 其中slb需要指定更新的slb Id
     */
    updateSlb({dispatch}, data){
        api.slbService.updateSlb(data).then(function (res) {
            dispatch("fetchSlbStatus", data.slb.id);
            dispatch("fetchSlbs");
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },
    /**
     * 发送请求到后端服务，删除指定SLB对象
     * @param dispatch  store action分发者
     * @param data      附带SLB信息的数据对象，data格式为 {slbId: x}, 其中x为指定删除的slb Id
     */
    deleteSlb({dispatch}, data){
        api.slbService.deleteSlb(data).then(function (res) {
            dispatch("fetchSlbs");
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    createSlbServer({dispatch}, data){
        api.slbService.createSlbServer(data).then(function (res) {
            dispatch("fetchSlbStatus", data.slbId);
            dispatch("fetchSlbs");
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    deleteSlbServer({dispatch}, data){
        api.slbService.deleteSlbServer(data).then(function (res) {
            dispatch("fetchSlbStatus", data.slbId);
            dispatch("fetchSlbs");
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，创建新的站点
     * @param dispatch  store action分发者
     * @param data      附带站点信息的数据对象，data格式为 {site: ...}
     */
    createSite({dispatch}, data){
        api.slbService.createSite(data).then(function (res) {
            dispatch("fetchSites");
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },
    /**
     * 发送请求到后端服务，更新指定站点信息
     * @param dispatch  store action分发者
     * @param data      附带站点信息的数据对象，data格式为 {site: ...}，其中site需指定要更新的site id
     */
    updateSite({dispatch}, data){
        api.slbService.updateSite(data).then(function (res) {
            dispatch("fetchSites");
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },
    /**
     * 发送请求到后端服务，删除指定站点
     * @param dispatch  store action分发者
     * @param data      附带站点信息的数据对象，data格式为 {siteId: x}，其中x为指定要删除的site id
     */
    deleteSite({dispatch}, data){
        api.slbService.deleteSite(data).then(function (res) {
            dispatch("fetchSites");
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，创建新的站点服务器
     * @param dispatch  store action分发者
     * @param data      附带服务器信息的数据对象，data格式为 {siteId：x, server：...}，其中siteId为指定站点，server为服务器信息
     */
    createSiteServer({dispatch}, data){
        api.slbService.createSiteServer(data).then(function (res) {
            dispatch("fetchSiteServers", data.siteId);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },
    /**
     * 发送请求到后端服务，更新指定站点服务器
     * @param dispatch  store action分发者
     * @param data      附带服务器信息的数据对象，data格式为 {siteId：x, server：...}，其中siteId为指定站点，server为服务器信息
     */
    updateSiteServer({dispatch}, data){
        api.slbService.updateSiteServer(data).then(function (res) {
            dispatch("fetchSiteServers", data.siteId);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },
    /**
     * 发送请求到后端服务，删除指定站点服务器
     * @param dispatch  store action分发者
     * @param data      附带服务器信息的数据对象，data格式为 {siteId：x, appSiteServerId：y}，其中siteId为指定站点ID，serverId为要删除的服务器ID
     */
    deleteSiteServer({dispatch}, data){
        api.slbService.deleteSiteServer(data).then(function (res) {
            dispatch("fetchSiteServers", data.siteId);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    }

};

// mutations
const mutations = {
    [types.UPDATE_SLB_CLUSTER] (state, data) {
        state.slbs = data;
        state.totalSlbs = data.length;
    },
    [types.UPDATE_SLB_STATUS] (state, data) {
        state.slbStatus = data;
        state.slbServers = data.slbServers;
    },
    [types.UPDATE_SITE_LIST] (state, data) {
        state.sites = data.content;
        state.totalSites = data.totalElements;
    },
    [types.UPDATE_SITE_SERVER_LIST] (state, data) {
        state.siteServers = data;
    }
};

export default {
    state,
    getters,
    actions,
    mutations
}