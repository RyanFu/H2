#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
fetchImages.py
    Downloads all the images on the supplied URL, and saves them to the
    specified output file ("/test/" by default)

Usage:
    python fetchImages.py http://example.com/ [output]
"""
import urlparse
from urllib2 import urlopen
from urllib import urlretrieve
import os
import sys
from mechanize import Browser, _http
from bs4 import BeautifulSoup as bs
from fnmatch import fnmatch

USER_AGENT = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071615 Fedora/3.0.1-1.fc9 Firefox/3.0.1')]
URL = "http://www.baidu.com"
IMAGE_URL = "http://image.baidu.com/"

#
# mechanize history memory
#
class NoHistory(object):
    """
    mechanize history memory
    """
  def add(self, *a, **k): pass
  def clear(self): pass
  
def fetch(url):
    result_no = 0
    browser = Browser(history = NoHistory())
    browser.set_handle_robots(False)
    browser.addheaders = USER_AGENT
    page = browser.open(url)
    html = page.read()
    soup = bs(html)
    browser.click()
    print soup.prettify()
    
def fetchFromBaidu():
    browser = Browser(history = NoHistory())
    browser.set_handle_robots(False)
    browser.addheaders = USER_AGENT
    page = browser.open(url)
    browser.select_form(name="f1")
    browser['word'] = "西洋美人"
    page = browser.submit()
    
    if 'Redirecting' in br.title():
        resp = br.follow_link(text_regex='click here')
    
    soup = bs(page.read())
    for image in soup.findAll("img"):
        try:
            print "Image: %(src)s" % image
            filename = image["src"].split("/")[-1]
            if fnmatch("*.jpg", filename) or fnmatch("*.jpeg", filename):
                parsed[2] = image["src"]
                outpath = os.path.join(out_folder, filename)
                if image["src"].lower().startswith("http"):
                    urlretrieve(image["src"], outpath)
                else:
                    urlretrieve(urlparse.urlunparse(parsed), outpath)
        except KeyError:
            continue

    
def main(url, out_folder="./test/"):
    """Downloads all the images at 'url' to /test/"""
    soup = bs(urlopen(url))
    parsed = list(urlparse.urlparse(url))

    if not os.path.exists(out_folder):
        os.makedirs(out_folder, 0777)

    for image in soup.findAll("img"):
        try:
            print "Image: %(src)s" % image
            filename = image["src"].split("/")[-1]
            if fnmatch("*.jpg", filename) or fnmatch("*.jpeg", filename):
                parsed[2] = image["src"]
                outpath = os.path.join(out_folder, filename)
                if image["src"].lower().startswith("http"):
                    urlretrieve(image["src"], outpath)
                else:
                    urlretrieve(urlparse.urlunparse(parsed), outpath)
        except KeyError:
            continue

def _usage():
    print "usage: python fetchImages.py http://example.com [outpath]"

if __name__ == "__main__":
    url = sys.argv[-1]
    out_folder = "./test/"
    if not url.lower().startswith("http"):
        out_folder = sys.argv[-1]
        url = sys.argv[-2]
        if not url.lower().startswith("http"):
            _usage()
            sys.exit(-1)
    main(url, out_folder)
