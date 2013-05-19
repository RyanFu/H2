#! /usr/bin/env python
# coding=utf-8

import json
import sys, os, fnmatch
import urllib, urllib2
from urllib import urlretrieve
from bs4 import BeautifulSoup as bs
from warnings import catch_warnings
import time
import base64
import re

url_format = "http://www.mm-girl.com/girl-star{0}{1}.html"
url_pic_format = "http://www.mm-girl.com/photo-{0}-{1}.html"
url_dangan_format = "http://www.mm-girl.com/{0}-dangan-{1}.html"
reload(sys)
sys.setdefaultencoding("utf-8")

head_html = '<html xmlns="http://www.w3.org/1999/xhtml">'
body_html = '<body>'
end_html = "</body><html>"

def filter_tags(htmlstr):
	"""
	过滤html中注释信息，以及 a 标签
	"""
	re_cdata = re.compile('//<!\[CDATA\[[^>]*//\]\]>', re.I)  # 匹配CDATA
	re_comment = re.compile('<!--[^>]*-->')  # HTML注释
	re_a = re.compile('</?\s*a[^>]*>')  # HTML a标签
	re_script = re.compile('</?\s*script[^>]*>')  # HTML script标签
	re_link = re.compile('</?\s*link[^>]*>')  # HTML script标签
	blank_line = re.compile('\n+')  # 去掉多余的空行
	result = re_cdata.sub('', htmlstr)  # 去掉CDATA
	result = re_a.sub('', result)  # HTML a标签
	result = re_script.sub('', result)  # HTML a标签
	result = re_link.sub('', result)  # HTML a标签
	result = re_comment.sub('', result)  # 去掉HTML注释
	result = blank_line.sub('', result)
	return result

def filter_space(str):
	"""
	删除空格等空白,并且删除括号中的内容
	"""
	re_comment = re.compile('（.*）')
	re_comment1 = re.compile('[(].*[)]')
	result = str.replace("　", "")
	result = result.replace(" ", "")
	result = result.replace("，", ",")
 	result = result.replace("（", "(")
 	result = result.replace("）", ")")
	result = re_comment.sub('', result)
	result = re_comment1.sub('', result)
	return result

def get_mm_girl_img(folder, girl):
	"""
	从连接中获取当前模特信息,并保存文件
	"""
	urls = []
	urls.append(girl['pic'])
	get_mm_girl_img_urls(folder, urls, girl['pic'])

def get_mm_girl_img_urls(folder, urls, url):
	"""
	从url中获取图片，并解析网页中的剩余图片
	"""
	page = urllib.urlopen(url)
	soup = bs(page.read())
	for div in soup.findAll('div', {"class" : "mypic"}):
		for li in div.findAll('li'):
			get_mm_girl_img_file(folder, li.a['href'])
			time.sleep(0.1)

def get_mm_girl_img_file(folder, url):
	page = urllib.urlopen(url)
	try:
		soup = bs(page.read())
	except:
		return
	else:
		for dimg in soup.findAll('div', {"class" : "mypic"}):
			img = dimg.img['src']
	        filename = img.split("/")[-1]
	        if fnmatch.fnmatch(filename, "*.jpg") or fnmatch.fnmatch(filename, "*.jpeg"):
	            outpath = os.path.join(folder, filename)
	            if not os.path.exists(outpath):
	            	try:
	                	urlretrieve(img, outpath)
	                except:
	                	time.sleep(0.1)
	                else:
	                	time.sleep(0.1)

def get_mm_girl_content1(folder, girl):
	"""
	从连接中获取当前模特信息,并保存文件
	"""
	page = urllib.urlopen(girl['dangan'])
	soup = bs(page.read())
	soup.head.link.extract()
	soup.head.script.extract()
	print soup.head
	
def get_img_base64(url):
	try:
		urlinfo = urllib2.urlopen(url, None, 3)
		return base64.b64encode(urlinfo.read())
	except:
		return None
	
def get_mm_girl_content(folder, girl):
	"""
	url = "http://www.mm-girl.com/zhangzhixi-dangan-32462.html"
	从连接中获取当前模特信息,并保存文件
	"""
	page = urllib.urlopen(girl['dangan'])
	print girl['dangan']
	try:
		soup = bs(page.read(), from_encoding="gb18030")  # 解决中文乱码问题
	except:
		return
	else:
		for mypic in soup.findAll('div', {"class" : "mypic"}):
			girl['name-ch'] = mypic.h3.get_text()[0:-4] 	# 明星名字
			girl['name-des'] = filter_space(filter_tags(mypic.get_text())) # 明星简介
			for p in mypic.findAll('p'):
				textp = p.get_text()
				if len(textp) >= 20:
					girl['name-des'] = textp
					break
			for rdiv in mypic.findAll('div', {"class" : "right"}):
				rdiv.extract()
			for script in mypic.findAll('script'):
				script.extract()
			for img in mypic.findAll('img'):
				uri = img['src']
				for i in range(0, 3 , 1):
					uri = get_img_base64(uri)
					if uri != None:
						break
				if uri != None:
					img['src'] = "data:image/png;base64,{0}".format(uri)
			#outpath = os.path.join(folder, "{0}.html".format(girl['name']))
			outpath = os.path.join(folder, "i.html")
			fout = open(outpath, 'w')
			mhead = soup.head
			for link in mhead.findAll('link'):
				link.extract()
			for script in mhead.findAll('script'):
				script.extract()
			print >> fout , head_html, filter_tags(mhead.prettify(formatter="html")), body_html, filter_tags(mypic.prettify(formatter="html")), end_html
			fout.close()
	time.sleep(0.1)
	
def get_mm_girl_model_links(folder, index, url):
	"""
	url = "http://www.mm-girl.com/girl-star.html"
	从连接中获取所有模特信息,比如名称, id, 档案连接, 图片连接等
	并返回该列表
	"""
	page = urllib.urlopen(url)
	print url
	try:
		soup = bs(page.read())
	except:
		return
	else:
	 	girls = []
	 	for mypic in soup.findAll('div', {"class" : "mypic"}):
			for ul in mypic.findAll('ul'):
				ss = ul.a['href'][0:-5].split('-')
				girl = {}
				girl['index'] = index
				girl['name'] = ss[1]
				girl['id'] = ss[2]
				girl['pic'] = url_pic_format.format(girl['name'], girl['id'])
				girl['dangan'] = url_dangan_format.format(girl['name'], girl['id'])
				#outpath = os.path.join(folder, girl['id'])
				outpath = os.path.join(folder, "{0}".format(girl['index']))
				if not os.path.exists(outpath):
					os.makedirs(outpath, 0777)
				get_mm_girl_content(outpath, girl)
				time.sleep(0.1)
				#get_mm_girl_img(outpath, girl)
				#time.sleep(0.1)
				index += 1
				girls.append(girl)
	return girls
    
def get_mm_girl_links(folder):
	"""
	生成连接信息
	"""
	girls = []
	cindex = 1
	#for index in range(1, 93, 1):
	for index in range(1, 3, 1):
		ch = '-'
		if index == 92:
			ch = ''
			url = url_format.format(ch, '')
		else:
			url = url_format.format(ch, index)
		girls.extend(get_mm_girl_model_links(folder, cindex, url))
		cindex = len(girls) + 1
	if not os.path.exists(folder):
		os.makedirs(folder, 0777)
	outpath = os.path.join(folder, "info-debug")
	fout = open(outpath, 'w')
	fout.writelines(['%s@@@%s@@@%s@@@%s\n' % (girl['name'], girl['id'], girl['pic'], girl['dangan']) for girl in girls])
	fout.close()
	outpath = os.path.join(folder, "info")
	fout = open(outpath, 'w')
	fout.writelines(['%s@@@%s@@@%s\n' % (girl['index'], girl['name-ch'], girl['name-des']) for girl in girls])
	fout.close()

if __name__ == '__main__':
# 	get_mm_girl_img_file('.', "http://www.mm-girl.com/photo-zhangzhixi-v479197.html")
  	get_mm_girl_links('mm_girl')
	# http://www.mm-girl.com/zhangzhixi-dangan-32462.html
#  	girl = {}
#  	girl['name'] = 'zhangzhixi'
#  	girl['id'] = 32462
#  	girl['pic'] = url_pic_format.format(girl['name'], girl['id'])
#  	girl['dangan'] = url_dangan_format.format(girl['name'], girl['id'])
#  	get_mm_girl_content('.', girl)
#  	get_mm_girl_img('.', girl)
#  	get_mm_girl_content1('.', girl)
