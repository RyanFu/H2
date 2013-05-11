<?php
	require("include/CacheManager.php");
	require('config.php');

	$cacheManager = CacheManger::getInstance();

	if($cacheManager->connectMEM($MEMCACHE_IP_OR_HOSTNAME, $MEMCACHE_PORT, $MEMCACHE_DIR)) {
		$allKey = $MEMCACHE_KEYS . '-' . $GET_FAVORITE;
		$cacheManager->flushToFile($allKey);
		$allKey = $MEMCACHE_KEYS . '-' . $GET_HATE;
		$cacheManager->flushToFile($allKey);
		$result['code'] = $SUCCESSED;
		die(json_encode($result));
	} else {
		$result['code'] = $FAILED_CANT_CONNTECTED_MEM;
		die(json_encode($result));
	}
?>