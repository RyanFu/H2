<?php
	require("include/CacheManager.php");
	require('config.php');

	$size = count($_REQUEST);
	if($size == 0) {
		$count = $DEFUALT_IMAGE_COUNT;
		$from = $DEFUALT_IMAGE_OFFSET;
		$imgId = $DEFUALT_IMAGEID;
		$level = $DEFUALT_IMAGE_LEVEL;
		$method = $GET_FAVORITE;
	} else {
		$keys = array_keys($_REQUEST);
		$count = in_array('count', $keys) ? $_REQUEST['count'] : $DEFUALT_IMAGE_COUNT;
		$from = in_array('offset', $keys) ? $_REQUEST['offset'] : $DEFUALT_IMAGE_OFFSET;
		$imgId = in_array('imgId', $keys) ? $_REQUEST['imgId'] : $DEFUALT_IMAGEID;
		$level = in_array('level', $keys) ? $_REQUEST['level'] : $DEFUALT_IMAGE_LEVEL;
		$method = in_array('method', $keys) ? $_REQUEST['method'] : $GET_FAVORITE;
	}
	$cacheManager = CacheManger::getInstance();
	$result = array('code' => $FAILED_NOT_SUCH_METHOD);
	if($cacheManager->connectMEM($MEMCACHE_IP_OR_HOSTNAME, $MEMCACHE_PORT, $MEMCACHE_DIR)) {
		if($imgId == -1) {
			if(in_array($method, $METHOD_LIST) || ($method == $GET_RANK_REAL_TIME)) {
				if($method == $GET_RANK_REAL_TIME) {
					$method = $GET_FAVORITE;
				}
				$allKey = $MEMCACHE_KEYS . '-' . $method;
				$keys = $cacheManager->fetch($allKey);
				if($keys) {
					$values = $cacheManager->fetchMulti($keys);
					uasort($values, "cmpUp");
					$keys = array_keys($values);
					$index = 0;
					$res_arr = array();
					foreach($keys as $key) {
						if($index >= $from && (($index - $from) < $count)){
							unset($values[$key]['favorited']); // 去除其中的 favorite属性
							array_push($res_arr, $values[$key]);
						}
						$index ++;
					}
					$result['code'] = $SUCCESSED;
					$result['data'] = $res_arr;
					die(json_encode($result));
				} else {
					$result['code'] = $SUCCESSED;
					die(json_encode($result));
				}
			} else if(in_array($method, $METHOD_RANK_LIST)) {
				$key = 'Rank' . $method . '-' . $GET_FAVORITE;
				$get_result = $cacheManager->fetch($key);
				$result['code'] = $SUCCESSED;
				if($get_result) {
					$result['data'] = $get_result['rankId'];
				}
				die(json_encode($result));
			}else {
				$result = array('code' => $FAILED_NOT_SUCH_METHOD);
				die(json_encode($result));
			}
		} else {
			$imgInfo['imgId'] = $imgId;
            $imgInfo['level'] = $level;
            $method = $GET_FAVORITE;
            $key = $level . '-' . $method . '-' . $imgId;
            $get_result = $cacheManager->fetch($key);
            $result['code'] = $SUCCESSED;
            if($get_result != false) {
                 $imgInfo['up'] = $get_result['favorited'];
            }
            $method = $GET_HATE;
            $key = $level . '-' . $method . '-' . $imgId;
            $get_result = $cacheManager->fetch($key);
            $result['code'] = $SUCCESSED;
            if($get_result != false) {
                $imgInfo['down'] = $get_result['favorited'];
            }
            $result['data'] = $imgInfo;
			die(json_encode($result));
		}
	} else {
		$result['code'] = $FAILED_CANT_CONNTECTED_MEM;
		die(json_encode($result));
	}

?>