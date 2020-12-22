const WebSocket = require('ws');
    class DittoWebSocket {

        constructor(pingInterval = 60000) {
            this.pingInterval = pingInterval;
            this.stopPing = false;
            this.clientCallbacks = {};
        }
        registerForMessages() {
            this.sendRaw('START-SEND-MESSAGES');
            console.log('registering')
            //this.logSendToUI(undefined, 'START-SEND-MESSAGES', '', 'tell Ditto that i want to receive Messages');
        }

        onConnected() {
            console.log('connected');
            this.registerForMessages();
        }
        connect(/*connectionConfig, callback*/) {
            const baseUrl = 'ws://ditto:ditto@localhost:8080/ws/1';
            this.ws = new WebSocket(baseUrl);
            this.ws.onopen = () => this.onOpen(this.onConnected());

        }

        close(callback) {
            this.ws.onclose = callback;
            this.ws.close();
        }

        onOpen(callback) {
            // define as functions, so the message is executed in current context
            this.ws.onmessage = (message) => this.onMessage(message);
            this.ws.onclose = () => this.onClosed();
            this.ws.onerror = (error) => this.onError(error);
            if (isDefined(callback)) {
                callback();
            }
            //this.schedulePingMessage();
        }

        onMessage(message) {
            if(message.data != "START-SEND-MESSAGES:ACK"){
                var obj = JSON.parse(message.data)
                if(obj['topic'].includes('messages')){
                    let array = obj['topic'].split('/')
                    let subject = array.pop()
                    sendToDevices(subject)
                }
            }
        }

        onClosed() {
            this.stopPing = true;
            if (isDefined(this.clientCallbacks.onClosed)) {
                this.clientCallbacks.onClosed();
            }
        }

        onError(error) {
            console.log(`error: ${e}`);
            if (isDefined(this.clientCallbacks.onError)) {
                this.clientCallbacks.onError(error);
            }
        }


        sendRaw(content) {
            this.ws.send(content);
        }

        send(json) {
            console.log(`sending JSON ${JSON.stringify(json)}`);
            this.sendRaw(JSON.stringify(json));
        }

        reply(message, payload, contentType, status) {
            const response = Object.assign({
                status
            }, this.protocolMessage(
                message.headers['thing-id'],
                message.topic,
                message.headers.subject,
                message.headers['correlation-id'],
                contentType,
                "FROM",
                message.path.replace("inbox", "outbox"),
                payload
            ));

            this.send(response);
        }

        /**
        * Create a Ditto protocol WebSocket API Message.
        */
        protocolMessage(thingId, topic, subject, correlationId, contentType, direction, path, payload) {
            return Object.assign({
                    "headers": {
                        "thing-id": thingId,
                        subject,
                        "correlation-id": correlationId,
                        "content-type": contentType,
                        direction
                    }
                },
                this.protocolEnvelope(topic, path, payload)
            );
        }

        /*
        * Create a ditto protocol envelope.
        */
        protocolEnvelope(topic, path, value) {
            return {
                topic,
                path,
                value
            };
        }

        setOnMessage(onMessage) {
            this.clientCallbacks.onMessage = onMessage;
        }

        setOnClosed(onClosed) {
            this.clientCallbacks.onClosed = onClosed;
        }

        setOnError(onError) {
            this.clientCallbacks.onError = onError;
        }

        schedulePingMessage() {
            setTimeout(() => this.sendPingMessage(), this.pingInterval);
        }

        sendPingMessage() {
            if (this.stopPing) {
                this.stopPing = false;
            } else {
                this.sendRaw(new ArrayBuffer(0));
                this.schedulePingMessage();
            }
        }
    }

    const protocolEnvelope = (topic, path, value) =>{
        return {
            topic,
            path,
            value
        };
    }


    const path = require('path')
    const rti = require('rticonnextdds-connector')
const configFile = path.join(__dirname, '/Every.xml')
const runsensepi = async (sock) => {

    //

    //
    const connector = new rti.Connector('MyParticipantLibrary::MySubParticipant', configFile)
    const input = connector.getInput('MySubscriber::MysensepiReader')
    try {
        console.log('Waiting for publications...')
        await input.waitForPublications()

        console.log('Waiting for data...')
        for (let i = 0; i < 500; i++) {
        await input.wait()
        input.take()
        for (const sample of input.samples.validDataIter) {
            const data = sample.getJson()
        const temperature = data.temperature
        const humidity = data.humidity
        const pressure = data.pressure
        console.log('Received temperature: ' +temperature)
        console.log('Received humidity: ' +humidity)
        console.log('Received pressure: ' +pressure)

    const updateFeatureMessage = protocolEnvelope(
        'szakdoga.bme.vik/sense/things/twin/commands/modify',
                '/',   
                {
                    "thingId":"szakdoga.bme.vik:sense",
      "features":{
       "temperatureSensor":{
            "properties":{
           "temperature" :temperature,
}},       "humiditySensor":{
            "properties":{
           "humidity" :humidity,
}},       "pressureSensor":{
            "properties":{
           "pressure" :pressure,
}},     }}
        )
        sock.send(updateFeatureMessage)
    }}} catch (err) {
        console.log('Error encountered: ' + err)
    }
    connector.close()
    }


    isDefined = (arg) => typeof arg !== 'undefined';
    const sendToDevices = async (message)=> {
    const connector = new rti.Connector('MyParticipantLibrary::MyPubParticipant', configFile)
    const output = connector.getOutput('MyPublisher::MyMessageWriter')
    try {
        console.log('Waiting for subscriptions...')
        await output.waitForSubscriptions()

        console.log('Writing...')
        output.instance.setString('Message', message)
        output.write()

        // Wait for all subscriptions to receive the data before exiting
        await output.wait()
    } catch (err) {
        console.log('Error encountered: ' + err)
    }
    connector.close()

    }
    const socket = new DittoWebSocket()
    socket.connect()
    runsensepi(socket)