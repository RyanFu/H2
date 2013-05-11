<?php
# type=[0,1,2, bd, bw, bm] id=n s=[0,1] level=n
require("common.php");
$con = connect_db();
$mem = connect_memcache();

# validate parameter
if(!$con || !$mem){
    mdie('no con or no mem cache');
}
if(isset($rtype) == FALSE || is_valid_type($rtype) == FALSE 
    || isset($rid) == FALSE || is_numeric($rid)==FALSE 
    || isset($rs) == FALSE || is_numeric($rs)==FALSE 
    || isset($rlevel) == FALSE || is_numeric($rlevel)==FALSE ){    
    mdie('invalid parameters');
}



# insert into db
$ip = $_SERVER["REMOTE_ADDR"];
$pids = strval($rlevel).'-'.strval($rid);
$image_key = $rtype.'-' . $pids;
insert_new_vote($rid, $rlevel, $ip, ($rs==0?1:-1), $con);


# make sure memcache has this entry
$info = $mem->get($image_key);
if($info == FALSE || $info['exp'] < time() || $NOCACHE == TRUE){
    # load, then store value into memcache
    $interval = type_to_interval($rtype);
    $info = load_vote_count_by_id_level($rid, $rlevel, $interval, $con);
    $info['id'] = $rid;
    $info['level']=$rlevel;
    $cache_expire=cache_expire_interval($rtype);
    $expire_time=cache_expire_interval($rtype)+time();                    
    $mem->set($image_key,array('data'=>$info, 'exp'=>$expire_time));
    #error_log("storing image $pids of $rtype, with expire time $expire_time");    
}else{
    # update memcache
    $expire_time = $info['exp'];
    $info = $info['data'];
    if($rs=='0'){
        $info['up']=$info['up']+1;
    }else{
        $info['down']=$info['down']-1;
    }
    $mem->set($image_key,array('data'=>$info, 'exp'=>$expire_time));
    #error_log("updating cache for image $pids of $rtype, with expire time $expire_time");
}


mysqli_close($con);

# output
?>
{
  'responseCode':0,
  'data':<?= json_encode($info) ?>  
}