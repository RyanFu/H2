var fs = require('fs');
var webpage = require('webpage');
var pageSize = 60;
var rootDir = '/tmp/aa/';


var page = webpage.create();
page.open('http://pan.baidu.com/share/home?uk=1578587660', 'get', null, function (status) {
        try{
            if (status !== 'success') {
                console.log('Request failed');
            } else {
                var s = page.content;
                console.log(s);
            }
        }catch(e){
            console.log(e);
        }
        //getImage(startIndex+pageSize, count);
        //setTimeout(function(){getImage(startIndex+pageSize, count);}, 500);        
    });
    
    
    
function pad(size, s){
    s = s+'';    
    return String("0000000" + s).slice(-size);
}

function getImage(startIndex, count){
    var filePath = rootDir+pad(6,startIndex)+'_'+pad(6,count)+'.json';
    if(fs.exists(filePath)==true){
        console.log(filePath);
        getImage(startIndex+pageSize, count);
        return;
    }
        
    var page = webpage.create();
    var url = 'http://tu.baidu.com/channel/listjson?fr=channel&tag1=%E7%BE%8E%E5%A5%B3&tag2=%E5%85%A8%E9%83%A8&sorttype=0&pn=' + startIndex + '&rn=' + count + '&ie=utf8&oe=utf-8&1365082781536';
    console.log('######################################');
    console.log(url); 
    page.open(url, 'get', null, function (status) {
        try{
            if (status !== 'success') {
                console.log('Request failed');
            } else {
                var s = page.content;
                var start = '<html><head></head><body>'.length;        
                var length = s.length - '</body></html>'.length;
                s = s.substring(start,length)
                var file = fs.open(filePath,'w');
                file.write(s);
                file.close();
                var data = JSON.parse(s).data;
                for(var i = 0;i<data.length;i++){
                    if(data[i] && data[i].image_url){                
                        console.log(data[i].image_url);
                    }
                }            
            }
        }catch(e){
            console.log(e);
        }
        getImage(startIndex+pageSize, count);
        //setTimeout(function(){getImage(startIndex+pageSize, count);}, 500);        
    });
}

//getImage(0, pageSize);