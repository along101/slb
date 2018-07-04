import * as types from '../mutation-types'
import {api} from '../../api'


// initial state
const state = {
    test: "123",
    currentSite: {},
    sites: [],
    slbs: [],
    totalSites: 0,
};

// getters
const getters = {
    getSites: state => state.sites,
    getSlbs: state => state.slbs,
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
            commit("updateSlbList", res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    fetchSites({commit}, data) {
        return api.siteService.getSites(data).then(function (res) {
            commit("updateSiteList", res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },

    searchSites({commit}, data) {
        return api.siteService.searchSites(data).then(
            function (res) {
                commit("updateSiteList", res.data.detail);
            }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));

    },

    /**
     * 发送请求到后端服务，删除指定站点
     * @param dispatch  store action分发者
     * @param data      附带站点信息的数据对象，data格式为 {siteId: x}，其中x为指定要删除的site id
     */
    deleteSite({commit}, data){
        return api.siteService.deleteSite(data);
    },

    /**
     * 发送请求到后端服务，更新指定站点信息
     * @param dispatch  store action分发者
     * @param data      附带站点信息的数据对象，data格式为 {siteList: ...}，其中site需指定要更新的site id
     */
    updateSite({commit}, data){
        api.siteService.updateSite(data);
    },

    /**
     * 发送请求到后端服务，创建新的站点
     * @param dispatch  store action分发者
     * @param data      附带站点信息的数据对象，data格式为 {siteList: ...}
     */
    createSite({commit}, data){
        let pageSize = data.size;

        return api.siteService.createSite(data);
    },

};

// mutations
const mutations = {

    test(state, data) {
        state.test = data;
    },

    setCurrentSite(state, current) {
        state.currentSite = current;
    },

    updateSiteList(state, data) {
        state.sites = data.content;
        state.totalSites = data.totalElements;
    },

    updateSlbList(state, data){
        state.slbs = data;
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}