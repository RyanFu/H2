<?php
	include("Abstract.php");
	include("Filesystem.php");

	/**
	 * 懒汉式单例类
	 */
	class CacheManger {
	 
		protected $defaultTTL = 600;
		/**
		 * 静态成品变量 保存全局实例
		 */
		private static  $_instance = NULL;
	 
		private $memcache = NULL;

		private $file_mem_cache = NULL;
		/**
		 * 私有化默认构造方法，保证外界无法直接实例化
		 */
		private function __construct() {
			$this->memcache = new Memcache;
		}
	 
		/**
		 * 静态工厂方法，返还此类的唯一实例
		 */
		public static function getInstance() {
			if (is_null(self::$_instance)) {
				self::$_instance = new CacheManger();
			}
	 
			return self::$_instance;
		}
	 
		/**
		 * 防止用户克隆实例
		 */
		public function __clone(){
			die('Clone is not allowed.' . E_USER_ERROR);
		}
	 
		public function connectMEM($host_name, $port, $mem_dir){
			$this->file_mem_cache = new Sabre_Cache_Filesystem($mem_dir);
			return $this->memcache->connect($host_name, $port);
		}

		/**
		 * 获取缓存内容
		 */
		public function fetch($key) {
			$result = $this->memcache->get($key);
			if($result == false) {
				$result = $this->file_mem_cache->fetch($key);
				if(is_null($result)) {
					$result = false;
				}
			}
			return $result;
		}
	 
		/**
		 * 内存缓存
		 */
		public function store($key, $data, $ttl=null) {
			$result = $this->memcache->set($key, $data, false, is_null($ttl) ? $this->defaultTTL : $ttl);
			// $this->file_mem_cache->store($key, $data, $ttl);  // -1 表示永不销毁
			return $result;
		}
	 
		/**
		 * 内存持久缓存
		 */
		public function storeForever($key, $data) {
			$result = $this->memcache->set($key, $data, false, 0);
			//$this->file_mem_cache->store($key, $data, -1);  // -1 表示永不销毁
			return $result;
		}

		/**
		 * 内存持久缓存
		 */
		public function storeForeverWithFile($key, $data) {
			$result = $this->memcache->set($key, $data, false, 0);
			$this->file_mem_cache->store($key, $data, -1);  // -1 表示永不销毁
			return $result;
		}

		/**
		* 同时获取多个缓存内容
		*/
	    public function fetchMulti(array $keys) {
			$data=array();
			foreach($keys as $key) {
				$data[$key] = $this->fetch($key);
			}
			return $data;
		}

		public function flushToFile($allKey) {
			$keys = $this->fetch($allKey);
			if($keys) {
				$this->file_mem_cache->store($allKey, $keys, -1);  // 保存列表
				foreach($keys as $key) {
					$this->file_mem_cache->store($key, $this->fetch($key), -1);  // 单一的数据
				}
			}
		}

	}
	
?>