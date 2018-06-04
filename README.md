# IxorTalk Gateway

## Architecture

IxorTalk is an open source IoT-platform.  It is a microservice architecture, built using Spring Cloud, providing various features like asset management, monitoring, dashboarding, alerting, centralized configuration, user management, ... 

An IxorTalk instance is highly configurable through the use of Spring Cloud Config, see [ixortalk.config.demo](https://github.com/ixortalk/ixortalk.config.demo) for an example configuration.

### Microservices available

* [ixortalk-config-server](https://github.com/ixortalk/ixortalk-config-server)
* [ixortalk-gateway](https://github.com/ixortalk/ixortalk-gateway)
* [ixortalk-authserver](https://github.com/ixortalk/ixortalk-authserver)
* [ixortalk-assetmgmt](https://github.com/ixortalk/ixortalk-assetmgmt)
* [ixortalk-assetstate](https://github.com/ixortalk/ixortalk-assetstate)
* [ixortalk-mailing-service](https://github.com/ixortalk/ixortalk-mailing-service)
* [ixortalk-nginx-docker-proxy](https://github.com/ixortalk/ixortalk-nginx-docker-proxy)
* [ixortalk-mongodb](https://github.com/ixortalk/ixortalk-mongodb)
* [ixortalk-alertmanager](https://github.com/ixortalk/ixortalk-alertmanager)
* [ixortalk-prometheus](https://github.com/ixortalk/ixortalk-prometheus)
* [ixortalk-blackbox-exporter](https://github.com/ixortalk/ixortalk-blackbox-exporter)
* [ixortalk-grafana](https://github.com/ixortalk/ixortalk-grafana)

### IxorTalk Gateway 

The gateway acts as a Zuul reverse proxy for all microservices in the platform and enforces OAuth2 on all endpoints.  The gateway is the only component in the stack that should be exposed to the outside world.

## Building

For building Maven, Java 1.8+ and Docker are required. 

```
$ mvn clean install
```

To build without a Docker image being built:

```
$ mvn clean install -DskipDocker
```

## Running

For easy bootstrapping an IxorTalk instance, see [ixortalk-config-docker](https://github.com/ixortalkadmin/ixortalk-config-docker) which contains a Docker compose configuration for the complete stack.

## Contributing

Pull request are welcome.

## License

```
The MIT License (MIT)

Copyright (c) 2016-present IxorTalk CVBA

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
