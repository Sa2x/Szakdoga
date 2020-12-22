import sys
from sys import path as sys_path
from os import path as os_path
from sense_hat import SenseHat
import time
sense = SenseHat()
file_path = os_path.dirname(os_path.realpath(__file__))
sys_path.append(file_path + "/../../../")
import rticonnextdds_connector as rti
with rti.open_connector(
            config_name="MyParticipantLibrary::MyPubParticipant",
        url="/home/pi/generated/Every.xml") as connector:
    while True:
        temperature =sense.get_temperature()
        humidity =sense.get_humidity()
        pressure =sense.get_pressure()
        output = connector.get_output("MyPublisher::MysensepiWriter")
        print("Waiting for subscriptions...")
            
        output.instance.set_number("temperature",temperature)
        output.instance.set_number("humidity",humidity)
        output.instance.set_number("pressure",pressure)
        output.write()
        #output.wait()
        time.sleep(5)