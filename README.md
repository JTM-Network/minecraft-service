# Minecraft Service (Work In Progress)

## Overview

These services handle the majority of the minecraft related services being offered under the JTM Network. Splitting up the logic from features, this will be the gateway to minecraft related services, which will also have fallback features to allow for scalable load handling under stressful conditions when in production. CI/CD is handled by Github Actions and will automatically deploy changes to the development cluster currently active.

## Technologies Used:
- Kotlin
  - Spring Boot
  - Spring Cloud
- MongoDB
- Docker
- Kubernetes
- Github Actions

## Current Features
- Plugin locking/unlocking based on premium or free plugins
- Stripe payment integration
- Users can post suggestions/bugs/reviews on plugins they have used or own.