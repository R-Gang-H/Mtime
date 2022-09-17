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

    }

}


