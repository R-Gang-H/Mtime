self.onload = function () {

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

    var img_template = '<img src="#{img_src}" /><em class="em"></em>',
        img_nodes = document.body.getElementsByTagName('img'),
        imgs = [],
        srcs = [];
    for (var i = 0, l = img_nodes.length; i < l; ++i) {
        imgs.push(img_nodes.item(i));
        srcs.push(img_nodes.item(i).src);
    }

    var imgcheck = function () {
        if (this.width > 200) {
            var src = this.src,
                new_img_node = document.createElement('div');
            //srcs.push(src);
            this.parentNode.replaceChild(new_img_node, this);
            new_img_node.innerHTML = img_template.replace(/#{img_src}/g, src);
            new_img_node.className = 'img_boxs';
            new_img_node.addEventListener('click', function () {
                location.href = 'click:' + src + ';' + srcs.join(';');
            }, false);
        }
    }


    for (var i = 0, l = imgs.length, img; i < l; ++i) {
        img = imgs[i];
        img.onload = imgcheck;
        var src = img.src;
        img.src = '';
        img.src = src;
    }


    /*var $main = document.getElementsByClassName('main')[0];
     if(!$main){
     return;
     }
     var img_template = '<img src="#{img_src}" /><em class="em"></em>',
     imgs = [].slice.apply($main.getElementsByTagName('img')),
     srcs = [],
     videolist = [].slice.apply(document.getElementsByTagName('video'));

     var imgs = [];
     for(var i=0,l=img_nodes.length;i<l;++i){
     imgs.push(img_nodes.item(i));
     }


     var imgcheck = function(){
     var src = this.src,
     new_img_node = document.createElement('div');
     new_img_node.className = 'img_boxs';

     this.parentNode.replaceChild(new_img_node,this);
     new_img_node.innerHTML = img_template.replace(/#{img_src}/g,src);
     new_img_node.addEventListener('click', function(){
     location.href = 'click:'+src+';'+srcs.join(';');
     }, false);
     }


     for(var i=0,l=imgs.length,img;i<l;++i){
     img = imgs[i];
     img.onload = imgcheck;
     var src = img.src;
     srcs.push(src);
     img.src = '';
     img.src = src;
     }*/

    var videolist = [].slice.apply(document.getElementsByTagName('video'));
    var video_template = '<a href="click2:#{video_src}" rel="#{video_src}"><img src="#{img_src}" onclick="videoImgClick(event)" onload="imgOnLoad(this)" ><i onclick="videoImgClick(event)"></i></a>';

    var video_nodes = document.body.getElementsByTagName('video');
    var videos = [];
    for (var i = 0, l = video_nodes.length; i < l; ++i) {
        videos.push(video_nodes.item(i));
        video_nodes[i].pause();
    }

    for (var i = 0, l = videos.length; i < l; ++i) {
        var _video = videos[i];
        var videoImg = _video.poster;
        var new_video_node = document.createElement('div');
        var rect = _video.getBoundingClientRect();
        var appProtocalParam = [
            "videoID=" + _video.getAttribute("data-video-id"),
            "videoType=" + _video.getAttribute("data-video-type")
        ];
        //showVideoPlayer?videoID=100&videoType=1&videoTop=200&videoLeft=15&videoWidth=345&videoHeight=240
        _video.parentNode.replaceChild(new_video_node, videos[i]);
        new_video_node.innerHTML = video_template.replace(/#{video_src}/g,
            "//showVideoPlayer?" + appProtocalParam.join("&")).replace(/#{img_src}/g, videoImg);
        new_video_node.className = 'video_boxs';

    }

    //    for(var i = 0, l = videolist.length ; i < l ; ++i){
    //        videolist[i].width = 250;
    //        videolist[i].height = 150;
    //    }


};

function imgOnLoad(imgNode) {
    imgNode.nextSibling.className = "video_boxs_play";
    var rect = imgNode.getBoundingClientRect();
    var appProtocalParam = [
        "videoTop=" + rect.top,
        "videoLeft=" + rect.left,
        "videoWidth=" + rect.width,
        "videoHeight=" + rect.height
    ];
    imgNode.parentNode.href += "&" + appProtocalParam.join("&");

}

function videoImgClick(e) {
    e.preventDefault();
    e.stopPropagation();
    location.href = e.target.parentNode.href;
}



/*
self.onload = function () {
    var img_template = '<img src="#{img_src}"/><em class="em"></em>';
    var img_nomore_template = '<img src="#{img_src}"/>';
    var img_nodes = document.body.getElementsByTagName('img');
    var imgs = [];
    for (var i = 0, l = img_nodes.length; i < l; ++i) {
        if (img_nodes.item(i).id != "imgDot") {
            imgs.push(img_nodes.item(i));
        }
    }

    for (var i = 0, l = imgs.length; i < l; ++i) {
        var img = imgs[i];
        var src = img.src;
        var template = img.width > 200 ? img_template : img_nomore_template;

        var new_img_container = document.createElement('div');
        imgs[i].parentNode.replaceChild(new_img_container, imgs[i]);

        new_img_container.innerHTML = template.replace(/#{img_src}/g, src);
        new_img_container.className = 'img_boxs';
        new_img_container.setAttribute("url", src);
        new_img_container.addEventListener('click', function () {
            var url = this.getAttribute("url");
            location.href = url;
        }, false);
    }

//    var video_template = '<video src="#{video_src}"></video>';
    var video_template = '<a rel="#{video_src}"><img src="#{img_src}"><i></i></a>';
    var video_nodes = document.body.getElementsByTagName('video');
    var videos = [];
    for (var i = 0, l = video_nodes.length; i < l; ++i) {
        videos.push(video_nodes.item(i));
    }

    for (var i = 0, l = videos.length; i < l; ++i) {
        var src = videos[i].src;
        var videoImg = videos[i].poster;
        var new_video_node = document.createElement('div');
        videos[i].parentNode.replaceChild(new_video_node, videos[i]);
        new_video_node.innerHTML = video_template.replace('#{video_src}', src).replace(/#{img_src}/g,videoImg);
        new_video_node.className = 'video_boxs';

        new_video_node.setAttribute("url", src);
        new_video_node.addEventListener('click', function () {
            var url = this.getAttribute("url");
            location.href = url;
        }, false);

    }
}
*/


