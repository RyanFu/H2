#! /usr/bin/env python
# coding=utf-8

import json
import sys, os
import urllib, urllib2
from bs4 import BeautifulSoup as bs
from warnings import catch_warnings
import re

url_format = "http://baike.baidu.com/fenlei/%E5%A5%B3%E4%BC%98?limit=30&index={0}&offset={1}#gotoList"
div = 30
max = 6
reload(sys)
sys.setdefaultencoding("utf-8")

linklist = ['http://baike.baidu.com/view/667852.htm',
'http://baike.baidu.com/view/25276.htm',
'http://baike.baidu.com/view/293208.htm',
'http://baike.baidu.com/view/53288.htm',
'http://baike.baidu.com/view/21620.htm',
'http://baike.baidu.com/view/2132269.htm',
'http://baike.baidu.com/view/2497107.htm',
'http://baike.baidu.com/view/1372114.htm',
'http://baike.baidu.com/view/2776793.htm',
'http://baike.baidu.com/view/44942.htm',
'http://baike.baidu.com/view/4056607.htm',
'http://baike.baidu.com/view/1874345.htm',
'http://baike.baidu.com/view/97968.htm',
'http://baike.baidu.com/view/548346.htm',
'http://baike.baidu.com/view/6201658.htm',
'http://baike.baidu.com/view/120365.htm',
'http://baike.baidu.com/view/3715630.htm',
'http://baike.baidu.com/view/4267184.htm',
'http://baike.baidu.com/view/2002858.htm',
'http://baike.baidu.com/view/1003513.htm',
'http://baike.baidu.com/view/1043523.htm',
'http://baike.baidu.com/view/2881721.htm',
'http://baike.baidu.com/view/3415063.htm',
'http://baike.baidu.com/view/1863071.htm',
'http://baike.baidu.com/view/1751582.htm',
'http://baike.baidu.com/view/4003065.htm',
'http://baike.baidu.com/view/2195554.htm',
'http://baike.baidu.com/view/617921.htm',
'http://baike.baidu.com/view/3588933.htm',
'http://baike.baidu.com/view/1818025.htm',
'http://baike.baidu.com/view/1980944.htm',
'http://baike.baidu.com/view/2708552.htm',
'http://baike.baidu.com/view/6271061.htm',
'http://baike.baidu.com/view/1021963.htm',
'http://baike.baidu.com/view/2856954.htm',
'http://baike.baidu.com/view/1589815.htm',
'http://baike.baidu.com/view/3287965.htm',
'http://baike.baidu.com/view/4151366.htm',
'http://baike.baidu.com/view/1333603.htm',
'http://baike.baidu.com/view/3959105.htm',
'http://baike.baidu.com/view/1019908.htm',
'http://baike.baidu.com/view/723907.htm',
'http://baike.baidu.com/view/5903474.htm',
'http://baike.baidu.com/view/3014767.htm',
'http://baike.baidu.com/view/1588171.htm',
'http://baike.baidu.com/view/1000222.htm',
'http://baike.baidu.com/view/126007.htm',
'http://baike.baidu.com/view/1238087.htm',
'http://baike.baidu.com/view/5669480.htm',
'http://baike.baidu.com/view/1772127.htm',
'http://baike.baidu.com/view/5127678.htm',
'http://baike.baidu.com/view/3859079.htm',
'http://baike.baidu.com/view/444248.htm',
'http://baike.baidu.com/view/2100972.htm',
'http://baike.baidu.com/view/2298927.htm',
'http://baike.baidu.com/view/4576404.htm',
'http://baike.baidu.com/view/6326873.htm',
'http://baike.baidu.com/view/4532552.htm',
'http://baike.baidu.com/view/222677.htm',
'http://baike.baidu.com/view/2539490.htm',
'http://baike.baidu.com/view/2947651.htm',
'http://baike.baidu.com/view/2581244.htm',
'http://baike.baidu.com/view/3330427.htm',
'http://baike.baidu.com/view/5289143.htm',
'http://baike.baidu.com/view/348955.htm',
'http://baike.baidu.com/view/2675570.htm',
'http://baike.baidu.com/view/1297262.htm',
'http://baike.baidu.com/view/2132654.htm',
'http://baike.baidu.com/view/3227782.htm',
'http://baike.baidu.com/view/262426.htm',
'http://baike.baidu.com/view/1145936.htm',
'http://baike.baidu.com/view/3393108.htm',
'http://baike.baidu.com/view/609739.htm',
'http://baike.baidu.com/view/3689204.htm',
'http://baike.baidu.com/view/237730.htm',
'http://baike.baidu.com/view/6214990.htm',
'http://baike.baidu.com/view/6375144.htm',
'http://baike.baidu.com/view/5862902.htm',
'http://baike.baidu.com/view/1518794.htm',
'http://baike.baidu.com/view/6334857.htm',
'http://baike.baidu.com/view/1014920.htm',
'http://baike.baidu.com/view/998593.htm',
'http://baike.baidu.com/view/2135365.htm',
'http://baike.baidu.com/view/6987686.htm',
'http://baike.baidu.com/view/3923098.htm',
'http://baike.baidu.com/view/3223912.htm',
'http://baike.baidu.com/view/3319930.htm',
'http://baike.baidu.com/view/562423.htm',
'http://baike.baidu.com/view/1174760.htm',
'http://baike.baidu.com/view/2632507.htm',
'http://baike.baidu.com/view/1839958.htm',
'http://baike.baidu.com/view/3229957.htm',
'http://baike.baidu.com/view/3295642.htm',
'http://baike.baidu.com/view/6657813.htm',
'http://baike.baidu.com/view/1140316.htm',
'http://baike.baidu.com/view/1496351.htm',
'http://baike.baidu.com/view/5394305.htm',
'http://baike.baidu.com/view/3113551.htm',
'http://baike.baidu.com/view/6295392.htm',
'http://baike.baidu.com/view/6250069.htm',
'http://baike.baidu.com/view/2729237.htm',
'http://baike.baidu.com/view/5580492.htm',
'http://baike.baidu.com/view/6290427.htm',
'http://baike.baidu.com/view/5127029.htm',
'http://baike.baidu.com/view/1280725.htm',
'http://baike.baidu.com/view/1702716.htm',
'http://baike.baidu.com/view/1001958.htm',
'http://baike.baidu.com/view/3176332.htm',
'http://baike.baidu.com/view/2057111.htm',
'http://baike.baidu.com/view/3203981.htm',
'http://baike.baidu.com/view/5930469.htm',
'http://baike.baidu.com/view/2118853.htm',
'http://baike.baidu.com/view/5894791.htm',
'http://baike.baidu.com/view/1477755.htm',
'http://baike.baidu.com/view/1043513.htm',
'http://baike.baidu.com/view/3623991.htm',
'http://baike.baidu.com/view/6175243.htm',
'http://baike.baidu.com/view/3230494.htm',
'http://baike.baidu.com/view/1483258.htm',
'http://baike.baidu.com/view/2197507.htm',
'http://baike.baidu.com/view/1800053.htm',
'http://baike.baidu.com/view/2074460.htm',
'http://baike.baidu.com/view/2177482.htm',
'http://baike.baidu.com/view/2074486.htm',
'http://baike.baidu.com/view/2766733.htm',
'http://baike.baidu.com/view/1588163.htm',
'http://baike.baidu.com/view/1642570.htm',
'http://baike.baidu.com/view/7099316.htm',
'http://baike.baidu.com/view/2905469.htm',
'http://baike.baidu.com/view/3259925.htm',
'http://baike.baidu.com/view/1740063.htm',
'http://baike.baidu.com/view/1699391.htm',
'http://baike.baidu.com/view/1490548.htm',
'http://baike.baidu.com/view/5697308.htm',
'http://baike.baidu.com/view/5930567.htm',
'http://baike.baidu.com/view/1060453.htm',
'http://baike.baidu.com/view/6291623.htm',
'http://baike.baidu.com/view/6319166.htm',
'http://baike.baidu.com/view/7223642.htm',
'http://baike.baidu.com/view/5335196.htm',
'http://baike.baidu.com/view/5156933.htm',
'http://baike.baidu.com/view/6278855.htm',
'http://baike.baidu.com/view/7671945.htm',
'http://baike.baidu.com/view/8681308.htm',
'http://baike.baidu.com/view/6308816.htm',
'http://baike.baidu.com/view/2662729.htm',
'http://baike.baidu.com/view/4890525.htm',
'http://baike.baidu.com/view/2730484.htm',
'http://baike.baidu.com/view/2352159.htm',
'http://baike.baidu.com/view/4292541.htm',
'http://baike.baidu.com/view/6270524.htm',
'http://baike.baidu.com/view/4782697.htm',
'http://baike.baidu.com/view/7875229.htm',
'http://baike.baidu.com/view/5388973.htm',
'http://baike.baidu.com/view/2755084.htm',
'http://baike.baidu.com/view/8336704.htm',
'http://baike.baidu.com/view/8153555.htm',
'http://baike.baidu.com/view/4584889.htm',
'http://baike.baidu.com/view/1079445.htm',
'http://baike.baidu.com/view/6622494.htm',
'http://baike.baidu.com/view/1661389.htm',
'http://baike.baidu.com/view/548227.htm',
'http://baike.baidu.com/view/5903542.htm',
'http://baike.baidu.com/view/807668.htm',
'http://baike.baidu.com/view/3034290.htm',
'http://baike.baidu.com/view/1024383.htm',
'http://baike.baidu.com/view/3454095.htm',
'http://baike.baidu.com/view/4277848.htm',
'http://baike.baidu.com/view/4971036.htm',
'http://baike.baidu.com/view/2941310.htm',
'http://baike.baidu.com/view/5126423.htm',
'http://baike.baidu.com/view/5126404.htm',
'http://baike.baidu.com/view/6791539.htm',
'http://baike.baidu.com/view/5126695.htm',
'http://baike.baidu.com/view/5799136.htm',
'http://baike.baidu.com/view/1153022.htm',
'http://baike.baidu.com/view/44222.htm',
'http://baike.baidu.com/view/5903732.htm',
'http://baike.baidu.com/view/5903516.htm',
'http://baike.baidu.com/view/2876898.htm',
'http://baike.baidu.com/view/447273.htm',
'http://baike.baidu.com/view/5683390.htm',
'http://baike.baidu.com/view/5127563.htm',
'http://baike.baidu.com/view/8278895.htm',
'http://baike.baidu.com/view/3230011.htm',
'http://baike.baidu.com/view/3186479.htm',
'http://baike.baidu.com/view/3571979.htm',
'http://baike.baidu.com/view/6474892.htm',
'http://baike.baidu.com/view/1297172.htm',
'http://baike.baidu.com/view/793880.htm',
'http://baike.baidu.com/view/1025798.htm',
'http://baike.baidu.com/view/3074316.htm']

head_html = "<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>"
end_html = "</body><html>"

def filter_tags(htmlstr):
	re_cdata = re.compile('//<!\[CDATA\[[^>]*//\]\]>', re.I)  # 匹配CDATA
	re_comment = re.compile('<!--[^>]*-->')  # HTML注释
	re_a = re.compile('</?\s*a[^>]*>')  # HTML a标签
	blank_line = re.compile('\n+')  # 去掉多余的空行
	result = re_cdata.sub('', htmlstr)  # 去掉CDATA
	result = re_a.sub('', result)  # HTML a标签
	result = re_comment.sub('', result)  # 去掉HTML注释
	result = blank_line.sub('', result)
	return result

def filter_space(str):
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

def fetchSite(index, website, l):
	page = urllib.urlopen(website)
	soup = bs(page.read())
	for cli in soup.findAll('li'):
		for link in cli.findAll('a', {"class" : "title nslog:7450"}):
			girl = {}
			girl['index'] = index
			girl['name'] = link.get_text()
			girl['link'] = link.get('href')
			index += 1
			l.append(girl)
	return index;

def wrap(index, link, l):
	girl = {}
	girl['name'] = ''
	girl['link'] = link
	girl['index'] = index
	index += 1
	l.append(girl)
	return index

def fetchContext(girl):
	print "the girl name: ", girl['name'], " and the site: ", girl['link'], '\n'
	waplink = "http://wap{0}".format(girl['link'][len("http://"):])
	print "the link: ", waplink, "\n"
	page = urllib.urlopen(waplink)
	try:
		soup = bs(page.read())
	except HTMLParseError:
		return
	else:
		fout = open("{0}.html".format(girl['index']), 'w')
		for cdiv in soup.findAll('div', {"id" : "main"}):
			for img in cdiv.findAll('img'):
				img.extract()  # 删除table工具节点
			for tool in cdiv.findAll('table', {"class" : "foot-tool"}):
				lins = tool.findAll('a')
				waplink1 = ''
				if len(lins) > 1:
					if len(lins) == 2:
						waplink1 = lins[0].get("href")
					else:
						waplink1 = lins[1].get("href")
					if waplink1[0:7] != 'http://':
						waplink1 = "http://wapbaike.baidu.com" + waplink1
					fetchContextI(cdiv, waplink1)
				tool.extract()  # 删除table工具节点
			# print >> fout , cdiv.prettify(formatter="html")
			print >> fout , head_html, filter_tags(cdiv.prettify(formatter="html")), end_html
		fout.close()

def fetchContextI(main, waplink):
	print "next link: ", waplink, "\n"
	page = urllib.urlopen(waplink)
	soup = bs(page.read())
	for cdiv in soup.findAll('div', {"class" : "content"}):
		for img in cdiv.findAll('img'):
			img.extract()  # 删除table工具节点
		main.append(cdiv)
	
def fetchImage(index, waplink):
    # waplink = "http://wap{0}".format(waplink[len("http://"):])
    print "next link: ", waplink, "\n"
    page = urllib.urlopen(waplink)
    soup = bs(page.read())
    outpath = "{0}.jpg".format(index)
    for img in soup.findAll('img', {"class" : "card-image editorImg log-set-param"}):
		urlretrieve(img["src"], outpath)
    
def listInfo(dirp):
	infos = []
 	cpinyin = pinyin()
	for pdir, dirs , files in os.walk(dirp):
		for file in files:
			if(file[-5:] == '.html'):
				path = os.path.join(pdir, file)
				fin = open(path, 'r')
				soup = bs(fin)
				name = filter_space(soup.h1.get_text()).encode("utf-8")
				print name
				girl = "{0}@@@{1}@@@{2}@@@{3}\n".format(file, name, len(soup.prettify(formatter="html")), filter_space(soup.p.get_text()))
				print girl
				infos.append(girl)
		break
	return infos
	
if __name__ == '__main__':
# 	cpath = "E:\\j2se_workspace\\PicProject\\mpic--test-2013\\client\\girls3\\assets\\langs\\zh-CN\\165.html"
# 	fin = open(cpath, 'r')
# 	cpath1 = "E:\\j2se_workspace\\PicProject\\mpic--test-2013\\client\\girls3\\assets\\langs\\zh-CN\\165.html-1"
# 	fout = open(cpath1, 'w')
# 	soup = bs(fin)
#    	html = filter_tags(soup.prettify(formatter="html"))
# 	fin.close()
# 	fout.write(html)
# 	fout.close()
# 	sys.exit(0)
#    	sl = "羽田桃子（はねだももこ），日本av女优，　2010年10月出道。"
#    	print filter_space(sl)
#    	sys.exit(0)
   	
#  	path = "E:\\j2se_workspace\\PicProject\\mpic--test-2013\\client\\AVGallery\\assets\\langs\\zh-CN"
#  	infos = listInfo(path)
#  	fout = open("info", 'w')
#  	fout.writelines(infos)
#  	fout.close()
#  	sys.exit(0)

    index = 0
    for link in linklist:
        fetchImage(index, link)
        index += 1
    sys.exit(0)
        
#  	pinyin = pinyin()
#  	a = pinyin.get('中文', True)
#  	print a
#  	sys.exit(0)

# 	lgirl = {"index": 0, "link": "http://baike.baidu.com/view/1813834.htm", "name": "\u9999\u5742\u767e\u5408"}
# 	fetchContext(lgirl)
# 	sys.exit(0)
	
# 	reallst = []
# 	index = 0
# 	for lnk in linklist:
# 		index = wrap(index, lnk, reallst)
# 		
# 	print "the size is: {0}".format(len(reallst))
# 	for link in reallst:
# 		fetchContext(link)
# 	
# 	path = "E:\\j2se_workspace\\PicProject\\mpic--test-2013\\servercode"
# 	infos = listInfo(path)
#  	fout = open("info", 'w')
#  	fout.writelines(infos)
#  	fout.close()
#  	sys.exit(0)
	
# 	list = []
# 	reallst = []
# 	for i in range(2, 8, 1):
# 		curl = url_format.format(i, div * (i - 1))
# 		list.append(curl)
# 		
# 	index = 0
# 	for lnk in list:
# 		index = fetchSite(index, lnk, reallst)
# 		
# 	print "the size is: {0}".format(len(reallst))
# 	for link in reallst:
# 		fetchContext(link)
		
# 	data = json.dumps(reallst)
# 	fout = open("info", 'w')
# 	print >> fout , data
# 	fout.close()
