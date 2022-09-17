(function () {

  var detail = document.getElementsByClassName("txtmid")[0];
  var width = document.body.clientWidth;
  var height = width * 0.85;
  var imgMap = document.createElement("div");
  imgMap.id = "imgMap";
 var imgList =[];
   var str=document.getElementById("imgList").innerHTML;
   str.replace(/\s/g,"")
   if(str.length>0){
    imgList=document.getElementById("imgList").innerHTML.split(",")
   }
//  var imgList = document.getElementById("imgList").innerHTML.split(",");
  var text = "";
  switch (imgList.length) {
    case 1:
     text = `<div class="imgList1">
         <img src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
    </div>`
     break;
    case 2:
      text = `<div class="imgList1">
        <div>
          <img src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div>
        <img src=${imgList[1]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
     </div>`
      break;
    case 3:
      text = `<div class="imgList1">
      <div class="img height100">
        <img  src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
      </div>
      <div class="img">
        <img src=${imgList[1]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        <img src="${imgList[2]}" alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
      </div>
    </div>`
      break;
    case 4:
      text =
        `<div class="imgList2">
          <div>
            <img src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
          </div>
          <div >
          <img src=${imgList[1]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
          </div>
        </div>
        <div class="imgList2">
          <div>
            <img src=${imgList[2]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
          </div>
          <div >
          <img src=${imgList[3]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
          </div>
        </div>`
      break;
    case 5:
      text = `<div class="imgList1">
        <div class="img height100">
          <img  src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div class="img">
          <img src=${imgList[1]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
          <img src="${imgList[2]}" alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
      </div> 
      <div class="imgList2">
        <div>
          <img src=${imgList[3]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[4]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>`
      break;
    case 6:
      text = `<div class="imgList1">
        <div class="img height100">
          <img  src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div class="img">
          <img src=${imgList[1]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
          <img src="${imgList[2]}" alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
      </div>
      <div class="rowlist">
        <div>
          <img src=${imgList[3]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[4]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[5]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>`
      break;
    case 7:
      text = `
      <div class="imgList2">
        <div>
          <img src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[1]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>
      <div class="imgList2">
        <div>
          <img src=${imgList[2]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[3]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>
      <div class="rowlist">
        <div>
          <img src=${imgList[4]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[5]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[6]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>`
      break;
    case 8:
      text = `
      <div class="imgList2">
        <div>
          <img src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[1]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
     </div>
      <div class="rowlist">
        <div>
          <img src=${imgList[2]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[3]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[4]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>
      <div class="rowlist">
        <div>
          <img src=${imgList[5]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[6]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[7]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>`
      break;
    case 9:
      text = `
      <div class="rowlist">
        <div>
          <img src=${imgList[0]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[1]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div>
          <img src=${imgList[2]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
     </div>
      <div class="rowlist">
        <div >
          <img src=${imgList[3]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[4]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div>
          <img src=${imgList[5]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>
      <div class="rowlist">
        <div >
          <img src=${imgList[6]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[7]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
        <div >
          <img src=${imgList[8]} alt="" onerror="javascript:this.src='file:///android_asset/Image/img_placeholder.png';">
        </div>
    </div>`
      break;
    default:
      break;
  }
  imgMap.innerHTML = text;
  detail.appendChild(imgMap);
  var imgList1Arr=document.getElementsByClassName("imgList1");
  if(imgList1Arr.length>0){
    for(var i=0;i<imgList1Arr.length;i++){
      imgList1Arr[i].style.height=height*0.66+ "px";
    }
  }
  var imgList2Arr=document.getElementsByClassName("imgList2");
  if(imgList2Arr.length>0){
    for(var i=0;i<imgList2Arr.length;i++){
      imgList2Arr[i].style.height=height*0.33+ "px";
    }
  }
  var rowlistArr=document.getElementsByClassName("rowlist");

  if(rowlistArr.length>0){
    for(var i=0;i<rowlistArr.length;i++){
      rowlistArr[i].style.height=height*0.33+ "px";
    }
  }
  imgMap.style.height=(imgList1Arr.length*height*0.66+ imgList2Arr.length*height*0.33+rowlistArr.length*height*0.33 - 10 )+ "px";
  
})();
