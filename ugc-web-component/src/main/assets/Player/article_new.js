(function () {
    //隐藏评分
    var ratingParent = document.getElementsByClassName('m_score')[0];
    if (ratingParent) {
        var ratingElements = ratingParent.childNodes,
            ratingElement = ratingElements[0];
        if (ratingElement) {
            var rating = parseInt(ratingElement.innerHTML);
            if (!rating) {
                ratingParent.style.display = 'none';
            }
        }
    }

    var i, len, network, h5Regex, pcRegex, allAppViewImgsSrc;

    //匹配H5格式的url链接
    h5Regex = {
        movieDetailRegex: /\/movie\/(\d+)\/$/i,
        actorRegex: /\/person\/(\d+)\/$/i,
        commerceDetailRegex: /\/commerce\/item\/(\d+)\//i,
        commerceListRegex: /\/commerce\/list\/(?:\?(.+)?)?$/i
    };
    //匹配PC格式的url链接
    pcRegex = {
        movieDetailRegex: /movie\.mtime\.com\/(\d+)\/?$/i,
        actorRegex: /people\.mtime\.com\/(\d+)\/?$/i,
        postRegex: /content\.mtime\.com\/post\/(\d+)\/?$/i,
        journalRegex: /content\.mtime\.com\/journal\/(\d+)\/?$/i,
        articleRegex: /content\.mtime\.com\/article\/(\d+)\/?$/i,
        reviewRegex: /content\.mtime\.com\/review\/(\d+)\/?$/i,
        communityRegex: /community\.mtime\.com\/familyDetail\/(\d+)\/?/i,
        listRegex: /list\.mtime\.com\/movieDetail\/(\d+)\/?/i,
        myRegex: /my\.mtime\.com\/(\d+)\/?/i,
        videoRegex: /video\.mtime\.com\/(\d+)\/?/i,
        commerceDetailRegex: /item\.mall\.mtime\.com\/(\d+)\/?$/i,
        commerceListRegex: /list\.mall\.mtime\.com(?:\/\?(.+)?)?$/i
    };

    //处理点击a标签跳转
    function linkHandle(event) {
        var href = this.href,
            i, len,
            queries,
            index,
            apiName,
            data,
            match
        event.preventDefault();

        if (event.target.nodeName == 'IMG') return false

        if (pcRegex.commerceDetailRegex.test(href)) {
            //商品详情页
            match = pcRegex.commerceDetailRegex.exec(href);
            //data = {
            //    item: match[1]
            //};
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "productDetail",
                            url: 'https://mall-wv.mtime.cn/#!/commerce/item/' + match[1] + '/'
                        })),
                    originUrl: window.location.href,
                };
            }

        } else if (pcRegex.commerceListRegex.test(href)) {
            //商品列表页
            match = pcRegex.commerceListRegex.exec(href);
            if (match) {//有查询条件
                queries = '?' + match[1];
                //queries = match[1].split('&');
                //data = {};
                //for (i = 0, len = queries.length; i < len; i++) {
                //    index = queries[i].indexOf('=');
                //    if (index > -1) {
                //        data[queries[i].slice(0, index)] = queries[i].slice(index + 1);
                //    }
                //}
            } else {//没有查询条件
                queries = '';
            }
            data = {
                applinkData:
                    encodeURIComponent(JSON.stringify({
                        handleType: "jumpPage",
                        pageType: "productList",
                        url: 'https://mall-wv.mtime.cn/#!/commerce/list/' + queries
                    })),
                originUrl: window.location.href,
            };

        } else if (pcRegex.actorRegex.test(href)) {
            //影人详情页
            match = pcRegex.actorRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "starDetail",
                            starId: match[1],
                        })),
                    originUrl: window.location.href,
                };
            }

        } else if (pcRegex.movieDetailRegex.test(href)) {
            //影片详情页
            match = pcRegex.movieDetailRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "movieDetail",
                            movieId: match[1],
                        })),
                    originUrl: window.location.href,
                };
            }
        } else if (pcRegex.postRegex.test(href)) {
            // 帖子
            match = pcRegex.postRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "generalDetails",
                            type: "2",
                            contentId: match[1],
                        })),
                    originUrl: window.location.href,
                };
            }
        } else if (pcRegex.journalRegex.test(href)) {
            // 日志
            match = pcRegex.journalRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "generalDetails",
                            type: "1",
                            contentId: match[1],
                        })),
                    originUrl: window.location.href,
                };
            }
        } else if (pcRegex.articleRegex.test(href)) {
            // 文章
            match = pcRegex.articleRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "generalDetails",
                            type: "4",
                            contentId: match[1],
                        })),
                    originUrl: window.location.href,
                };
            }
        } else if (pcRegex.reviewRegex.test(href)) {
            // 影评
            match = pcRegex.reviewRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "generalDetails",
                            type: "3",
                            contentId: match[1],
                        })),
                    originUrl: window.location.href,
                };
            }
        } else if (pcRegex.communityRegex.test(href)) {
            // 家族
            match = pcRegex.communityRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "groupDetails",
                            groupID: match[1]
                        })),
                    originUrl: window.location.href,
                };
            }
        } else if (pcRegex.listRegex.test(href)) {
            // 榜单
            match = pcRegex.listRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "commonListDetail",
                            listID: match[1]
                        })),
                    originUrl: window.location.href,
                };
            }
        } else if (pcRegex.myRegex.test(href)) {
            // 个人主页
            match = pcRegex.myRegex.exec(href);
            if (match) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "userProfile",
                            userId: match[1]
                        })),
                    originUrl: window.location.href,
                };
            }
        } else if (pcRegex.videoRegex.test(href)) {
            // 视频详情
            match = pcRegex.videoRegex.exec(href);
            let videoSource = href.split("?")[1].split("=")[1]
            if (match && videoSource) {
                data = {
                    applinkData:
                        encodeURIComponent(JSON.stringify({
                            handleType: "jumpPage",
                            pageType: "videoPlayDetail",
                            videoId: match[1],
                            videoSource: videoSource
                        })),
                    originUrl: window.location.href,
                };
            }
        } else {
            data = {
                applinkData: encodeURIComponent(JSON.stringify({
                    handleType: "jumpPage",
                    pageType: "h5",
                    isOpenByBrowser: "NO",
                    url: href
                })),
                originUrl: window.location.href
            }
        }
        apiName = "openAppLinkClient";
        if (apiName) {
            data.originUrl = href;
            window.appBridge({
                'data': data,
                'apiName': apiName
            });
        }
    }

    // 卡片点击跳转
    function linkCardHandle(event) {
        // event.preventDefault();
        event.stopPropagation();
        let id = this.getAttribute('data-mtime-movie-id');
        let href = 'http://movie.mtime.com/' + id + '/'
        let apiName = "openAppLinkClient";
        let data = {
            applinkData:
                encodeURIComponent(JSON.stringify({
                    handleType: "jumpPage",
                    pageType: "movieDetail",
                    movieId: id,
                })),
            originUrl: href
        };

        if (apiName) {
            data.originUrl = href;
            window.appBridge({
                'data': data,
                'apiName': apiName
            });
        }
    }



    //获取网络状态
    function isCellularNetWork() {
        return !(network in { '0': '不可用', '1': 'wifi' });
    }

    //视频播放事件
    function videoPlay() {
        event.preventDefault();
        var offset = this.getBoundingClientRect();

        window.appBridge({
            'data': {
                "videoID": this.children[0].getAttribute('data-video-id'),
                "videoType": this.children[0].getAttribute('data-video-type'),
                "videoTop": offset.top,
                "videoLeft": offset.left,
                "videoWidth": offset.width,
                "videoHeight": offset.height,
            },
            'apiName': 'showVideoPlayer'
        });
    }

    //gif图点击播放事件
    function gifClickHandle(event) {
        var imgSrc, imgNode = this.children[0];
        if (imgNode) {
            imgSrc = imgNode.getAttribute('data-src');
            if (imgSrc) {
                this.removeChild(this.children[1]);
                this.removeEventListener('click', gifClickHandle, false);
                imgLoad.call(imgNode, imgSrc);
                imgNode.addEventListener('click', imgAppView, false);
            }
        }
    }

    //为视频和gif添加播放标签
    function addPlayBtn() {
        var imgWrap,
            playElem;

        if (this.getAttribute('data-vsrc')) {
            imgWrap = this.parentNode;
            playElem = document.createElement('i');
            playElem.className = 'video_boxs_play';
            imgWrap.appendChild(playElem);
        } else if (this.getAttribute('data-src')) {
            imgWrap = this.parentNode;
            playElem = document.createElement('i');
            playElem.innerHTML = 'gif';
            playElem.className = 'gif_play';
            imgWrap.appendChild(playElem);
        }
        this.removeEventListener('load', addPlayBtn, false);
    }

    //加载图片
    function imgLoad(url) {
        // alert("imgLoad url" + url);
        var img = new Image(), _this = this;
        img.onload = function () {
            _this.src = img.src;
            //_this.width = img.width;
            //_this.height = img.width;

            img.onload = null;
            img = null;
            _this = null;
        };
        img.src = url;
    }

    //element是否隐藏
    function isHidden(element) {
        return (element.offsetParent === null);
    }

    //lelement是否在指定view的区域中可见
    function inView(element, view) {
        //if (isHidden(element)) {
        //    return false;
        //}

        var box = element.getBoundingClientRect();
        return (box.right >= view.l && box.bottom >= view.t && box.left <= view.r && box.top <= view.b);
    }

    //图片懒加载
    function imgLazyLoad() {
        var i, len,
            imgs, img,
            view;

        imgs = document.getElementsByTagName('img');
        view = {
            l: 0,
            t: 0,
            b: window.innerHeight || document.documentElement.clientHeight,
            r: window.innerWidth || document.documentElement.clientWidth
        };

        for (i = 0, len = imgs.length; i < len; i++) {
            img = imgs[i];
            if (!img.getAttribute('src')) {
                imgLoad.call(img, img.getAttribute('data-original'));
            }
        }
    }
    document.ready = function () {
        imgLazyLoad();
    }
    // window.addEventListener('resize', imgLazyLoad, false);


    //只在非WIFI 环境下 处理gif
    //预加载下面两种图片
    //var gifDefault = new Image(),//gif默认显示图
    //    imgLoading = new Image();//gif加载时的loading图
    //gifDefault.src = '';
    //imgLoading.src = '';

    //处理所有img标签，对gif图根据网络状态特殊处理
    function gifHandle() {
        var i, len,
            img_nodes,
            img_node,
            dataSrc,
            originSrc,
            imgWrap;

        img_nodes = document.body.getElementsByTagName('img');
        if (isCellularNetWork()) {
            for (i = 0, len = img_nodes.length; i < len; i++) {//只处理gif图
                img_node = img_nodes[i];
                dataSrc = img_node.getAttribute('data-src');
                originSrc = img_node.getAttribute('data-original');
                if (dataSrc && originSrc && dataSrc !== originSrc) {
                    img_node.setAttribute("data-isgif", 1);
                }
                if (dataSrc && dataSrc !== originSrc) {
                    imgWrap = document.createElement('div');
                    imgWrap.className = 'gif_wrap';
                    img_node.parentNode.replaceChild(imgWrap, img_node);
                    imgWrap.appendChild(img_node);

                    img_node.addEventListener('load', addPlayBtn, false);
                    imgWrap.addEventListener('click', gifClickHandle, false);
                }
            }
        } else {
            for (i = 0, len = img_nodes.length; i < len; i++) {//只处理gif图
                img_node = img_nodes[i];
                dataSrc = img_node.getAttribute('data-src');
                originSrc = img_node.getAttribute('data-original');
                if (dataSrc && originSrc && dataSrc !== originSrc) {
                    img_node.setAttribute("data-isgif", 1);
                }
                if (dataSrc) {
                    img_node.setAttribute('data-original', dataSrc);
                }
            }
        }
        imgLazyLoad();
    }

    //页面执行调用的第一个方法
    function getNetWorkType(data) {
        network = data.data.nativeNetStatus;//放到全局
        handleAllImg();
        gifHandle();
    }

    //处理所有视频，将视频替换为首帧静态图片
    function handleAllImg() {
        var i, len;
        //处理video
        var video_template = '<img data-original="#{img_src}" data-video-type="#{vtype}" data-video-id="#{vid}" data-vsrc="#{video_src}" />';

        var video_nodes = document.body.getElementsByTagName('video'),
            videoWrap, video;
        console.log('video_nodes', video_nodes)
        if (video_nodes.length) {
            for (i = 0, len = video_nodes.length; i < len; i++) {
                video = video_nodes[0];
                video.pause();
                videoWrap = document.createElement('div');
                videoWrap.innerHTML = video_template.replace(/#{video_src}/g, video.getAttribute('src')).replace(/#{img_src}/g, video.getAttribute('poster')).
                    replace(/#\{vtype\}/g, video.getAttribute("data-video-type")).replace(/#\{vid\}/g, video.getAttribute("data-video-id"));
                video.parentNode.replaceChild(videoWrap, video);
                videoWrap.className = 'video_boxs';

                console.log('videoWrap', videoWrap)
                videoWrap.addEventListener('click', videoPlay, false);
                videoWrap.children[0].addEventListener('load', addPlayBtn, false);
            }
        }

        //处理所有图片
        var img_nodes = document.body.getElementsByTagName('img'),
            img_node,
            gifsrc,
            figur,
            figurText;

        len = img_nodes.length;
        if (len) {
            allAppViewImgsSrc = getNotVideoImgsSrc(img_nodes);

            for (i = 0; i < len; i++) {
                img_node = img_nodes[i];
                gifsrc = img_node.getAttribute('data-src');
                if (!img_node.getAttribute("data-vsrc")) { //video的缩略图，不当作点击浏览
                    if (!gifsrc || gifsrc === img_node.getAttribute('data-original')) { //没有data-src,不是老版内容，就是普通图片
                        img_node.addEventListener('click', imgAppView, false);
                    } else { //gif图
                        if (isCellularNetWork()) {
                            if (img_node.src === gifsrc) {//非wifi环境下，只有加载过的gif才能点击浏览
                                img_node.addEventListener('click', imgAppView, false);
                            }
                        } else {
                            img_node.addEventListener('click', imgAppView, false);
                        }
                    }
                    //图注内容最多20字
                    figur = img_node.nextElementSibling;
                    if (figur) {
                        figurText = figur.innerHTML;
                        if (figurText.length < 21) {
                            figur.innerHTML = figurText.slice(0, 20);
                        }
                    }
                }
            }
        }
    }

    window.appBridge({
        'apiName': 'getNativeNetStatus',
        'callback': (res)=>{
            console.log('res2222', res)
            getNetWorkType(res);
        }
    });


    //处理跳转页面的链接
    var all_link = document.body.getElementsByTagName('a'),
        a_node;
    len = all_link.length;
    console.log('all_link', all_link)
    if (len) {
        console.log('all_link', all_link)
        for (i = 0; i < len; i++) {
            a_node = all_link[i];
            a_node.addEventListener('click', linkHandle, false);
        }
    }
    // 处理卡片
    var all_link_card = document.getElementsByClassName('movieCard'),
        card_node,
        len_card = all_link_card.length;
    if (len_card) {
        for (i = 0; i < len_card; i++) {
            card_node = all_link_card[i];
            card_node.addEventListener('click', linkCardHandle, true);
        }
    }

    //图片浏览事件
    function imgAppView() {
        var index = -1;
        for (var i = 0, len = allAppViewImgsSrc.length; i < len; i++) {
            if (this.src == allAppViewImgsSrc[i]) {
                index = i;
                break;
            }
        }
        if (index === -1) {
            //说明图片还没加载完
            return;
        }
        var gifSrc = this.getAttribute('data-src');
        window.appBridge({
            'data': {
                'isGif': this.getAttribute("data-isgif") === "1" ? true : false,
                'currentImageIndex': index,
                'photoImageUrls': allAppViewImgsSrc || [],
                'originUrl': this.src || ''
            },
            'apiName': 'openImageBrowser'
        });
    }

    //获取所有不是视频首帧的图片
    function getNotVideoImgsSrc(img_nodes) {
        var img_node,
            len, i,
            imgsSrcArr;

        len = img_nodes.length;

        if (len) {
            i = 0;
            imgsSrcArr = [];
            for (; i < len; i++) {
                img_node = img_nodes[i];
                //video的缩略图和卡片，不计算在内
                if (!img_node.getAttribute("data-vsrc") && !img_node.getAttribute("data-mtime-img")) {
                    imgsSrcArr.push(img_node.getAttribute('data-src') || img_node.getAttribute('data-original') || img_node.src);
                }
            }
        }
        return imgsSrcArr;
    }

    // //注册app需要使用的js方法
    // window.getNetWorkType = getNetWorkType;
    // window.registJSBridge(getNetWorkType);

})();