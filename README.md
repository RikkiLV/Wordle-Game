Basic outline of how the Wordle game will work.The app allows users to guess a 5-letter word chosen randomly from a list. The app validates the guess length, gives feedback on each guess indicating correct letters and wrong positions with "+" and incorrect letters with "-", and shows the previous guesses in a GridView.

Things to consider for improvement:

**Interface:**
Resemble Wordle UI: Implementing a UI that resembles the original Wordle game, especially with correct letters having a green square background in the GridView, will enhance the user experience and familiarity.

**Logic:**
More Words: Expand the word list to include more options for random generation. Consider integrating with an external word database API to diversify the word choices dynamically.
Input Validation: Improve input validation to accept only alphabetic characters for guesses, ensuring the game accepts valid words.

**Features:**
Save Game State: Implementing SharedPreferences or SQLite to save game state will allow users to continue their game sessions across app launches.
Settings/Menu: Introduce settings or a menu for customizable UI elements, such as themes or font sizes, to personalize the app experience.
Reset Button: Adding a reset button will enable users to start a new game or reset their current game state easily.
