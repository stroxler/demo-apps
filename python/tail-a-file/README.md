# Tailing logs in realtime with python parallelism

I originally wanted to work this functionality out for some long-running
integration tests, where I could set a logging `FileHandler` as a way of
finding out what was happening inside of an opaque service (I was testing a
plugin for a bigger software application that didn't have a good api).

The purpose is just to have a minimal code snippet illustrating:
  - python code sending lines to a log file in a thread
  - another python thread that tails the logfile using `plumbum`, and
    enqueues log lines
  - the main thread dequeues log lines and prints them

I realized pretty quickly that although it's easy to do this in python,
it's not easy to clean up the log-tailing thread (more on this later), so
I wound up writing two versions:
 - `tail_threading.py`, which uses daemon threads and as a result
   wouldn't be a good choice for a long-running use case where we do
   this repeatedly, since we'll leak threads
 - `tail_procs.py`, which is more complex because it uses
   `multiprocessing`, but as a result is fairly easy to 

## Threading example: `tail_threading.py`

The code in `tail_threading.py` is pretty simple and mostly speaks
for itself. I'm using `plumbum` to tail the log file and get a `Popen`
instance back, and then the iterator interface to block and pull
lines from the `Popen`; each line is actually a 2-item list of stdout
and stderr, where typically one or the other is `None`; in this case
I only care about stdout.

The `dequeue` class is thread-safe according to the docs; every operation I use
here is explicitly documented to be threadsafe.

This example runs perfectly well, but there's one problem: I could use some
kind of signalling mechanism to terminate the `write_logs` thread from the main
thread, but I don't have a good way of terminating the `tail_to_queue` thread
because it's generally blocking waiting for lines from `Popen`.

It *may* be possible to send an exception to this thread from the main thread
using a more advanced interface, but I decided to use a multiprocessing
solution to fix this.

## Multiprocessing example: `tail_procs.py`

When we use `multiprocessing` and try to clean up after ourselves,
things get a bit more complex.

The first change is that in the `write_logs` thread we are checking the global
variable `finished`, and exiting the thread (which is no longer a `daemon`
thread) when the main thread decides we're ready.

The bigger change is that we use a `multiprocessing.Process` for
`tail_to_queue`, and we need to use a `multiprocessing.Queue`, which *must* be
passed in via `args` (you cannot use a global variable) to the `Process`.

Inside `main`, when we're ready to exit we set `finished = True` to kill the
`write_to_logs` thread, and we use `os.kill` to send a `SIGINT` to the
`tail_to_queue` process. Why not just use the `Process.terminate()` method?
Because that sends a `SIGTERM`, which could lead to resources and child
processes of that second `Process` not being properly cleaned up.

Finally, to avoid a spurious traceback at runtime, we set a `SIGINT` handler in
the `tail_to_queue` process; a `try/catch` for a `KeyboardInterrupt` would also
have worked here. Note that we could easily add extra cleanup into our
handler if we wanted to.

## FileTailer demo: `file_tailer.py`

In `file_tailer.py`, I use the ideas from `tail_procs.py` to create a
class that acts as a context manager and handles both the tailing process
and access to the `Queue` for inter-process messaging.

There are no new threading/multiprocessing ideas here, but wrapping up
both ends of the multiprocessing call in a context manager makes the
client code much simpler, and helps ensure that we could use this approach
in a large program without much risk of leaking child processes.
