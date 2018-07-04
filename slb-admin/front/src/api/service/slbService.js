import restApi from '../restApi'

export default {
    getSLBs (request = {}) {
        let url = '/web/slbs';
        return restApi.doGetRequest(url);
    },
    getSlbStatus (request) {
        let url = '/web/slbs/' + request;
        return restApi.doGetRequest(url);
    },
    getSites (request = {}) {
        let url = '/web/sites?page=' + (request.page -1) + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    getSiteServers (request = {}) {
        let url = '/web/sites/' + request.siteId + '/servers';
        return restApi.doGetRequest(url);
    },


    getSiteArchiveStatus(request = {}){
        let url = '/web/archive/appSite/' + request.siteId;
        return restApi.doGetRequest(url);
    },
    getNginxAgentStatus(request = {}) {
        let url = '/web/agent/appSite/' + request.siteId;
        return restApi.doGetRequest(url);
    },
    getSiteReleaseHistory (request = {}) {
        let url = '/web/tasks?siteId=' + request.siteId;
        return restApi.doGetRequest(url);
    },

    createSlb(request = {}){
        let url = "/web/slbs";
        return restApi.doPostRequest(url, request.slb);
    },
    updateSlb(request = {}){
        let url = "/web/slbs";
        return restApi.doPutRequest(url, request.slb);
    },
    deleteSlb(request = {}){
        let url = "/web/slbs/" + request.slbId;
        return restApi.doDeleteRequest(url);
    },
    createSlbServer(request = {}){
        let url = "/web/slbs/" + request.slbId + "/servers";
        return restApi.doPostRequest(url, request.slbServer);
    },
    deleteSlbServer(request = {}){
        let url = "web/slbs/" + request.slbId + "/servers/" + request.slbServerId;
        return restApi.doDeleteRequest(url);
    },
    createSite(request = {}){
        let url = "/web/sites";
        return restApi.doPostRequest(url, request.site);
    },
    updateSite(request = {}){
        let url = "/web/sites";
        return restApi.doPutRequest(url, request.site);
    },
    deleteSite(request = {}){
        let url = "/web/sites/" + request.siteId;
        return restApi.doDeleteRequest(url);
    },


    createSiteServer(request = {}){
        let url = "/web/sites/" + request.siteId + "/servers";
        return restApi.doPostRequest(url, request.server);
    },
    updateSiteServer(request = {}){
        let url = "/web/sites/" + request.siteId + "/servers";
        return restApi.doPutRequest(url, request.server);
    },
    deleteSiteServer(request = {}){
        let url = "/web/sites/" + request.siteId + "/servers/" + request.serverId;
        return restApi.doDeleteRequest(url);
    },
    doOperationPullIn(request = {}){
        let url = "/web/operation/pullIn/" + request.siteId + "?serverId=" + request.serverId;
        return restApi.doPutRequest(url, null);
    },
    doOperationPullOut(request = {}){
        let url = "/web/operation/pullOut/" + request.siteId + "?serverId=" + request.serverId;
        return restApi.doPutRequest(url, null);
    },
    doOperationRaiseByHealth(request = {}){
        let url = "/web/operation/healthCheckUp/" + request.siteId + "?serverId=" + request.serverId;
        return restApi.doPutRequest(url, null);
    },
    doOperationFallByHealth(request = {}){
        let url = "/web/operation/healthCheckDown/" + request.siteId + "?serverId=" + request.serverId;
        return restApi.doPutRequest(url, null);
    },
    doOperationManualUp(request = {}){
        let url = "/web/operation/manualUp/" + request.siteId + "?serverId=" + request.serverId;
        return restApi.doPutRequest(url, null);
    },
    doOperationManualDown(request = {}){
        let url = "/web/operation/manualDown/" + request.siteId + "?serverId=" + request.serverId;
        return restApi.doPutRequest(url, null);
    }

}
