Author: Boston Mak
Date: Sept 2, 2018
This is a small project written in Java that compares two poker hands and determines the winner (or draw).
The version of poker is assumed to be a game of 52-card draw poker. This implies that there are no wild cards, nor any duplicate cards in either players' hands

Instructions -
Input a comma-separated list of cards for player 1 and player 2 in the format <Value><Suit>, where the value is a single character 2-9,J,Q,K,A and the suit is a single character C,D,H,S
If an invalid card is provided, the application will continue to re-prompt the user for a proper list of cards until one is finally given.

For the unit tests, I used JUnit 4.10, which was already integrated into IntelliJ.
The best way to probably run this code is to open the folder as an IntelliJ project and run the PokerHands configuration for the actual application and the Test configuration for the unit tests.

Post Mortem -
This application took me longer than expected to write, since this was the first Java application I had written from start to finish in many years.
A good amount of time was spent relearning both IntelliJ as well as JUnit and Java in general; much of the coding process was slowed due to my unfamiliarity.
In total, I would estimate between 10-12 hours in total for this project.
A rough breakdown of my hours are as follows:
- Code design and data structures: 1.5h
- I/O: 1h
- Poker hand calculation: 1h
- Poker hand comparison: 1.5h
- Misc. and bugs: .5h
- Learning and integrating JUnit: 1h
- Writing unit tests: 4h