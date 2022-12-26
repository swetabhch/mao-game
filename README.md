# cs0320 Term Project 2021

**Team Members:** Nishka Pant, Jay Sarva, Swetabh Changkakoti, Sidharth Anand

**Team Strengths and Weaknesses:**

Strengths:
- Java/Python/JavaScript
- Algorithmic thinking
- Picking things up fairly quickly

Weaknesses:
- Front end development
- General organization
- Thorough and frequent testing
- Accurate time estimation


**Project Idea(s):** _Fill this in with three unique ideas! (Due by March 1)_
### Idea 1:   Multiplayer interface for the card game Mao

#### Requirements:
- Problem: Create a digital platform for this game (none exists at this time)
- Solution: We will make this interface

Critical Features:
- Multiplayer
    - Why this is being included: To play with friends
    - Challenges: Unsure how to implement at this point in time (socket?)
- Random rule generator/picker (will not pick conflicting rules)
    - Why this is being included: To keep the game fun and interesting
    - Challenges: Making sure rules are not conflicting, chosing appropriately varied rules
- Chat box
    - Why this is being included: To implement "verbal rules"
    - Challenges: Matching up who said what, interpreting the text in the context of the rules of the game
- Manual penalty button
    - Why this is being included: To allow for more freedom of game play (and also to be a first checkpoint for a working implementation)
    - Challenges: Front end/JavaScript / Only chosen person can use/access the button

Reach Goal/Nice to have
- AI players
    - Why this is being included: To have more players if you're playing in a small group
    - Challenges: Implementing a "player" with reasonable/human behavior

**HTA Approval (crusch):** Approved, but the AI player _has_ to be a feature or else the project will lack a core algorithm.

### Idea 2

### Idea 3

**Mentor TA:** Christine Wang christinewang@brown.edu

## Meetings
_On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** March 15, 2021

**4-Way Checkpoint:** April 5, 2021

**Adversary Checkpoint:** April 12, 2021

## Overview
Mao is a multiplayer turn-based card game where the first person to run out of cards wins. It is Uno-like on the basis of its gameplay (i.e. a card can only be played if its suit or rank matches that of the previous card), but what makes it spicy is the inclusion of additional rules that only the host knows about. Every time someone breaks a rule, they are penalized by being forced to draw a card. The rules include various things from phrases you have to say when playing a certain card, switching up the order of play, actions you have to carry out, etc. The idea is that players who do not know the rules will pick up on them as the game progresses (if they can figure out the patterns). 

Our implementation of Mao, hosted on Heroku (at https://maogame.herokuapp.com/), attempts to recreate the chaos of this game as closely as possible in a digital space. This chaos and the sheer customizability of the game are hard to model, which is probably why there has been no online platform for Mao before this. We attempted to approach this issue by restricting the types and structures of enforceable rules (eg. saying “have a nice day” when you play a 7) while keeping them broad enough for fun gameplay and allowing arbitrary unenforced rules (e.g. turning your camera off on Zoom when you play a Queen) too.

## Accessing, Building, and Running
Navigate to the root directory of the project repository in a terminal, and build the program with "mvn package". Start the backend server by using "./run". In a separate terminal, start the React frontend by navigating to the frontend folder inside the root directory and using "npm start". Visit the website on localhost:3000.

## Division of Labor
* Nishka - frontend design/UI, React logic
* Sid - Websocket connection between frontend and backend, component integration
* Swetabh - backend class/interface structure, rule generation/storage/interactions
* Jay - AI players, integration with backend via Python webserver

## Design Details
### Sockets
Multiplayer games are a common use case for websockets because they often require a bidirectional connection between clients and the server. Generally, when a client makes an action (e.g. playing a card), they send it to the server, which then runs some backend logic before sending a message back to all the clients so they can update their state. We modeled our client-server relationship according to this design pattern and specified all the different message types using enums on the backend and frontend, making sure to keep track of the payload specification for each type.

### AI Players
The AI Players use sklearn as the base framework. Each turn that a player is not penalized, the AI is sent both the card and message played. Once the AI player’s turn comes around, it prepares a model based on the data it has from the ongoing game. The AI Player then chooses a card -- it is more likely to choose a card that it has seen either the rank/suit be played before than not (for example, if the AI Player has seen a 9 of clubs played a few turns ago, it is more likely to play a 7 of clubs than a 7 of diamonds, which it has never seen before). Then, based on the model it has prepared, it will determine what message it should send alongside the card. 

The AI Players have 3 difficulty settings based on their “memory.” Lower difficulty AI players will not remember very many past turns from the game so their model will almost never be able to have complete accuracy in the game, while higher difficulty players remember all correct turns played.

### Rule Handling and Enforcement
In designing the rule-handling functionality for the game, we needed to ensure that the game was mostly automated while also maintaining the wide customization options it has offline. Therefore, in recognizing that there was only so much that could be enforced automatically, we provided structured, enforceable rules and unstructured, unenforceable rules.

Enforceable rules follow the structure: “When a card is played with suit from [<list of suits>] and rank from [<list of ranks>], <action to be done>.”, where this action could be having to say something specific, skipping the next player, or reversing gameplay. Our rules package in the backend describes this very structure in the Rule interface, and has associated interfaces for rule storage and generation. These generation and storage interfaces also help ensure that the rules in question are not conflicting (which is possible with silence rules if extraneous speech is allowed, for example). Moreover, each rule is associated with a hint, which appears in the chat if the rule is violated.
Unenforceable rules are completely unstructured, and are just Strings a user enters. The responsibility for enforcing these rules lies directly with the host, who must, in a 4-step process, select the manual penalty button, select a player, select a rule to enforce, and press the submit button, which then leads to the selected player getting penalized, with the hint appearing in chat. The length of this process deliberately makes it slower, so as to reduce the likelihood of the manual penalty button being abused by the host.
    
### Frontend Design Choices
The frontend is set up with a react-router in Main.JS that keeps all the pages/urls separated accordingly. Each page is associated with one component, and each component is made up of various subcomponents + style sheets. All the components are functional components for ease of use. All state is stored in the most general component for each page for ease of communication with the backend/websockets and updating of state. 

## Notes
Testing – JUnit tests have been included for methods that do not involve connections to WebSockets and that are otherwise self-contained. These tests are automatically run when entering `mvn package` to build the application, but might also be run by entering `mvn test` in the command line.
