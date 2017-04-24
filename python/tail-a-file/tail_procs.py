from __future__ import print_function
import os
import signal
import sys
import time
import multiprocessing
import threading
import Queue

from plumbum import cmd


manager = multiprocessing.Manager()
log_content_queue = multiprocessing.Queue()
finished = False


def write_logs():
    with open('./log_file.txt', 'w') as f:
        i = 0
        while True:
            i += 1
            f.write('Log line number %d\n' % i)
            f.flush()
            if finished:
                return
            time.sleep(1)


def tail_to_queue(log_content_queue):
    # handle SIGINT nicely (a try / catch KeyboardInterrupt works too)
    def exit_cleanly(signal, frame):
        sys.exit(0)
    signal.signal(signal.SIGINT, exit_cleanly)
    # do the actual logic
    tail = cmd.tail.popen(['-f', './log_file.txt'])
    while True:
        for line in tail.iter_lines():
            log_content_queue.put(line)


thread = threading.Thread(target=write_logs)
thread.start()

time.sleep(.1)

proc = multiprocessing.Process(target=tail_to_queue, args=(log_content_queue,))
proc.start()


def interrupt_proc(proc):
    # the terminate() method on multiprocessing.Process sends a SIGTERM,
    # which is not what we want because it is likely to not clean up child
    # processes properly. This is a hand-crafted utility function to send
    # a SIGINT instead
    os.kill(proc.pid, signal.SIGINT)


while True:
    try:
        outline, errline = log_content_queue.get(block=False)
        sys.stdout.write(outline + '\n')
        sys.stdout.flush()
        if '5' in outline:
            sys.stdout.write('Terminating process and thread\n')
            finished = True
            interrupt_proc(proc)
            break
    except Queue.Empty:
        time.sleep(.1)


time.sleep(2)  # long enough for the thread to die

if proc.is_alive():
    raise RuntimeError
if thread.is_alive():
    raise RuntimeError

sys.stdout.write('Thread and process terminated!\n')
