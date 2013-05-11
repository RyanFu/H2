var casper = require('casper').create({
    //timeout:3000
});

var fs = require('fs'); 
var rootDir = '/tmp/';

casper.start();

var urls = [/*'http://baike.baidu.com/view/667852.htm',
'http://baike.baidu.com/view/25276.htm',
'http://baike.baidu.com/view/293208.htm',
'http://baike.baidu.com/view/53288.htm',
'http://baike.baidu.com/view/21620.htm',
'http://baike.baidu.com/view/2132269.htm',
'http://baike.baidu.com/view/2497107.htm',
'http://baike.baidu.com/view/1372114.htm',
'http://baike.baidu.com/view/2776793.htm',
'http://baike.baidu.com/view/44942.htm',
'http://baike.baidu.com/view/4056607.htm',
'http://baike.baidu.com/view/1874345.htm',
'http://baike.baidu.com/view/97968.htm',
'http://baike.baidu.com/view/548346.htm',
'http://baike.baidu.com/view/6201658.htm',
'http://baike.baidu.com/view/120365.htm',
'http://baike.baidu.com/view/3715630.htm',
'http://baike.baidu.com/view/4267184.htm',
'http://baike.baidu.com/view/2002858.htm',
'http://baike.baidu.com/view/1003513.htm',
'http://baike.baidu.com/view/1043523.htm',
'http://baike.baidu.com/view/2881721.htm',
'http://baike.baidu.com/view/3415063.htm',
'http://baike.baidu.com/view/1863071.htm',
'http://baike.baidu.com/view/1751582.htm',
'http://baike.baidu.com/view/4003065.htm',
'http://baike.baidu.com/view/2195554.htm',
'http://baike.baidu.com/view/617921.htm',
'http://baike.baidu.com/view/3588933.htm',
'http://baike.baidu.com/view/1818025.htm',
'http://baike.baidu.com/view/1980944.htm',
'http://baike.baidu.com/view/2708552.htm',
'http://baike.baidu.com/view/6271061.htm',
'http://baike.baidu.com/view/1021963.htm',
'http://baike.baidu.com/view/2856954.htm',
'http://baike.baidu.com/view/1589815.htm',
'http://baike.baidu.com/view/3287965.htm',
'http://baike.baidu.com/view/4151366.htm',
'http://baike.baidu.com/view/1333603.htm',
'http://baike.baidu.com/view/3959105.htm',
'http://baike.baidu.com/view/1019908.htm',
'http://baike.baidu.com/view/723907.htm',
'http://baike.baidu.com/view/5903474.htm',
'http://baike.baidu.com/view/3014767.htm',
'http://baike.baidu.com/view/1588171.htm',
'http://baike.baidu.com/view/1000222.htm',
'http://baike.baidu.com/view/126007.htm',
'http://baike.baidu.com/view/1238087.htm',
'http://baike.baidu.com/view/5669480.htm',
'http://baike.baidu.com/view/1772127.htm',
'http://baike.baidu.com/view/5127678.htm',
'http://baike.baidu.com/view/3859079.htm',
'http://baike.baidu.com/view/444248.htm',
'http://baike.baidu.com/view/2100972.htm',
'http://baike.baidu.com/view/2298927.htm',
'http://baike.baidu.com/view/4576404.htm',
'http://baike.baidu.com/view/6326873.htm',
'http://baike.baidu.com/view/4532552.htm',
'http://baike.baidu.com/view/222677.htm',
'http://baike.baidu.com/view/2539490.htm',
'http://baike.baidu.com/view/2947651.htm',
'http://baike.baidu.com/view/2581244.htm',
'http://baike.baidu.com/view/3330427.htm',
'http://baike.baidu.com/view/5289143.htm',
'http://baike.baidu.com/view/348955.htm',
'http://baike.baidu.com/view/2675570.htm',
'http://baike.baidu.com/view/1297262.htm',
'http://baike.baidu.com/view/2132654.htm',
'http://baike.baidu.com/view/3227782.htm',
'http://baike.baidu.com/view/262426.htm',
'http://baike.baidu.com/view/1145936.htm',
'http://baike.baidu.com/view/3393108.htm',
'http://baike.baidu.com/view/609739.htm',
'http://baike.baidu.com/view/3689204.htm',
'http://baike.baidu.com/view/237730.htm',
'http://baike.baidu.com/view/6214990.htm',
'http://baike.baidu.com/view/6375144.htm',
'http://baike.baidu.com/view/5862902.htm',
'http://baike.baidu.com/view/1518794.htm',
'http://baike.baidu.com/view/6334857.htm',
'http://baike.baidu.com/view/1014920.htm',
'http://baike.baidu.com/view/998593.htm',
'http://baike.baidu.com/view/2135365.htm',
'http://baike.baidu.com/view/6987686.htm',
'http://baike.baidu.com/view/3923098.htm',
'http://baike.baidu.com/view/3223912.htm',
'http://baike.baidu.com/view/3319930.htm',
'http://baike.baidu.com/view/562423.htm',
'http://baike.baidu.com/view/1174760.htm',
'http://baike.baidu.com/view/2632507.htm',
'http://baike.baidu.com/view/1839958.htm',
'http://baike.baidu.com/view/3229957.htm',
'http://baike.baidu.com/view/3295642.htm',
'http://baike.baidu.com/view/6657813.htm',
'http://baike.baidu.com/view/1140316.htm',
'http://baike.baidu.com/view/1496351.htm',
'http://baike.baidu.com/view/5394305.htm',
'http://baike.baidu.com/view/3113551.htm',
'http://baike.baidu.com/view/6295392.htm',
'http://baike.baidu.com/view/6250069.htm',
'http://baike.baidu.com/view/2729237.htm',
'http://baike.baidu.com/view/5580492.htm',
'http://baike.baidu.com/view/6290427.htm',
'http://baike.baidu.com/view/5127029.htm',
'http://baike.baidu.com/view/1280725.htm',
'http://baike.baidu.com/view/1702716.htm',
'http://baike.baidu.com/view/1001958.htm',
'http://baike.baidu.com/view/3176332.htm',
'http://baike.baidu.com/view/2057111.htm',
'http://baike.baidu.com/view/3203981.htm',
'http://baike.baidu.com/view/5930469.htm',
'http://baike.baidu.com/view/2118853.htm',
'http://baike.baidu.com/view/5894791.htm',
'http://baike.baidu.com/view/1477755.htm',
'http://baike.baidu.com/view/1043513.htm',
'http://baike.baidu.com/view/3623991.htm',
'http://baike.baidu.com/view/6175243.htm',
'http://baike.baidu.com/view/3230494.htm',
'http://baike.baidu.com/view/1483258.htm',
'http://baike.baidu.com/view/2197507.htm',
'http://baike.baidu.com/view/1800053.htm',
'http://baike.baidu.com/view/2074460.htm',
'http://baike.baidu.com/view/2177482.htm',
'http://baike.baidu.com/view/2074486.htm',
'http://baike.baidu.com/view/2766733.htm',
'http://baike.baidu.com/view/1588163.htm',
'http://baike.baidu.com/view/1642570.htm',
'http://baike.baidu.com/view/7099316.htm',
'http://baike.baidu.com/view/2905469.htm',
'http://baike.baidu.com/view/3259925.htm',
'http://baike.baidu.com/view/1740063.htm',
'http://baike.baidu.com/view/1699391.htm',
'http://baike.baidu.com/view/1490548.htm',
'http://baike.baidu.com/view/5697308.htm',
'http://baike.baidu.com/view/5930567.htm',
'http://baike.baidu.com/view/1060453.htm',
'http://baike.baidu.com/view/6291623.htm',
'http://baike.baidu.com/view/6319166.htm',
'http://baike.baidu.com/view/7223642.htm',
'http://baike.baidu.com/view/5335196.htm',
'http://baike.baidu.com/view/5156933.htm',
'http://baike.baidu.com/view/6278855.htm',
'http://baike.baidu.com/view/7671945.htm',
'http://baike.baidu.com/view/8681308.htm',
'http://baike.baidu.com/view/6308816.htm',
'http://baike.baidu.com/view/2662729.htm',
'http://baike.baidu.com/view/4890525.htm',
'http://baike.baidu.com/view/2730484.htm',
'http://baike.baidu.com/view/2352159.htm',
'http://baike.baidu.com/view/4292541.htm',
'http://baike.baidu.com/view/6270524.htm',
'http://baike.baidu.com/view/4782697.htm',
'http://baike.baidu.com/view/7875229.htm',
'http://baike.baidu.com/view/5388973.htm',
'http://baike.baidu.com/view/2755084.htm',
'http://baike.baidu.com/view/8336704.htm',
'http://baike.baidu.com/view/8153555.htm',
'http://baike.baidu.com/view/4584889.htm',
'http://baike.baidu.com/view/1079445.htm',
'http://baike.baidu.com/view/6622494.htm',
'http://baike.baidu.com/view/1661389.htm',*/
'http://baike.baidu.com/view/548227.htm',
'http://baike.baidu.com/view/5903542.htm',
'http://baike.baidu.com/view/807668.htm',
'http://baike.baidu.com/view/3034290.htm',
'http://baike.baidu.com/view/1024383.htm',
'http://baike.baidu.com/view/3454095.htm',
'http://baike.baidu.com/view/4277848.htm',
'http://baike.baidu.com/view/4971036.htm',
'http://baike.baidu.com/view/2941310.htm',
'http://baike.baidu.com/view/5126372.htm',
'http://baike.baidu.com/view/5126423.htm',
'http://baike.baidu.com/view/5126404.htm',
'http://baike.baidu.com/view/6791539.htm',
'http://baike.baidu.com/view/5126695.htm',
'http://baike.baidu.com/view/5799136.htm',
'http://baike.baidu.com/view/1153022.htm',
'http://baike.baidu.com/view/44222.htm',
'http://baike.baidu.com/view/5903732.htm',
'http://baike.baidu.com/view/5903516.htm',
'http://baike.baidu.com/view/2876898.htm',
'http://baike.baidu.com/view/447273.htm',
'http://baike.baidu.com/view/5683390.htm',
'http://baike.baidu.com/view/5127563.htm',
'http://baike.baidu.com/view/5903520.htm',
'http://baike.baidu.com/view/5903530.htm',
'http://baike.baidu.com/view/8278895.htm',
'http://baike.baidu.com/view/3230011.htm',
'http://baike.baidu.com/view/3186479.htm',
'http://baike.baidu.com/view/3571979.htm',
'http://baike.baidu.com/view/5127098.htm',
'http://baike.baidu.com/view/5126494.htm',
'http://baike.baidu.com/view/5126569.htm',
'http://baike.baidu.com/view/6474892.htm',
'http://baike.baidu.com/view/1297172.htm',
'http://baike.baidu.com/view/793880.htm',
'http://baike.baidu.com/view/1025798.htm',
'http://baike.baidu.com/view/3074316.htm'];



function processEntry(url){
    console.log(url);
    casper.thenOpen(url, function(){
        var url = this.getCurrentUrl();
        if(url.indexOf('ressafe.html')>0){
             this.click('a');
             casper.then(function(){
                 casper.echo('redirected');
                 processEntryPage(this);                
             });            
        }else{        
            processEntryPage(this);
        }        
    });
}

function processEntryPage(context){
    var url = context.getCurrentUrl();
    var title = context.getTitle();
    casper.echo(url);
    casper.echo(title);
    var links = context.evaluate(getLinks);
    if(!links || links.length==0){
        return;
    }
    for(var i in links){
        var l = links[i];
        if(l && l.indexOf('/picview')>0){
            //console.log(l);
            var start = url.lastIndexOf('/');
            var end = url.indexOf('.ht');                       
            processAllImagesInPicViewPage(l,rootDir + url.slice(start+1,end)+'_'+title);                    
            break;
        }
    }    
}

function processAllImagesInPicViewPage(url, dir){       
    casper.thenOpen(url, function(){
        console.log('Processing pic view url ' + casper.getCurrentUrl());
        if(fs.exists(dir)==true){
            console.log('dir exists '+ dir);
        }        
        var c = this.evaluate(listUrl);
        
        for(var i in c){
            var imageUrl = c[i];
            var filePath = dir+'/'+i+'.jpg';
            if(fs.exists(filePath)==true){
                console.log('file exists for image '+ imageUrl +'   '+filePath);
            }else{
                console.log('downloading image '+ imageUrl +'   '+filePath);
                this.download(c[i],filePath);
            }                                          
        }
    });    
}

function getLinks() {
    var links = document.querySelectorAll('a');
    return Array.prototype.map.call(links, function(e) {
        return e.href;
    });
}

for(var i in urls){
    /*if(i == 2){
        break;
    }*/
    processEntry(urls[i]);
}

casper.run();





function listUrl(){
    try{
    
    function dumpInfo(info){
        var r = [];
        var albums = info.albumList;
        for(var i = 0;i<albums.length;i++){
            pics = albums[i].pic;
            for(pic in pics){
                var sizes = pics[pic].sizes
                var largest = sizes[getLargestSize(sizes)]; 
                r[r.length]=largest.url;            
            }
        }
        return r;
    }
    
    function getLargestSize(sizes){
        var r = '0';    
        for(s in sizes){        
            if(parseInt(s)>parseInt(r)){
                r = s;
            }
        }
        return r;
    }

    
    var scripts = document.getElementsByTagName('script');  
    for(var i =0 ;i<scripts.length;i++){
        var txt = scripts[i].innerText;
        if(!txt) continue;
        txt = txt.trim();
        if(txt.indexOf('var baikeInfo')==0){
            
            console.log(txt);
            eval(txt);          
            return dumpInfo(baikeInfo);         
        }
    }
    }catch(e){
        return e;
    }   
}


