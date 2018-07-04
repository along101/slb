import Vue from 'vue';

import router from './router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-default/index.css';
import commonPlugin from '~/common/commonPlugin';
// import './assets/css/theme-green/index.css';
// import './assets/css/theme-green/color-green.css';
import './assets/css/color-dark.css';
import './assets/css/main.css';
import store from './store'
import axios from 'axios'

Vue.prototype.$http = axios;

Vue.use(ElementUI);
Vue.use(commonPlugin);
new Vue({
    store,
    router,
}).$mount('#app');

