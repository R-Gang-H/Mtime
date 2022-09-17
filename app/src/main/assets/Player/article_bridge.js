(function (undefined) {

    window.onerror = function (e, url) {
        alert(e + ' ;at file:' + url);
    };

    var appMethodApi, isIOS, iosBizCallbackMap = {};

    if (window.webkit && window.webkit.messageHandlers &&
        window.webkit.messageHandlers.nativeEventHandler &&
        window.webkit.messageHandlers.nativeEventHandler.postMessage) {
        isIOS = true;
        console.log(window.webkit.messageHandlers.nativeEventHandler.postMessage);
        appMethodApi = window.webkit.messageHandlers.nativeEventHandler.postMessage;
    } else if (window.mtime && typeof window.mtime === 'object') {
        console.log(window.mtime);
        appMethodApi = window.mtime;
    } else {
        //不在app webview中，不向全局注册appBridge和jsBridge
        //return;
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
                appMethodApi[_apiName](JSON.stringify(wrapData));
            }
        } catch (e) {

        }
    };

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

    window.registJSBridge = function (jsFunc) {
        var jsFnName = jsFunc.name;
        if (!jsFnName) {
            throw new Error('jsBridge need a named function');
        }
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