

from __future__ import print_function
import time
import os
import signal
import sys
import ctypes
import threading
import multiprocessing
import Queue

from plumbum import cmd, ProcessExecutionError


# the file-tailer class

class FileTailer():

    def __init__(self, filepath):
        def tail_file_to_queue(filepath, queue, error_repr):
            # handle SIGINT nicely (a try / catch KeyboardInterrupt works too)
            def exit_cleanly(sig, frame):
                print('exiting the tail-file proc')  # NOTE just for demo
                sys.exit(0)
            signal.signal(signal.SIGINT, exit_cleanly)
            # do the actual logic
            try:
                tail = cmd.tail.popen(['-f', filepath])
                while True:
                    for stdout, _ in tail.iter_lines():
                        queue.put(stdout)
            except ProcessExecutionError:
                error_repr.value = True
                raise
        self._queue = multiprocessing.Queue()
        self._error_repr = multiprocessing.Value(ctypes.c_int8, 0)
        self.proc = multiprocessing.Process(
            target=tail_file_to_queue,
            args=(filepath, self._queue, self._error_repr)
        )
        self.proc.start()

    def __enter__(self):
        return self

    def __exit__(self, *args, **kwargs):
        self.stop_tailing()

    def getline(self):
        """
        Returns the newest line if there is one, or None if there is no
        new line. If we've raised a plubmbum.ProcessExecutionError in
        the child process, it gets re-raised in the main process.

        """
        if self._error_repr.value:
            raise FileTailError('The file tail process died because of a '
                                'plumbum exception, check logs')
        try:
            return self._queue.get(block=False)
        except Queue.Empty:
            return None

    def wait_for_line(self, sleep=.01):
        # NOTE we don't use block=True in _queue.get because
        # we don't want to actually block, we want to keep
        # checking _error_repr for error messages.
        line = None
        line = self.getline()
        while line is None:
            time.sleep(sleep)
            line = self.getline()
        return line

    def stop_tailing(self):
        os.kill(self.proc.pid, signal.SIGINT)


class FileTailError(Exception):
    pass


# demo program

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


thread = threading.Thread(target=write_logs)
thread.start()

time.sleep(.5)


with FileTailer('./log_file.txt') as ftailer:
    while not finished:
        line = None
        line = ftailer.wait_for_line()
        print(line)
        if '5' in line:
            finished = True

thread.join()
print('Exiting main thread')
