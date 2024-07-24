import io
import os
import select
import subprocess
import threading
import socket
import logging
import time
import sys

logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')


def is_connection_live(conn):
    try:
        conn.sendall("aXNsaXZlY29ubmVjdA==".encode())
        return True
    except (OSError, socket.error, socket.timeout):
        return False


def read_stdout_nonblocking(process, connection):
    stdout_fd = process.stdout.fileno()
    flag = False

    while True:
        # Use select to check if there is data to read
        readable, _, _ = select.select([stdout_fd], [], [], 0.1)

        if stdout_fd in readable:
            output = os.read(stdout_fd, 4096).decode('utf-8')
            if output:
                if output[-1] != "\n":
                    output += "\n"
                print(output, end='')
                connection.sendall(output.encode())
                flag = True
            else:
                break  # End of stream
        elif process.poll() is not None:
            break  # Process has terminated
        elif flag:
            print("NULL_MARKER\n", end='')
            connection.sendall("NULL_MARKER\n".encode())
            flag = False
    logging.debug("Stdout thread has stopped.")


def read_stderr_nonblocking(process, connection):
    stderr_fd = process.stderr.fileno()
    flag = False

    while True:
        # Check if the process is still running
        # Use select to check if there is data to read
        readable, _, _ = select.select([stderr_fd], [], [], 0.1)

        if stderr_fd in readable:
            output = os.read(stderr_fd, 4096).decode('utf-8')
            if output:
                if output[-1] != "\n":
                    output += "\n"
                print(output, end='')
                connection.sendall(output.encode())
                flag = True
            else:
                break  # End of stream
        elif process.poll() is not None:
            break  # Process has terminated
        elif flag:
            print("NULL_MARKER\n", end='')
            connection.sendall("NULL_MARKER\n".encode())
            flag = False
    logging.debug("Stderr thread has stopped.")


def command(cmd, process, stdout_thread, stderr_thread):
    try:
        process.stdin.write(cmd + '\n')
        process.stdin.flush()

    except KeyboardInterrupt:
        print("\nExiting...")
        process.terminate()
        stdout_thread.join()  # Ensure the thread has finished
        stderr_thread.join()
        process.wait()  # Wait for the process to terminate
    except EOFError:
        print("\nExiting...")
        process.terminate()
        stdout_thread.join()  # Ensure the thread has finished
        stderr_thread.join()
        process.wait()  # Wait for the process to terminate


def terminate_if_not_live(conn, process):
    while is_connection_live(conn):
        time.sleep(15)
    process.terminate()
    process.wait()
    logging.debug("Shell subprocess is terminated by terminate if not live")
    logging.debug("Terminate if not live thread ended.")


def pawn_subprocess(conn):
    process = subprocess.Popen(
        ['bash'],  # Command to start the shell
        stdin=subprocess.PIPE,  # Pipe for sending input
        stdout=subprocess.PIPE,  # Pipe for receiving output
        stderr=subprocess.PIPE,  # Pipe for receiving errors
        text=True,  # Use text mode for input/output
        bufsize=1,  # Line-buffered mode
        universal_newlines=True  # Ensure newline characters are handled consistently
    )

    stdout_thread = threading.Thread(target=read_stdout_nonblocking, args=(process, conn))
    stdout_thread.start()
    stderr_thread = threading.Thread(target=read_stderr_nonblocking, args=(process, conn))
    stderr_thread.start()
    terminate_thread = threading.Thread(target=terminate_if_not_live, args=(conn, process))
    terminate_thread.start()

    while True:
        try:
            data = conn.recv(1024)
            command_received = data.decode()
            command(command_received, process, stdout_thread, stderr_thread)
        except (OSError, ValueError, BrokenPipeError, io.UnsupportedOperation):
            break
    stdout_thread.join()
    stderr_thread.join()
    terminate_thread.join()
    logging.debug("Pawn subprocess ended thus ending shell thread.")


def main(s):
    s.listen()
    print(f"Server listening on {host}:{port}")
    conn, addr = s.accept()
    print(f"Connected by {addr}")
    shell_thread = threading.Thread(target=pawn_subprocess, args=(conn,))
    shell_thread.start()


if __name__ == "__main__":

    host = "localhost"
    port = 12349

    if len(sys.argv) == 1:
        print(f"Using default IP {host} and default port {port}")
        print("""To provide IP run as :
        python terminal_server.py <IP>
To provide IP and port run as :
        python terminal_server.py <IP> <port>
To provide port only run as :
        python terminal_server.py localhost <port>""")
    elif len(sys.argv) == 2:
        host = sys.argv[1]
        print(f"Using IP {host} and default port {port}")
    elif len(sys.argv)==3:
        host = sys.argv[1]
        port = int(sys.argv[2])
        print(f"Using IP {host} and default port {port}")

    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))

    while True:
        main(server_socket)
