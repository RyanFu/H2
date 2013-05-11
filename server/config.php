<?php
	error_reporting(E_USER_ERROR);
	// ============ service config ============
	$MEMCACHE_PORT = 11211;

	$MEMCACHE_IP_OR_HOSTNAME = 'localhost';

	$MEMCACHE_DIR = './cache/memcached-';

	$MEMCACHE_KEYS = 'KEYS';
	// ============ service default value ============

	$DEFUALT_IMAGEID = -1;
	$DEFUALT_IMAGE_LEVEL = 0;
	$DEFUALT_IMAGE_OFFSET = 0;
	$DEFUALT_IMAGE_COUNT = 100;

	// ============ service get method ============

	// 赞
	$GET_FAVORITE = 1;

	// 鄙视
	$GET_HATE = 2;

	// 实时排行
	$GET_RANK_REAL_TIME = 3;

	// 周排行
	$GET_WEEK_RANK = 4;

	// 月排行
	$GET_MONTH_RANK = 5;

	// 服务器请求方法列表
	$METHOD_LIST = array($GET_FAVORITE, $GET_HATE);

	// 服务器请求方法列表
	$METHOD_RANK_LIST = array($GET_RANK_REAL_TIME, $GET_WEEK_RANK, $GET_MONTH_RANK);

	// ============ service error code ============
	$SUCCESSED = 0;

	$FAILED_CANT_GET_ALL = 1;

	$FAILED_CANT_CONNTECTED_MEM = 2;

	$FAILED_SAVED_MEM = 3;

	$FAILED_NOT_SUCH_METHOD = 4;

	$FAILED_NO_IMGID = 5;

	function cmpUp($a, $b){
	    if ($a["favorited"] == $b["favorited"]) {
		    return 0;
	    }
	    return ($a["favorited"] > $b["favorited"]) ? -1 : 1;
    }

?>