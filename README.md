# TP-S_MR_OUNI_GL3

# TPs - Distributed Systems Practical Work

Welcome to my `tp's` repository! This repository contains code, examples, and configurations for my practical works (Travaux Pratiques) focusing on distributed systems and messaging architectures.

## Current Contents

### TP1: Initiation au Serveur de messagerie pour les applications réparties

This section of the repository focuses on **RabbitMQ**. The main goal of this lab is to understand RabbitMQ messaging distributed systems by running examples.

The Java code provided in this repository covers the following messaging patterns:

- **Basic Sending and Receiving:** Using a simple queue to send a "hello" message from a producer and receive it asynchronously using a `DeliverCallback`.
  - **Files:** `Send.java`, `Receive.java`
- **Message Exchange and Routing:** Building a simple logging system using a `direct` exchange. This demonstrates how an exchange pushes messages to specific queues based on matching routing keys (e.g., severity levels like `info`, `warning`, `error`).
  - **Files:** `EmitLogDirect.java`, `ReceiveLogsDirect.java`

## Prerequisites

To run the Java examples in this repository, you will need:

- **RabbitMQ Server:** Installed and running locally.
- **Erlang:** A 64-bit supported version must be installed prior to the RabbitMQ server.
- **RabbitMQ Java Client:** The appropriate Java client libraries must be added to your project to run the code correctly.
- **RabbitMQ Management Plugin (Optional but Recommended):** Enable this plugin to use the web administration interface at `http://localhost:15672` to visually monitor your queues, exchanges, and messages.
