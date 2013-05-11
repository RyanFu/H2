var casper = require('casper').create({
     pageSettings: {
        useragent:'Mozilla/5.0 (Linux; U; Android 2.2; en-us; DROID2 GLOBAL Build/S273) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1'
    }
});

//casper.start('http://wapbaike.baidu.com/view/548227.htm');
casper.start('http://news.sina.com.cn');
casper.then(function(){
    this.capture('/tmp/aaa.png');    
});

casper.run();
