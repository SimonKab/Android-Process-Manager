| Title           |      Description      |  Deadline | Finished |
|----------------------|:---------------------:|-------:|------:|
| Init and configure project |  Initialize project, configure build.gradle and other settings for modern work process. Modern dependencies, debug keystore, versioning of apk | 15.04 | 29.03 |
| Show working processes |  Create fragment with recycler view that shows list of running proccesses. Icon, process name, process package. Tap on item should show additional information | 15.04 | 07.04 |
| Let process be killed |  Let user to kill a process. Button in list item that appears when user makes one tap | 15.04 | 27.04 |
| Clean memory |  Add feature to kill all apps. Show memory usage and percent of free memory. Button like Clean up to remove process from memory. Show result dialog: memory before cleaning, memory after cleaning, effectiveness | 15.04 | 11.04 |
| Change process details |  Long tap should show dialog where you can change process details. Figure out what we can change | 15.04 | 11.04 |
| Clean architecture, Tabs | Add tabs in main acitivity, all other ui logic in corresponding fragments. Introduce Clean architecture with Room, View Model, Live Data, Repository | 20.04 | 21.04 (Lack of time) |
| Blacklist fragment add. All apps | Show all installed apps in recyclerview in special list. Create separated adapter with twe different lists in one  | 01.05 | 28.04 |
| Blacklist. Persistance storage | Create database to store blacklist data. Show blacklist apps in special part of recyelr view (may be in top)  | 01.05 | 28.04 |
| Background killing |  Make blacklist apps to be killed by app. Create JobScheduler service for this puprose | 01.05 | 29.04 |
| Notification |  Send notification to user when blacklist app is running. Show scary message. Make notification sound. Save timestamp to show user when blacklist app was opened last time | 01.05 | 29.04 |
| Terminal fragment add. Terminal simple output | Create simple fragment where corresponding edit text and text view will be created dynamically. Create terminal class where new processes for commands will be created | 01.05 | 23.04 |
| Terminal. Add request handlers | Create request handlers for commands: pwd, cd, echo, export to handle it manually. Use brain to create it maintainable | 01.05 | 23.04 |
| Terminal. Add multithreading | Create HandlerThread to perform temrinal process in other thread. Add listeners to get data in ui. Add handlers to communicate with thread | 01.05 | 23.04 |
| Terminal. Top command | Let terminal to handle input stream while command is running and perform some commands in other way. Support top command. Show output continuously while user will not stop. Show top command output correctly: first memory usage, than table headers and then all processes. When new refresh occure, refresh textview and not add new output to bottom of fragment, just change current textview content | 01.05 | 26.04 |
| Terminal. Database | Add history to temrinal. Save history in database to let user see previous commands. Also let user tap on previous command to reuse it | 01.05 | 27.04 |
| Terminal. Database | Add history to temrinal. Save history in database to let user see previous commands. Also let user tap on previous command to reuse it | 01.05 | 27.04 |
| Localization. Russion. English | Add localization to app: russian, english |  03.05 | 01.05 |
