import axios from 'axios'

export default {

    doGetRequest(url){
        console.log("send GET request to : " + url);
        return axios.get(url);
    },
    doDeleteRequest(url){
        console.log("send DELETE request to : " + url);
        return axios.delete(url)
    },
    doPutRequest(url, data){
        console.log("send PUT request to : " + url);
        if (typeof(data) == "object") {
            data = JSON.stringify(data);
        }
        return axios.put(url, data)
    },
    doPostRequest(url, data){
        console.log("send POST request to : " + url);
        if (typeof(data) == "object") {
            data = JSON.stringify(data);
        }
        return axios.post(url, data)
    }
}
