import restApi from '../restApi'
import {handResponse} from "~/common/common";

export default {
    getSiteById(siteId) {
        let url = '/web/sites/' + siteId;
        return restApi.doGetRequest(url);
    },
    getSites(request = {}) {
        let url = '/web/sites?page=' + (request.page - 1) + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    getSiteServers(request = {}) {
        let url = '/web/sites/' + request.siteId + '/servers';
        return restApi.doGetRequest(url);
    },

    searchSites(request = {}) {
        let url = '/web/sites/site?page=' + (request.page - 1) + '&size=' + request.size;

        //判断查询参数是否为空

        if (request.queryForm.slbId != '') {
            url = url + '&slbId=' + request.queryForm.slbId;
        }

        if (request.queryForm.appId != '') {
            url = url + '&appId=' + request.queryForm.appId;
        }

        if (request.queryForm.name != '') {
            url = url + '&name=' + request.queryForm.name;
        }

        if (request.queryForm.domain != '') {
            url = url + '&domain=' + request.queryForm.domain;
        }

        console.log("api searchSites url: " + url);
        return restApi.doGetRequest(url);
    },

    getSiteArchiveStatus(request = {}) {
        let url = '/web/archive/appSite/' + request.siteId;
        return restApi.doGetRequest(url);
    },
    getNginxAgentStatus(request = {}) {
        let url = '/web/agent/appSite/' + request.siteId;
        return restApi.doGetRequest(url);
    },
    getSiteReleaseHistory(request = {}) {
        let url = '/web/tasks?siteId=' + request.siteId;
        return restApi.doGetRequest(url);
    },

    createSite(request = {}) {
        let url = "/web/sites";
        return restApi.doPostRequest(url, request.siteList);
    },
    updateSite(request = {}) {
        let url = "/web/sites";
        return restApi.doPutRequest(url, request.siteList);
    },
    deleteSite(request = {}) {
        let url = "/web/sites/" + request.siteId;
        return restApi.doDeleteRequest(url);
    },

    createSiteServer(request = {}) {
        let url = "/web/sites/" + request.siteId + "/servers";
        return restApi.doPostRequest(url, request.server);
    },
    updateSiteServer(request = {}) {
        let url = "/web/sites/" + request.siteId + "/servers";
        return restApi.doPutRequest(url, request.server);
    },
    deleteSiteServer(request = {}) {
        let url = "/web/sites/" + request.siteId + "/servers/" + request.serverId;
        return restApi.doDeleteRequest(url);
    },
    getAgentSiteStatus(siteId) {
        let url = "/web/agent/appSite/" + siteId;
        return restApi.doGetRequest(url);
    },
    getAgentTaskResults(siteId, page = 0, size = 10) {
        let url = "/web/agent/taskResult/appSite/" + siteId + "?page=" + page + "&size=" + size;
        return restApi.doGetRequest(url);
    },
    doOperationDeploy(siteId) {
        let url = "/web/operation/deploy/" + siteId;
        return restApi.doPutRequest(url, null);
    },
    doOperationUnDeploy(siteId) {
        let url = "/web/operation/undeploy/" + siteId;
        return restApi.doPutRequest(url, null);
    },
    doOperateServer(request = {}) {
        let url = "/web/operation/" + request.action + "/" + request.siteId;
        return restApi.doPutRequest(url, request.serverId);
    },
    getNginxConf(siteId) {
        let url = '/web/nginx/' + siteId;
        return restApi.doGetRequest(url);
    },
    getSiteEdit(siteId) {
        let url = '/web/siteedit/' + siteId;
        return restApi.doGetRequest(url).then(handResponse);
    },
    updateSiteEdit(site) {
        let url = '/web/siteedit';
        return restApi.doPutRequest(url, site).then(handResponse);
    },
    revertSiteEdit(siteId) {
        let url = '/web/siteedit/' + siteId + '/revert';
        return restApi.doPostRequest(url, {}).then(handResponse);
    },
    deploySiteEdit(siteId) {
        let url = '/web/siteedit/' + siteId + '/deploy';
        return restApi.doPostRequest(url, {}).then(handResponse);
    },
    updateSiteEditServers(siteId, appSiteServers) {
        let url = '/web/siteedit/' + siteId + '/servers';
        return restApi.doPutRequest(url, appSiteServers).then(handResponse);
    },
    previewEditNginxConf(siteId) {
        let url = '/web/siteedit/' + siteId + '/preview';
        return restApi.doGetRequest(url).then(handResponse);
    },
}
