import sys
from sys import path as sys_path
from os import path as os_path

file_path = os_path.dirname(os_path.realpath(__file__))
sys_path.append(file_path + "/../../../")
import rticonnextdds_connector as rti
with rti.open_connector(
        config_name="MyParticipantLibrary::MyPubParticipant",
        url=file_path +"/Every.xml" ) as connector:
  temperature =sys.argv[1]
  humidity =sys.argv[2]
  pressure =sys.argv[3]
  output = connector.get_output("MyPublisher::MysensepiWriter")

  print("Waiting for subscriptions...")
  output.wait_for_subscriptions()
        
  output.instance.set_string("temperature",temperature)
  output.instance.set_string("humidity",humidity)
  output.instance.set_string("pressure",pressure)
  output.write()
  output.wait()