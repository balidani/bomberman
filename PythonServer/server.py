import socket
import threading

host = '127.0.0.1'
port = 4444

def handle_client(conn):
    while True:
        msg = conn.recv(1024)
        broadcast(msg, sender=conn)

def broadcast(msg, sender=None):
    global connections
    for c in connections:
        if c is not sender:
            c.send(msg)
                
# Connect
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(3)

print "Listening on %s" % port

connections = []

while True:
    conn, addr = s.accept()
    print "Accepted client", addr
    
    connections.append(conn)
    
    if len(connections) == PLAYER_COUNT:
        broadcast("START")
    
    t = threading.Thread(target=handle_client, args=(conn,))
    t.start()
