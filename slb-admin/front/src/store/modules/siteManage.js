import * as types from '../mutation-types'
import {api} from '../../api'


// initial state
const state = {
    site: {},
    agentSiteStatus: [],
    agentTaskResults: {},
};

// getters
const getters = {
    getSite: state => state.site,
    getAgentSiteStatus: state => state.agentSiteStatus,
    getAgentTaskResults: state => state.agentTaskResults,
};

// mutations
const mutations = {
    init(state) {
        state.site = {};
        state.agentSiteStatus = [];
        state.agentTaskResults = {};
    },
    setSite(state, site) {
        state.site = site;
    },
    setAgentSiteStatus(state, agentSiteStatus) {
        state.agentSiteStatus = agentSiteStatus;
    },
    setAgentTaskResults(state, agentTaskResults) {
        state.agentTaskResults = agentTaskResults;
    }
};

// actions
const actions = {
    fetchSiteById({commit}, siteId) {
        return api.siteService.getSiteById(siteId).then(function (res) {
            commit('setSite', res.data.detail);
        }.bind(this));
    },
    fetchAgentSiteStatus({commit}, siteId) {
        api.siteService.getAgentSiteStatus(siteId).then(function (res) {
            commit('setAgentSiteStatus', res.data.detail);
        }.bind(this));
    },
    fetchAgentTaskResults({commit}, siteId, page, size) {
        api.siteService.getAgentTaskResults(siteId, page, size).then(function (res) {
            commit('setAgentTaskResults', res.data.detail);
        }.bind(this)).catch(function (err) {
            console.log(err);
        }.bind(this));
    },
    unDeploySite({dispatch}, siteId){
        return api.siteService.doOperationUnDeploy(siteId);
    },
    deploySite({dispatch}, siteId){
        return api.siteService.doOperationDeploy(siteId);
    },
    operateServer({dispatch}, data) {
        return api.siteService.doOperateServer(data);
    },
    fetchNginxConf({dispatch}, siteId) {
        return api.siteService.getNginxConf(siteId);
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}