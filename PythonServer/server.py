import socket
import threading

host = '0.0.0.0'
port = 5001
player_count = 3

def host_service():
    global connections

    while True:
        conn, addr = s.accept()
        print "Accepted client", addr
        
        t = threading.Thread(target=handle_client, args=(conn,))
        t.start()

        connections.append((t, conn))

        if len(connections) == player_count:
            broadcast("START\n")

def handle_client(conn):
    while True:
        msg = conn.recv(1024)
        broadcast(msg, sender=conn)

def broadcast(msg, sender=None):
    global connections
    for t, c in connections:
        if c is not sender:
            c.send(msg)

# Connect
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(3)

print "Listening on %s" % port

# Start hosting the service
t = threading.Thread(target=host_service)
t.start()

connections = []
while True:
    cmd = raw_input()

    if cmd == "reset":

        print "Resetting connections"

        for t, c in connections:
            t.join()
        connections = []
