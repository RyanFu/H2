var casper = require('casper').create();

function getLinks() {
    var links = document.querySelectorAll('a');
    return Array.prototype.map.call(links, function(e) {
        return e.getAttribute('href')
    });
}

casper.start('http://baike.baidu.com/view/548227.htm');
casper.then(function(){
    var links = this.evaluate(getLinks);    
    for(var i in links){
        var l = links[i];
        if(l.indexOf('/picview/')==0){
            l = 'http://baike.baidu.com'+l;
            this.echo(l); 
            casper.thenOpen(l, function(){
                this.echo(casper.getCurrentUrl());
                var c = this.evaluate(listUrl);
                for(var i in c){
                     console.log(c[i]);
                     this.download(c[i], i+'.jpg');                                          
                }
            });
            return;
        }
    }
});

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

