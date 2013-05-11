var fs = require('fs');
var webpage = require('webpage');
var pageSize = 60;
var rootDir = '/tmp/aa/';

//var c =FileUtils.getLocalCache()._mCache[0]

var index = 0;
function loadUrl(url){
    var page = webpage.create();
    console.log('###'+url);
    page.open(url, 'get', null, function (status) {
            try{
                if (status !== 'success') {
                    console.log('Request failed');
                } else {
                    var c = page.content;
                    var start = c.indexOf('http://www.baidupcs.com/file/');
                    var end = c.indexOf('" id="downFileButtom"');
                    console.log(c.substring(start, end).replace(/&amp;/g, '&'));                    
                }
            }catch(e){
                console.log(e);
            }
            if(index<urls.length){              
                loadUrl(urls[index++]);
            }
    });
}
loadUrl(0);


var urls = [
"http://pan.baidu.com/share/link?shareid=433658&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=431784&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=430927&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=429811&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=423948&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=409427&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=402048&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=401243&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=400844&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=400507&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=398319&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=398304&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=398153&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=395709&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=395102&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=393456&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=391726&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=391704&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=389958&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=389943&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=388514&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=387099&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=387057&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=387043&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=386165&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=384705&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=382721&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=382661&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=382520&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=382517&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=381612&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=380853&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=380850&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=380839&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=380672&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=380618&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=380610&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=380604&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=379646&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=379632&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374124&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374122&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374120&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374117&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374116&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374114&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374110&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374108&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=374100&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373745&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373744&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373736&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373726&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373722&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373681&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373667&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373643&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373605&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373603&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373601&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373600&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373599&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373598&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373596&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373595&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373593&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373585&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373582&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373581&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373580&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373579&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373578&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373577&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373511&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373509&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373507&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373505&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373504&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373501&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373500&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373499&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373497&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373496&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373492&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373491&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373489&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373488&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373487&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373486&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373483&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373475&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373469&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373467&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373466&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373465&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373464&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373463&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373462&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373461&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373459&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373458&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373457&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373455&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373454&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373451&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373442&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373437&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373435&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373427&uk=1578587660",
"http://pan.baidu.com/share/link?shareid=373425&uk=1578587660"
]