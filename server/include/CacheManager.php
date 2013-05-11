<?php
	include("Abstract.php");
	include("Filesystem.php");

	/**
	 * ����ʽ������
	 */
	class CacheManger {
	 
		protected $defaultTTL = 600;
		/**
		 * ��̬��Ʒ���� ����ȫ��ʵ��
		 */
		private static  $_instance = NULL;
	 
		private $memcache = NULL;

		private $file_mem_cache = NULL;
		/**
		 * ˽�л�Ĭ�Ϲ��췽������֤����޷�ֱ��ʵ����
		 */
		private function __construct() {
			$this->memcache = new Memcache;
		}
	 
		/**
		 * ��̬�������������������Ψһʵ��
		 */
		public static function getInstance() {
			if (is_null(self::$_instance)) {
				self::$_instance = new CacheManger();
			}
	 
			return self::$_instance;
		}
	 
		/**
		 * ��ֹ�û���¡ʵ��
		 */
		public function __clone(){
			die('Clone is not allowed.' . E_USER_ERROR);
		}
	 
		public function connectMEM($host_name, $port, $mem_dir){
			$this->file_mem_cache = new Sabre_Cache_Filesystem($mem_dir);
			return $this->memcache->connect($host_name, $port);
		}

		/**
		 * ��ȡ��������
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
		 * �ڴ滺��
		 */
		public function store($key, $data, $ttl=null) {
			$result = $this->memcache->set($key, $data, false, is_null($ttl) ? $this->defaultTTL : $ttl);
			// $this->file_mem_cache->store($key, $data, $ttl);  // -1 ��ʾ��������
			return $result;
		}
	 
		/**
		 * �ڴ�־û���
		 */
		public function storeForever($key, $data) {
			$result = $this->memcache->set($key, $data, false, 0);
			//$this->file_mem_cache->store($key, $data, -1);  // -1 ��ʾ��������
			return $result;
		}

		/**
		 * �ڴ�־û���
		 */
		public function storeForeverWithFile($key, $data) {
			$result = $this->memcache->set($key, $data, false, 0);
			$this->file_mem_cache->store($key, $data, -1);  // -1 ��ʾ��������
			return $result;
		}

		/**
		* ͬʱ��ȡ�����������
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
				$this->file_mem_cache->store($allKey, $keys, -1);  // �����б�
				foreach($keys as $key) {
					$this->file_mem_cache->store($key, $this->fetch($key), -1);  // ��һ������
				}
			}
		}

	}
	
?>