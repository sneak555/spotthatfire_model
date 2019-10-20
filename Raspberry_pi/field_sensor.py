from sense_hat import SenseHat
from Adafruit_IO import Client
import time
import datetime

ADAFRUIT_IO_USERNAME = "USER_NAME"
ADAFRUIT_IO_KEY = "API_KEY"

SLEEP = 60

TEMPERATURE_OFFSET = -20


class Sensor():
    def __init__(self):
        sense = self.start(),
        temperature = None,
        pressure = None,
        humidity = None
        
    def start(self):
        self.sense = SenseHat()
        self.sense.clear()

    def get_temperature(self):
        self.temperature = "{0:.4f}".format(self.sense.get_temperature() + TEMPERATURE_OFFSET)
        
    def get_pressure(self):
        self.pressure = "{0:.4f}".format(self.sense.get_pressure())      
        
    def get_humidity(self):
        self.humidity = "{0:.4f}".format(self.sense.get_humidity())        

    def get_all(self):
        self.get_temperature()
        self.get_humidity()
        self.get_pressure()        

class Outbound():
    def __init__(self):
        aio = self.start()
        
    def start(self):
        self.aio = Client(ADAFRUIT_IO_USERNAME, ADAFRUIT_IO_KEY)

    def send(self, sensor):
        temperature = self.aio.feeds("temperature")
        humidity = self.aio.feeds("humidity")
        pressure = self.aio.feeds("pressure")
    
        self.aio.send_data(temperature.key, sensor.temperature)
        self.aio.send_data(humidity.key, sensor.humidity)
        self.aio.send_data(pressure.key, sensor.pressure)    


def print_out(sensor):
    print("==== {} ====".format(datetime.datetime.now()))
    print("Temperature: {} C".format(sensor.temperature))
    print("Humidity: {} %".format(sensor.humidity))
    print("Pressure: {} mBar".format(sensor.pressure))
    print("")
    
def pause():
    time.sleep(SLEEP)
    

outbound = Outbound()
sensor = Sensor()

while(True):
    sensor.get_all()
    outbound.send(sensor)
    print_out(sensor)
    pause()
