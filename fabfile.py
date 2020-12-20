from fabric2 import Connection, task
import sys

c = Connection(
    host="10.42.0.24",
    user="pi",
    connect_kwargs={
        "password": "raspberry",
    },
)


@task(name="command")
def command(ctx,command="pwd"):
    with c:
        c.run(command)


@task(name="transfer")
def transfer(ctx,file="",targetdir=""):
    with c:
        print("elééérrtt")
        result = c.put(file, targetdir)
        print(result)