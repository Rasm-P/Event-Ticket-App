# Event-Ticket-App

Android Jetpack Compose project for the blockchain event ticket client app.

Contributors:
- Rasmus Prætorius s215777

Project description
==================
This project constitutes the android event ticket client app frontend with functionality for a local crypto wallet including district event organizer and customer modes.
## Organizer mode ##
- Ticket scanning
- Ticket registration
- Registration log

## Customer mode ##
- Ticket purchase
- Resell tickets
- Purchase tickets from resale
- Withdraw ticket from resale
- Present ticket QR-codes

The blockchain used for this project is the Polygon PoS Mumbai testnet.

NPM Hardhat backend project containing the smart contracts to be deployed on the blockchain can be found [here](https://github.com/Rasm-P/Event-Ticket-Smart-Contract).

Technologies
==================
The technologies used for this project includes:
- Jetpack Compose
- Web3j
- Dagger Hilt
- ML Kit
- CameraX
- Zxing

Design patterns
==================
The design patterns used for this project includes:
- Repository pattern
- MVVM
- Dependency injection

Using the project
==================
## Project setup ##
In order to run the project, the gradle.properties file first has to be set up with the following entries:
```
API_KEY="Alchemy/Infura API key"
TICKET_CONTRACT="Address"
RESALE_CONTRACT="Address"
REGISTER_CONTRACT="Address"
CHAINID=80001L
```
To use the organizer mode, the user customer address first has to be promoted to the role of organizer by the TicketContract admin.
