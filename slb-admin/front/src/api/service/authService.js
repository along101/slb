import restApi from '../restApi'

export default {

    doAuth(token){
        let url = '/api/auth?code=' + token;
        return restApi.doGetRequest(url);
    }

}
