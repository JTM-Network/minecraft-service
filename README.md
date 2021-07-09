# Minecraft Service

## Overview

This service handles majority of the minecraft related services being offered under the JTM Network. Splitting up the load from other services this will be the gateway to minecraft related services, which will also fallback features to allow for scalable load handling under stressful conditions when in use. CI/CD handled by Github Actions and will automatically deploy changes to the development cluster being used.

## Technologies Used:
- Kotlin
  - Spring Boot
  - Spring Cloud

- Docker
- Kubernetes
- Github Actions

## Future Features

- Full Server Management for Spigot/Bungee/Velocity Servers
- Plugin Management
- Server command operations
- Detailed logging
