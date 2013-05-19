var fs = require('fs');
var cs = require('casper').create({
    pageSettings : {
        loadImages : false,
        loadPlugins : false,
    },
    viewportSize : {
        width : 800,
        height : 600
    },
    clientScripts : [ 'jquery-1.9.1.js' ]
});

cs.start();
cs.thenOpen('http://www.mm-girl.com/girl-star-91.html');
cs.then(function() {
    console.log('ok');    
    console.log(this.evaluate(getList));
    console.log('done');
});

cs.run();

function getList() {    
    try {
        var r = [];
        var a1 = $('img[src="/img/xiangqing.jpg"]');        
        var a2 = $('img[src="/img/xiangqing.jpg"]');
        for ( var i = 0; i < a1.length; i++) {
            if(a1[i].parentNode && a2[i].parentNode)
            r[r.length] = [ a1[i].parentNode.href, a2[i].parentNode.href ];
        }
        return JSON.stringify(r);
    } catch (e) {
        return e.toString();
    }
}