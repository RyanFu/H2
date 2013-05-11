<?php
	require("include/CacheManager.php");
	require('config.php');
	
	$result = array('code' => $FAILED_NOT_SUCH_METHOD);
	$size = count($_REQUEST); 
	if($size == 0) {
		$result['code'] = $FAILED_NOT_SUCH_METHOD;
		die(json_encode($result));
	} else {
		$keys = array_keys($_REQUEST);
		$imgId = in_array('imgId', $keys) ? $_REQUEST['imgId'] : $DEFUALT_IMAGEID;
		$level = in_array('level', $keys) ? $_REQUEST['level'] : $DEFUALT_IMAGE_LEVEL;
		$method = in_array('method', $keys) ? $_REQUEST['method'] : $GET_FAVORITE;
		if(in_array($method, $METHOD_LIST) && ($imgId == $DEFUALT_IMAGEID)) {
			$result['code'] = $FAILED_NO_IMGID;
			die(json_encode($result));
		} else if(!in_array($method, $METHOD_LIST) && !in_array($method, $METHOD_RANK_LIST)) {
			$result['code'] = $FAILED_NOT_SUCH_METHOD;
			die(json_encode($result));
		} else {
			$cacheManager = CacheManger::getInstance();
			if($cacheManager->connectMEM($MEMCACHE_IP_OR_HOSTNAME, $MEMCACHE_PORT, $MEMCACHE_DIR)) {
				if(in_array($method, $METHOD_LIST)) {
					$key = $level . '-' . $method . '-' . $imgId;
					$get_obj = $cacheManager->fetch($key);
					if($get_obj) {
						$keys = array_keys($get_obj);
						$get_obj['imgId'] = $imgId;
						$get_obj['level'] = $level;
						$get_obj['favorited'] = (in_array('favorited', $keys) ? $get_obj['favorited'] : 0) + 1;
					} else {
						$get_obj['imgId'] = $imgId;
						$get_obj['level'] = $level;
						$get_obj['favorited'] = 1;
					}
					$tryCount = 0;
					while(!$cacheManager->storeForever($key, $get_obj)){
						$tryCount ++;
						if($tryCount == 3) {
							$result['code'] = $FAILED_CANT_CONNTECTED_MEM;
							die(json_encode($result));
						}
					}
					$get_result = $cacheManager->fetch($key);
					$result['code'] = $SUCCESSED;
					$result['data'] = $get_result;
					echo json_encode($result);
					$allKey = $MEMCACHE_KEYS . '-' . $method;
					$keys = $cacheManager->fetch($allKey);
					$changed = false;
					if(!$keys) {
						$keys = array($key);
						$changed = true;
					} else if(!in_array($key, $keys)) {
						array_push($keys, $key);
						$changed = true;
					}
					if($changed){
						$cacheManager->storeForever($allKey, $keys);
						if($method == $GET_FAVORITE) {
							$rankKey = 'Rank' . $method . '-' . $GET_FAVORITE;
							$rank = array();
							$rank['time'] = time();
							$rank['keys'] = $keys;
							$cacheManager->storeForever($rankKey, $rank);
						}
					}
					exit(0);
				} else if(in_array($method, $METHOD_RANK_LIST)) {
					$key = 'Rank' . $method . '-' . $GET_FAVORITE;
					$allKey = $MEMCACHE_KEYS . '-' . $GET_FAVORITE;
					$keys = $cacheManager->fetch($allKey);
					$values = $cacheManager->fetchMulti($keys);
					uasort($values, "cmpUp");
					$keys = array_keys($values);
					$index = 0;
					$res_arr = array();
					foreach($keys as $ckey) {
						if($index < 100){
							unset($values[$ckey]['favorited']); // 去除其中的 favorite 属性
							array_push($res_arr, $values[$ckey]);
						} else {
							break;
						}
						$index ++;
					}
					$rank = array();
					$rank['time'] = time();
					$rank['rankId'] = $res_arr;
					$cacheManager->storeForeverWithFile($key, $rank);
					$result['code'] = $SUCCESSED;
					$result['data'] = $rank['rankId'];
					die(json_encode($result));
				} else {
					$result['code'] = $FAILED_NOT_SUCH_METHOD;
					die(json_encode($result));
				}
			}else {
				$result['code'] = $FAILED_CANT_CONNTECTED_MEM;
				die(json_encode($result));
			}
		}
	}

?>