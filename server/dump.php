<?php
        require("include/CacheManager.php");
        require('config.php');

function generate_sql($data,$level,$image_id,$score){
    $count = $data['favorited'];
    if($count==0) return;
    $pid = $image_id;
    $pids=$level.'-'.$pid;
    $ip='x';
    for($i=0;$i<$count;$i++){
        echo "($pid, $level, '$pids', '$ip', $score), ";
    }
    echo PHP_EOL;
}
    $cacheManager = CacheManger::getInstance();
    if($cacheManager->connectMEM($MEMCACHE_IP_OR_HOSTNAME, $MEMCACHE_PORT, $MEMCACHE_DIR)) {
        $max_id=13530;
        for($level=0;$level<2;$level++){
            for($imgId=0;$imgId<$max_id;$imgId++){
                for($method=1;$method<3;$method++){
                    #$imgId=13529;
                    $key = $level . '-' . $method . '-' . $imgId;
                    $get_result = $cacheManager->fetch($key);
                    generate_sql($get_result, $level, $imgId, $method==1?1:-1);
                    #echo json_encode($get_result);
                }
            }
        }

    }
?>