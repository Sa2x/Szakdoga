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

@task(name="runlocal")
def runlocal(ctx):
    ctx.run("node /home/sasa/data1/TDKCaseStudy/DigitalTwinController/generated/sensepi/AllReader.js")

@task(name="stoplocal")
def stoplocal(ctx):
    output = ctx.run("ps -ef | grep node | awk '{print $2" "$9}'")
    # print(output)
    output_stdout = output.stdout.split("\r\n")
    nodetasks = output_stdout[0].split('\n')
    for task in nodetasks:
        print("Task:"+task)
        if ("AllReader.js" in task):
            print("PID"+task.split('/')[0])
            ctx.run("kill " + task.split('/')[0])

@task(name="runremote")
def runremote(ctx,targetdir=""):
    with c:
        c.run("python3 /home/pi/generated/DeviceWriter.py")

@task(name="stopremote")
def stopremote(ctx):
    with c:
        output = c.run("ps -ef | grep python3 | awk '{print $2" "$9}'")
        #print(output)
        output_stdout = output.stdout.split("\r\n")
        pythontasks = output_stdout[0].split('\n')
        for task in pythontasks:
            if("DeviceWriter.py" in task):
                print(task)
                c.run("sudo kill "+task.split('/')[0])
