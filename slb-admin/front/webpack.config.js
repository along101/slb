const path = require('path');
const resolve = require('path').resolve;
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const url = require('url');
const publicPath = '';
const request = require("request");

module.exports = (options = {}) => ({
    entry: {
        vendor: './src/vendor',
        index: './src/main.js'
    },
    output: {
        path: resolve(__dirname, '../src/main/resources/static'),
        filename: options.dev ? '[name].js' : '[name].js?[chunkhash]',
        chunkFilename: '[id].js?[chunkhash]',
        publicPath: options.dev ? '/assets/' : publicPath
    },
    module: {
        loaders: [
            {
                test: /\.css$/,
                loader: 'style-loader!css-loader'
            }
        ],
        rules: [
            {
                test: /\.vue$/,
                use: ['vue-loader']
            },
            {
                test: /\.js$/,
                use: ['babel-loader'],
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader', 'postcss-loader']
            },
            {
                test: /\.(png|jpg|jpeg|gif|eot|ttf|woff|woff2|svg|svgz)(\?.+)?$/,
                use: [{
                    loader: 'url-loader',
                    options: {
                        limit: 10000
                    }
                }]
            }
        ]
    },
    plugins: [
        new webpack.optimize.CommonsChunkPlugin({
            names: ['vendor', 'manifest']
        }),
        new HtmlWebpackPlugin({
            title: 'My App',
            template: 'src/index.html'
        })
    ],
    resolve: {
        alias: {
            '~': resolve(__dirname, 'src'),
            'vue': 'vue/dist/vue.js',
        }
    },
    devServer: {
        port: 8012,
        contentBase: [path.join(__dirname, "public"), path.join(__dirname, "/assets")],
        proxy: {
            '/web/': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            }
        },
        historyApiFallback: {
            index: url.parse(options.dev ? '/assets/' : publicPath).pathname
        },
        setup(app){
            app.get('/some/path', function (req, res) {
                res.json({custom: 'response'});
            });

            // provide the JWT token
            app.get('/api/auth', function (req, res) {

                if ((req.query == null) || (req.query.code == null)) {
                    // exit with 400 bad request error
                    console.log("The user inputs an empty code for query.");
                    res.status(400).end();
                } else {

                    let authCode = req.query.code;

                    var options = {
                        method: 'POST',
                        url: 'http://sso.along101corp.com/cas/oidc/accessToken',
                        headers: {
                            authorization: 'Basic c2xiOnNsYkBwcGRhaS5jb20=',
                            'content-type': 'application/x-www-form-urlencoded',
                            'cache-control': 'no-cache'
                        },
                        form: {
                            code: authCode,
                            redirect_uri: 'http://slb.along101corp.com/',
                            grant_type: 'authorization_code'
                        }
                    };

                    request.post(options, function (e, r, response) {
                        console.log(response);

                        let accessToken = null;
                        try {
                            let tokenInfo = JSON.parse(response);
                            accessToken = tokenInfo.access_token;
                        }
                        catch (e) {
                            console.log(e);
                            console.log("failed to parse the access token info from response.");
                        }

                        if (accessToken == null) {
                            // exit with 401 Unauthorized
                            console.log("no access token is returned from sso, please ensure user has input correct auth code.");
                            res.status(401).end();
                        } else {

                            let userProfileUrl = "http://sso.along101corp.com/cas/oidc/profile?access_token=" + accessToken;
                            request.get(userProfileUrl, function (e, r, response) {
                                let jwtToken = null;
                                try {
                                    let jsonResponse = JSON.parse(response);
                                    console.log(jsonResponse);

                                    let jwt = require('jsonwebtoken');
                                    jwtToken = jwt.sign(jsonResponse, 'slb-secret', {
                                        algorithm: 'HS256',
                                        expiresIn: "1d",
                                        issuer: "slb-front"
                                    });
                                } catch (e) {
                                    console.log(e);
                                }

                                // send the jwt token back
                                if (jwtToken) {
                                    console.log("print out jwt token in the next line: ");
                                    console.log(jwtToken);
                                    res.send(jwtToken);
                                } else {
                                    console.log("failed to generate jwt token with user inputs..");
                                    // exit with 400 bad request error
                                    res.status(400).end();
                                }
                            });

                        }

                    });

                }
            });

        }
    },
    devtool: options.dev ? '#eval-source-map' : '#source-map'
})
