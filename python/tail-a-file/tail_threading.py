from __future__ import print_function
import sys
import time
import threading
import collections

from plumbum import cmd


log_content_queue = collections.deque()


def write_logs():
    with open('./log_file.txt', 'w') as f:
        i = 0
        while True:
            i += 1
            f.write('Log line number %d\n' % i)
            f.flush()
            time.sleep(1)


def tail_to_queue(log_content_queue):
    tail = cmd.tail.popen(['-f', './log_file.txt'])
    while True:
        for line in tail.iter_lines():
            log_content_queue.append(line)


thread0 = threading.Thread(target=write_logs)
thread0.daemon = True
thread0.start()

time.sleep(.1)

thread1 = threading.Thread(target=tail_to_queue, args=(log_content_queue,))
thread1.daemon = True
thread1.start()

while True:
    if len(log_content_queue) > 0:
        outline, errline = log_content_queue.popleft()
        sys.stdout.write(outline + '\n')
        sys.stdout.flush()
    else:
        time.sleep(.1)
