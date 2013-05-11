var server = require('webserver').create();
var fs = require('fs');

var TAFFY = require("./taffy").taffy;

var DB_NAME = 'rank.db';

var ERROR_CODE = {
	"SUCCESSED" : 1,
	"FAILED_NOT_DATA" : 2,
	"NO_SUCH_METHOD" : 3
};

var content = '', f = null;

phantom.onError = function(msg, trace) {
    var msgStack = ['PHANTOM ERROR: ' + msg];
    if (trace) {
        msgStack.push('TRACE:');
        trace.forEach(function(t) {
            msgStack.push(' -> ' + (t.file || t.sourceURL) + ': ' + 
							t.line + (t.function ? ' (in function ' + t.function + ')' : ''));
        });
    }
    console.error(msgStack.join('\n'));
};

phantom.exit = (function() {
	var realExit = phantom.exit;
	return function() {
		if (typeof phantom.onExit === 'function') {
			phantom.onExit.apply(this, arguments);  // Just to pass along the exit code, if desired
		}
		realExit.apply(this, arguments);
	};
})();

try {
    f = fs.open(DB_NAME, "r");
    content = f.read();
} catch (e) {
    console.log(e);
}

if (f) {
    f.close();
}

var rank = TAFFY(content);

phantom.onExit = function(msg, trace) {
	var items = new Array(); 
	rank().each(function(item){
		if(item === undefined){
			//NG
		}else{
			items.push(JSON.stringify(item));
		}
	});
	try {
		fs.write(DB_NAME, JSON.stringify(items));
	} catch (e) {
		console.error(e);
	}
};

// 服务器数据库表项封装
function Info(img, count) {
	this.imgId = img;
	this.favorited = count;
}

// 服务器返回值封装
function Result(code, data) {
	this.code = code;
	this.data = data;
}

// Url参数解析
function urlQuery(url, query) {
	query = query.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var expr = "[\\?&]"+query+"=([^&#]*)";
	var regex = new RegExp( expr );
	var results = regex.exec(url);
	if ( results !== null ) {
		return results[1];
	} else {
		return false;
	}
}

// 删除json生成时候产生的多余内容
function replacer(key, value) {
	if (key=="___id"||key=="___s") {
		return undefined;
	} else {
		return value;
	}
}

// 服务器监听类
var service = server.listen(8080, function(request, response) {
	response.statusCode = 200;
	response.headers = {
		'Cache': 'no-cache',
		'Content-Type': 'text/plain;charset=utf-8'
	};
	var urls = request.url.split('?');
	if(urls[0] === "/update") {
		var id = urlQuery(request.url, 'imgId');
		if(!id) {
			response.write(JSON.stringify(new Result(ERROR_CODE['NO_SUCH_METHOD'], null), replacer));
		} else {
			var info = rank().filter({ imgId : id}).get();
			console.log("favorited: " + id);
			if(info[0] === undefined || info[0]['imgId'] === undefined) {
				rank.insert(new Info(id, 1));
			} else {
				rank({imgId:id}).update({favorited:info[0]['favorited'] + 1});
			}
			response.write(JSON.stringify(new Result(ERROR_CODE['SUCCESSED'], null)));
		}
	} else if(urls[0] === "/get") {
		var id = urlQuery(request.url, 'imgId');
		if(id){
			console.log("query id: " + id);
			var info = rank().filter({ imgId : id}).get();
			response.write(JSON.stringify(new Result(ERROR_CODE['SUCCESSED'], info), replacer));
		} else {
			console.log("query rank: " + id);
			var from = urlQuery(request.url, 'from');
			if(!from) from = 0;
			var count = urlQuery(request.url, 'count');
			if(!count) {
				count = 100;
			}
			var items = new Array(); 
			var index = 0;
			rank().order("favorited desc").each(function(item){
				if((index ++ < from) || (item === undefined) || ((index - from) > count)) {
					//NG
				} else {
					items.push(item);
					console.log(JSON.stringify(item, replacer));
				}
			});
			response.write(JSON.stringify(new Result(ERROR_CODE['SUCCESSED'], items), replacer));
		}
	} else {
		response.write(JSON.stringify(new Result(ERROR_CODE['NO_SUCH_METHOD'], null), replacer));
	}
	response.close();
});

// 每五分钟保存一下实时数据库中的内容
setInterval(function() {
	var items = new Array(); 
	rank().each(function(item){
		if(item === undefined){
			//NG
		}else{
			items.push(JSON.stringify(item));
		}
	});
	try {
		fs.write(DB_NAME, "[" + items + "]");
	} catch (e) {
		console.log(e);
	}
}, 5 * 60000);
