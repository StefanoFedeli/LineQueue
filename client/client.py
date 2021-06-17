import socket
import qprompt
import os
import time


class ClientSocket:
    """
    Keep the status of the Client. Handles also the logic

    ...

    Attributes
    ----------
    sock : socket
        a socket connection to the server
    last_retrieved : str
        string that contains the last answer coming from the server
    commands : list
        list of available commands

    Methods
    -------
    connect()
        create socket connection to the given hostname and port
    close()
        close the available socket connection
    _send()
        send bitstream into the socket
    _response()
        collect response from serverù
    parse_input()
        parse command
    _dispatch_command()
        execute command logic
    """
    
    def __init__(self, host:str, port:int) -> None:
        """Construct the object, default attribute

        Parameters
        ----------
        host : str
            server hostname / IP address
        port : int
            server port number
        """

        try:
            self.sock = socket.socket()
        except socket.error as err:
            print('[CLIENT LOG] Socket error because of %s',err)
            exit()
        
        self.connect(host,port)
        self.last_retrieved = ""
        self.commands = ['PUT','GET','SHUTDOWN','QUIT']


    def connect(self, host:str, port:int) -> None:
        """Create socket connection to the given hostname and port.

        Parameters
        ----------
        host : str
            server hostname / IP address
        port : int
            server port number
        """
        try:
            self.sock.connect((host, port))
        except socket.gaierror:
            print('[CLIENT LOG] There an error resolving the host')
            exit()


    def parseInput(self, command: str) -> None:
        """Clean input command activating the right command logic.

        Parameters
        ----------
        command : str
            unparsed command string
        
        Raises
        ------
        ValueError
            If command is not supported.
        """

        parsed = command.strip().split(' ',1)
        if len(parsed) < 1:
            raise ValueError()
        if len(parsed) < 2:
            parsed.append(' ')
        input = parsed[0]
        argument = parsed[1]

        if input not in self.commands:
            raise ValueError("Command not supported")
        
        self._dispatch_command(input,argument)
        
    
    def _dispatch_command(self, command: str, arg) -> None:
        """Check command's argument and execute the logic.

        Parameters
        ----------
        command : str
            parsed command ID
        arg : str/int
            unparsed argument of the command
        
        Raises
        ------
        ValueError
            If commands' argument is illegit.
        """

        if command == 'PUT':
            #TODO check valid with regex
            self._send('PUT|{0}\n'.format(arg))
        if command == 'GET':
            try:
                arg = int(arg)
                self._send('GET|{0}\n'.format(arg))
                self._response()
            except ValueError:
                print('[CLIENT LOG] Not a valid integer')
        if command == 'SHUTDOWN':
            self._send('SHUTDOWN|\n'.format(arg))
            self.close()
        if command == 'QUIT':
            self.close()
    
    def _response(self):
        """Collect answer from server by waiting for the end communication token."""

        server_msg = ''
        while '*END*' not in server_msg:
            server_msg = self.sock.recv(1024).decode('utf-8')
            if '*END*' not in server_msg:
                self.last_retrieved += server_msg

    def close(self):
        """Check command's argument and execute the logic."""

        self.sock.close()
        print("[CLIENT LOG] CONNECTION AND SOCKET CLOSED")
    
    def _send(self, message: str):
        """Send message to the server

        Parameters
        ----------
        message : str
            message to encode and send
        """

        self.sock.sendall(message.encode())
        print("[CLIENT LOG] MESSAGE was SENT -> {0}".format(message))

    def get_message(self) -> str:
        """Return stored server message."""

        msg = self.last_retrieved
        self.last_retrieved = ''
        return msg


if __name__ == "__main__":
    """ 
    Depending of where the code is executed - Docker vs Native -
    The client would present a CLI interactive menu or read commands from the appropriate file        
    """

    print('PYTHON IS RUNNING')
    producer: ClientSocket = ClientSocket("server",10042)
    if os.environ.get("MYAPP_DOCKER", False):
        with open('commands.txt', encoding='utf8') as f:
            for line in f:
                producer.parseInput(line)
                print(producer.get_message())
                time.sleep(1)
    else:
        stay_log = True
        while stay_log:
            menu = qprompt.Menu()
            menu.add(1, "GET")
            menu.add(2, "PUT")
            menu.add(3, "SHUTDOWN")
            menu.add("q", "QUIT")
            choice = menu.show()
            if choice==1:
                value = qprompt.ask_int("How many row do you want to read? ",lambda x: x>0)
                producer._dispatch_command("GET",value)
                print("MESSAGE IN THE QUEUE: \n {0}".format(producer.get_message()))
            if choice==2:
                value = qprompt.ask_str("Insert the value to add: ")
                producer._dispatch_command("PUT",value)
                print("Message '{0}' SENT".format(value))
            if choice==3:
                producer._dispatch_command("SHUTDOWN"," ")
                stay_log = False
            if choice=='q':
                producer._dispatch_command("QUIT"," ")
                stay_log = False
    print('PROGRAM IS QUITTING, BYE')