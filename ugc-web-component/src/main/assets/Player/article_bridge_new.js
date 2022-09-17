(function (undefined) {
    window.onerror = function (e, url) {
        alert(e + ' ;at file:' + url);
    };
    window.supportShake = function () {
    }
    var appMethodApi, isIOS, iosBizCallbackMap = {};
    var isWebview;
    if (window.webkit && window.webkit.messageHandlers &&
        window.webkit.messageHandlers.nativeEventHandler &&
        window.webkit.messageHandlers.nativeEventHandler.postMessage) {
        isIOS = true;
        // console.log(window.webkit.messageHandlers.nativeEventHandler.postMessage);
        appMethodApi = window.webkit.messageHandlers.nativeEventHandler.postMessage;
    } else if (window.mtime && typeof window.mtime === 'object') {
        // console.log(window.mtime);
        appMethodApi = window.mtime;
    } else {
        //不在app webview中，不向全局注册appBridge和jsBridge
        //return;
        isWebview = false;
    }
    window.jsBridge = {};
    //js调用app的方法 需要使用appBridge()

    //使用方法 appBridge(
    //   {
    //     callback:app回调js的方法名
    //     apiName:app的方法名
    //     data:{} 传给app的参数
    //   }
    //)
    window.appBridge = function (param) {
        var apiMethod, wrapData,
            _apiName, _callback,
            token, result,
            key, value;
        if (!param.apiName) {
            throw new Error('appBridge need an api name');
        }
        _apiName = param.apiName;
        delete param.apiName;
        _callback = param.callback;
        if (_callback) {
            delete param.callback;
        }
        if (!isIOS) {
            apiMethod = appMethodApi[_apiName];
            if (!apiMethod) {//安卓的，方法不存在直接抛出错误
                throw new Error('api name is not exist');
            }
        } else {
            //ios的方法不存在 在jsBridge.jsCallback里抛出错误
            apiMethod = appMethodApi;
        }
        wrapData = {};
        wrapData['callBackMethod'] = _callback;
        token = String((new Date()).getTime());
        iosBizCallbackMap[token] = _callback;
        if (isIOS) {//ios 不论是否需要app回调js，这里都会提供回调用于判断方法是否存在
            wrapData['callBackMethod'] = "jsCallback";
            wrapData['methodName'] = _apiName;
            wrapData['token'] = token;
        }
        for (key in param) {
            value = param[key];
            if (value != undefined) {//undefined、null 不给app
                wrapData[key] = param[key];
            }
        }
        try {
            if (isIOS) {
                window.webkit.messageHandlers.nativeEventHandler.postMessage(JSON.stringify(wrapData));
            } else {
                var result = appMethodApi[_apiName](JSON.stringify(wrapData));
                // window.alert(result,"result")
                var res = eval("(" + result + ")");
                if (wrapData.callBackMethod && (typeof wrapData.callBackMethod) == "function") {
                    // 执行回调方法
                    // window.alert(res,"res")
                    callbackJSSDKBridge({ success: true, errMsg: '', data: res },wrapData);
                }
            }
        } catch (e) {
            window.alert("错误信息2：", e)
        }
    };
    function callbackJSSDKBridge(result,param) {
        // 防止阻塞UI线程

        setTimeout(function () {

            if (!result) {
                // 提示信息
                throw "调用JSSDK返回失败，无结果。";
            } else {
                if (!result.success) {
                    if (result.errMsg)
                        // result.errMsg不为空，提示错误信息
                        throw "result.errMsg:" + result.errMsg;
                    else
                        // 提示信息
                        throw "调用JSSDK返回失败，无错误信息。";
                } else {
                    // window.alert(result.data,"result.data")
                    if (result.data) {
                        // result.data存在
                        if (param.callBackMethod) {
                            if (typeof param.callBackMethod == "function")
                                // 使用data执行后处理
                                param.callBackMethod(result.data);

                            else
                                throw "后处理丢失！";
                        }
                    } else {
                        // result.data为空，提示信息
                        throw "调用JSSDK返回无数据。";
                    }
                    // 在全局Token变量中，销毁源操作生成的Token值相应的后处理
                    // delete window.globalJSSDKTokenMap[result.tokenKey];
                }
            }
        }, 1);
        return "success";
    }
    //注册app调用的js方法,app需要使用jsBridge[function name]()调用
    //app调用jsBridge['假设是funcA'](),如果按格式传了参数
    //funcA会收到一个对象
    //   {
    //     callback:js回调app的方法名
    //     data:{} 传给js的参数
    //   }
    //)
    if (isIOS) {
        window.jsBridge['jsCallback'] = function (data) {
            var res, result, token, bizCallbackName;
            try {
                res = JSON.parse(data);
            } catch (e) {
                throw e;
            }
            if (!res.success) {
                if (res.code == 1) {
                    //方法不存在
                    throw new Error('api name is not exist');
                }
            }
            token = res['token'];
            if (!(token in iosBizCallbackMap)) {
                throw new Error('ios-js回调方法丢失');
            }
            bizCallbackName = iosBizCallbackMap[token];

            delete res['token'];

            window.jsBridge[bizCallbackName](res);

            delete iosBizCallbackMap[token];
            res = null;
        };
    }
    window.registJSBridge = function (jsFunc, name) {
        var jsFnName = jsFunc.name;
        if (!jsFnName) {
            if (!name) {
                throw new Error('jsBridge need a named function');
            }
            jsFnName = name;
        }
        window[jsFnName] = jsFunc;
        window.jsBridge[jsFnName] = function (res) {
            var result, token, bizCallbackName;
            if (res && typeof res === 'string') {
                try {
                    res = JSON.parse(res);
                } catch (e) {
                    throw e;
                }
                if (('callBackMethod' in res) && !('callback' in res)) {
                    res.callback = res['callBackMethod'];
                    delete res['callBackMethod'];
                }
            }

            result = jsFunc(res);

            obj = null;
            jsFunc = null;

            return JSON.stringify({ data: result });
        };
    };
})(undefined);