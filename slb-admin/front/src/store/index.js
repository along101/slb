/**
 * Created by lizhiming on 2017/8/17.
 */
import Vue from 'vue'
import Vuex from 'vuex'
import slb from './modules/slb'
import siteList from './modules/siteList'
import siteManage from './modules/siteManage'

Vue.use(Vuex);

const debug = process.env.NODE_ENV !== 'production';

export default new Vuex.Store({
    modules: {
        slb,
        siteList,
        siteManage,
    },
    strict: debug
})

