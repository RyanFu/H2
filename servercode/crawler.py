#! /usr/bin/env python
# coding=utf-8

import json
import urllib, os, fnmatch
from urllib import urlretrieve
import time
import socket

url_format = 'http://tu.baidu.com/channel/listjson?fr=channel&tag1=%E7%BE%8E%E5%A5%B3&tag2=%E5%85%A8%E9%83%A8&sorttype=0&pn={0}&rn={1}&ie=utf8&oe=utf-8&1365082781536'
folder = './all'
div = 60

def crawler(folder):
    for i in range(39340, 50000, div):
        end = div + i
        curl = url_format.format(i, div)
        print curl
        crawler1(curl, folder)
    
def crawler1(url, folder):
    page = urllib.urlopen(url)
    data = page.read()

    ddata = json.loads(data)
    
    if not os.path.exists(folder):
        os.makedirs(folder, 0777)
        
    socket.setdefaulttimeout(30)
    
    for j in ddata['data']:
        try:
            url = str(j['image_url'])
            filename = url.split("/")[-1]
            if fnmatch.fnmatch(filename, "*.jpg") or fnmatch.fnmatch(filename, "*.jpeg"):
                outpath = os.path.join(folder, filename)
                if not os.path.exists(outpath):
                    urlretrieve(url, outpath)
        except KeyError:
            continue
        except socket.timeout:
            continue
    
if __name__ == '__main__':
    crawler(folder)
