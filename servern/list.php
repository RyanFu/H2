<?php
#list.php?from=n&count=n&type=[0,1,2, bd, bw, bm]
require("common.php");
$con = connect_db();
$mem = connect_memcache();

# validate parameter
if(!$con || !$mem){
    mdie('no con or no mem cache');
}
if(isset($rtype) == FALSE || is_valid_type($rtype) == FALSE){    
    mdie('invalid type');
}
if(is_numeric($rtype)==TRUE){
    if(isset($rcount) == FALSE || isset($rfrom) == FALSE 
        || is_numeric($rcount)==FALSE || is_numeric($rfrom)==FALSE || $rcount>50){
        mdie('no count or from for numeric type, or count from is not numbers, or invalid count value');
    }    
}else{
    $rcount = 100;    
} 


#generate list
$interval = type_to_interval($rtype);
if(is_numeric($rtype)==TRUE){
    $list = array();
    for($i=0;$i<$rcount;$i++){
        $index = $rfrom+$i;
        if($index>0){
            $list[] = $rtype.'-'.strval($index);
        }
    }
}else{
    #get image list from memcache
    $list = $mem->get($rtype);
    if($list == FALSE || $list['exp']<time() ||$NOCACHE == TRUE){
        #error_log('miss cache for list ' . $rtype);
        $list = load_vote_list($interval, $rcount, $con);
        $expire_time=cache_expire_interval($rtype)+time();                
        $mem->set($rtype, array('data'=>$list,'exp'=>$expire_time));
        #error_log("storing list of $rtype, with expire time $expire_time");
    }else{
        $list = $list['data'];
        #error_log('hit cache for list ' . $rtype);
    }
}
#echo json_encode($list);


# loop through list to get info for every image
$length = count($list);
$r = array();
for($i=0; $i<$length; $i++){
    $pids = $list[$i];
    $image_key = $rtype.'-'.$pids;
    $info = $mem->get($image_key);
    if($info == FALSE || $info['exp']<time() || $NOCACHE == TRUE){
        #error_log('miss cache for image'.$image_key);
        $info = load_vote_count_by_pids($pids, $interval, $con);
        $ids = explode('-',$pids);
        $info['level']=$ids[0];
        $info['id']=$ids[1];                
        $expire_time=cache_expire_interval($rtype)+time();                
        $mem->set($image_key, array('data'=>$info,'exp'=>$expire_time));
        #error_log("storing image $pids of $rtype, with expire time $expire_time");
    }else{
        $info = $info['data'];
        #error_log('hit cache for image '.$image_key);
    }
    $r[]=$info;
}
mysqli_close($con);
?>
{
  responseCode:0,
  from:<?= is_numeric($rtype)==TRUE?$rfrom:'0' ?>,
  count:<?= count($r) ?>,
  type:'<?= $rtype ?>',
  data: <?= json_encode($r) ?>
}
