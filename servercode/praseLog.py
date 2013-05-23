#! /usr/bin/env python
# coding=utf-8

import os
import re

log2 = '222.251.27.121 - - [19/May/2013:06:53:07 +0000] "GET /jp/180/4.jpg HTTP/1.1" 200 158486 "-" "Dalvik/1.4.0 (Linux; U; Android 2.3.4; HTC Wildfire S A515c Build/GRJ22)"'

def praseToLog(logs):
    logp = logs.split('-')
    log = {}
    log['ip'] = filter_space(logp[0])
    log['info'] = filter_space(logp[2])
    log['proxy'] = filter_space(logp[-1])
    return log

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

def praseLogFile(logf):
    fin = open(logf, 'r')
    logs = {}
    for line in fin.readlines():
        log = praseToLog(line)
        if logs.get(log['ip']) == None:
            log['num'] = 1
            logs[log['ip']] = log
        else:
            num = logs[log['ip']]['num']
            num += 1
            logs[log['ip']]['num'] = num
    fin.close()
    logl = sorted(logs.items(), lambda x, y: cmp(x[1]['num'], y[1]['num']), reverse=True) 
    fout = open('c.txt', 'w')
    for log in logl:
        print >> fout, log[0], " ===== ", log[1]['num']
    fout.close()

if __name__ == "__main__":
#     log21 = praseToLog(log2)
#     print log21['ip'], log21['info'], log21['proxy']
#     l = {}
#     l['a2'] = {'num':1}
#     l['a1'] = {'num':2}
#     l['a3'] = {'num':3}
#     b = sorted(l.items(), lambda x, y: cmp(x[1]['num'], y[1]['num']), reverse=True) 
#     print b
    praseLogFile("access.log")
