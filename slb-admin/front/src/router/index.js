/**
 * Created by lizhiming on 2017/8/17.
 */
import Vue from 'vue';
import Router from 'vue-router';
import Login from '../components/Login.vue'
import Home from '../components/layout/Home.vue'
import SlbList from '../components/slb/SlbList.vue'
import SiteList from '../components/slb/SiteList.vue'
import SiteManage from '../components/slb/SiteManage.vue'
import SiteEdit from '../components/slb/SiteEdit.vue'
import Dashboard from '../components/Dashboard.vue'
import Introduction from '../components/Introduction.vue'
import Slb from '../components/slb/Slb.vue'

Vue.use(Router);

export default new Router({
    mode: "hash",
    routes: [
        {
            path: '/login',
            name: 'Login',
            component: Login,
        },
        {
            path: '/home',
            component: Home,
            children: [
                {
                    path: '',
                    name: 'Introduction',
                    component: Introduction
                },
                {
                    path: '/slbs',
                    name: 'SlbList',
                    component: SlbList
                },
                {
                    path: '/slb',
                    name: 'Slb',
                    component: Slb
                },
                {
                    path: '/slb/sites',
                    name: 'SiteList',
                    component: SiteList
                },
                {
                    path: '/slb/sites/manage/:id',
                    name: 'SiteManage',
                    component: SiteManage
                },
                {
                    path: '/slb/sites/edit/:id',
                    name: 'SiteEdit',
                    component: SiteEdit
                },
                {
                    path: '/dashboard',
                    name: 'Dashboard',
                    component: Dashboard

                }
            ]
        },
        {
            path: '/',
            redirect: '/login'
        }
    ],
    linkActiveClass: 'active'
})
