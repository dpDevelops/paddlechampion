# paddlechampion
This an Arkanoid clone game, written in Java/Kotlin as the final project for a university class.  It features 6 stages, a highscore system, a simple UI, along with sound effects and music  

This application is built with MVVM architecture.  Jetpack fragments are used to manage menus and navigating between them.  The main UI thread is used to update the game state and render entities to the screen, 
while a coroutine runs in the background to handle collision detection during gameplay.
