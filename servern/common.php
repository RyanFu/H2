<?php

$DB = "girls";
$TABLE = "vote";
$NOCACHE = FALSE;

$INTERVAL_HOUR= 60*60;
$INTERVAL_DAY= $INTERVAL_HOUR*24;
$INTERVAL_WEEK = $INTERVAL_DAY*7;
$INTERVAL_MONTH = $INTERVAL_DAY*30;
$INTERVAL_ALL = -1;
$INTERVAL_MAPPING = array("bd"=>$INTERVAL_DAY,"bw"=>$INTERVAL_WEEK,"bm"=>$INTERVAL_MONTH);
$TYPE_LIST = array('0'=>$INTERVAL_DAY,'1'=>$INTERVAL_DAY,'2'=>$INTERVAL_DAY,'bd'=>$INTERVAL_HOUR,'bw'=>$INTERVAL_DAY,'bm'=>$INTERVAL_WEEK);


import_request_variables("g", "r");

if(isset($rnocache)==TRUE){
    $NOCACHE = $NOCACHE || $rnocache; 
}

function connect_db(){
    global $DB;    
    $con=mysqli_connect("localhost","root","feiyang123",$DB);
    if (mysqli_connect_errno($con))  {
        die("Failed to connect to MySQL: " . mysqli_connect_error());
    }
    return $con;
}

function connect_memcache(){
    $mem = new Memcached();
    $mem->addServer('localhost', 11211);
    return $mem;
}

function insert_new_vote($pid, $level, $ip, $score, $con){
    global $TABLE;    
    $pids = strval($level).'-'.strval($pid);
    $sql = "insert into $TABLE (pid, level, pids, ip, score) VALUES($pid, $level, '$pids', '$ip', $score)";
    #echo $sql;
    if (mysqli_query($con, $sql) == FALSE){
        mdie('Error: ' . mysqli_error($con));
    }    
};

/** 
 * $interval is how long before now, do you want the count. in ms. -1 means load all time cache
 * this is load single page's info 
 */
function load_vote_count_by_id_level($pid, $level, $interval, $con){
    $pids = strval($level).'-'.strval($pid);        
    return load_vote_count_by_pids($pids, $interval, $con);        
}

function load_vote_count_by_pids($pids,$interval,$con){
   if($interval==-1){
        $sql = "select sum(score) as score from vote where pids='$pids' group by score order by score desc";
    }else{
        $time = time() - $interval;
        #$time = $time * 1000;
        $sql = "select sum(score) as score from vote where pids='$pids' and time>FROM_UNIXTIME($time) group by score order by score desc";
    }
    
    #echo $sql;
    $result = mysqli_query($con, $sql);
    if($result == FALSE){
        return FALSE;
    }
    $row1 = mysqli_fetch_array($result);
    if($row1 == NULL){
        $row1 = 0;
        $row2 = 0;
    }else if($row1['score']<0){
        $row2 = $row1['score'];
        $row1 = 0;
    }else{
       $row1 = $row1['score'];
       $row2 = mysqli_fetch_array($result);        
       if($row2 == NULL  || $row2['score'] == NULL){
           $row2 = 0;
       }else{
           $row2 = $row2['score'];
       }
    }
    $r = array();
    $r['up'] = intval($row1);
    $r['down'] = intval($row2);    
    #echo json_encode($r);
    return $r;
}

/**
 * this is loading a list only, return a list of pids ['1-100', '2-100']
 */
function load_vote_list($interval,$count,$con){
    if($interval==-1){
        $sql = "select sum(score) as score, pids from vote where score>0 group by pid order by score desc limit $count";
    }else{
        $time = time() - $interval;
        #$time = $time * 1000;        
        $sql = "select sum(score) as score, pids from vote where score>0 and time>FROM_UNIXTIME($time) group by pids order by score desc,pids desc limit $count";
    }
    #echo $sql;
    $result = mysqli_query($con, $sql);
    if($result == FALSE){
        return FALSE;
    }
    $r = array();
    while($row = mysqli_fetch_array($result)){
        $r[] = $row['pids'];
    }
    return $r;
}



function type_to_interval($type){
    global $INTERVAL_MAPPING;    
    if(is_numeric($type)==TRUE){
        return -1;
    }else if(array_key_exists($type, $INTERVAL_MAPPING)==TRUE){
        return $INTERVAL_MAPPING[$type];
    }else{
        return $INTERVAL_DAY;
    }
}
function mdie($msg){
    error_log($msg);
    die();
}

function is_valid_type($type){
    global $TYPE_LIST;
    return array_key_exists($type, $TYPE_LIST);  
}

/* 
 * How soon a list or image of certain type will expire, based on the type.
 * Daily list/image expires every hour
 * Weekly every day
 * Monthly every week
 * Also, there will be a less than 10 mintues random offset, to make sure every one expires on different times.
 * return value of -1 means this type should never expire
 */
function cache_expire_interval($type){
    global $TYPE_LIST;
    #return 10;
    if(is_valid_type($type)==FALSE){
        mdie('invalid type ' . $type);
    }
    $value = $TYPE_LIST[$type];    
    if($value == -1){
        return -1;
    }else{
        $value = $value + abs(rand()) % 600 ;
        return $value;
    } 
}
?>