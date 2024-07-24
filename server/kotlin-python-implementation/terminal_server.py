import os
import select
import subprocess
import threading
import socket


def read_stdout_nonblocking(process, connection):
    stdout_fd = process.stdout.fileno()
    flag = False

    while True:
        # Use select to check if there is data to read
        readable, _, _ = select.select([stdout_fd], [], [], 0.1)

        if stdout_fd in readable:
            output = os.read(stdout_fd, 4096).decode('utf-8')
            if output:
                if (output[-1] != "\n"):
                    output += "\n"
                print(output, end='')
                connection.sendall(output.encode())
                flag = True
            else:
                break  # End of stream
        elif process.poll() is not None:
            break  # Process has terminated
        elif flag == True:
            print("NULL_MARKER\n", end='')
            connection.sendall("NULL_MARKER\n".encode())
            flag = False


def read_stderr_nonblocking(process, connection):
    stderr_fd = process.stderr.fileno()
    flag = False

    while True:
        # Use select to check if there is data to read
        readable, _, _ = select.select([stderr_fd], [], [], 0.1)

        if stderr_fd in readable:
            output = os.read(stderr_fd, 4096).decode('utf-8')
            if output:
                if (output[-1] != "\n"):
                    output += "\n"
                print(output, end='')
                connection.sendall(output.encode())
                flag = True
            else:
                break  # End of stream
        elif process.poll() is not None:
            break  # Process has terminated
        elif flag == True:
            print("NULL_MARKER\n", end='')
            connection.sendall("NULL_MARKER\n".encode())
            flag = False


def command(cmd):
    try:
        # Send user input to the subprocess
        process.stdin.write(cmd + '\n')
        process.stdin.flush()

    except KeyboardInterrupt:
        print("\nExiting...")
        process.terminate()
        stdout_thread.join()  # Ensure the thread has finished
        process.wait()  # Wait for the process to terminate
    except EOFError:
        print("\nExiting...")
        process.terminate()
        stdout_thread.join()  # Ensure the thread has finished
        process.wait()  # Wait for the process to terminate


if __name__ == "__main__":

    host = "192.168.101.3"
    port = 12349

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind((host, port))
    s.listen()
    print(f"Server listening on {host}:{port}")
    conn, addr = s.accept()
    print(f"Connected by {addr}")

    process = subprocess.Popen(
        ['bash'],  # Command to start the shell
        stdin=subprocess.PIPE,  # Pipe for sending input
        stdout=subprocess.PIPE,  # Pipe for receiving output
        stderr=subprocess.PIPE,  # Pipe for receiving errors
        text=True,  # Use text mode for input/output
        bufsize=1,  # Line-buffered mode
        universal_newlines=True  # Ensure newline characters are handled consistently
    )

    # Start a thread to read the subprocess stdout
    stdout_thread = threading.Thread(target=read_stdout_nonblocking, args=(process, conn))
    stdout_thread.start()
    stderr_thread = threading.Thread(target=read_stderr_nonblocking, args=(process, conn))
    stderr_thread.start()

    while True:
        data = conn.recv(1024)
        command_received = data.decode()
        command(command_received)

