# To-Do List

### Ideas/Other
- [X] Update this TODO list (check things off)
- [ ] When a player's punishment expires, send a notification to online staff
- [ ] Combine NetunoAPI and Netuno into a single Intellij project
- [ ] Research information about velocity/bungee support (?)
- [ ] Create an addon plugin for discord support (i.e. punishment logging, etc.) (?)
- [ ] See TODO comments in PreLoginListener class
- [X] Add better exception handling for CompletableFutures, as they silently swallow any errors thrown within them
- [ ] Add a staff chat (?)
- [ ] Add a staff mode (?)
- [ ] Add a vanish system (?)

### Bugs (unfixed)
- [ ] Forgot to add support for hover text in punished alt notifications
- [ ] Error when trying to press the arrows in the /netuno help command
- - [ ] Error when doing /netuno help 2
- [ ] History list GUI does not work correctly when it is used for the first time after the server starts
- - No clue how to fix this
- [ ] There might be issues with sending punishment notifications, i.e. being sent them repeatedly

### Backend
- [X] Better punishment model
- [X] Better alt searching algorithm
- [X] Better player model
- [ ] Databases
- - [X] IP database
- - [X] Punishment database
- - [ ] Togglesigns database
- - - [ ] Make the togglesigns database into a "staff settings" database
- - [ ] Reports database
- [X] Update to latest version of spigot/paper
- [X] Update java version
- [X] Update CyberCore to latest version of spigot/paper
- [X] Update CyberCore's java version
- [ ] Add API event dispatching (where needed)

### Commands
- [X] Netuno command
- [X] Punishment commands (i.e. /ban, /mute, etc.)
- [X] Punish command (and GUI)
- - [X] Instant punishments
- - - [X] Instant punishments for console
- [ ] Chat management commands
- - [ ] Chatslow command
- - [ ] Clearchat command
- - [ ] Mutechat command
- [X] History command
- - [X] History list GUI
- - [X] History list subcommand
- - [X] History edit GUI
- - [X] History edit subcommand
- - [X] History delete GUI
- - [X] History delete subcommand
- - [X] History reset subcommand
- - [X] History staff GUI
- - [X] History staff subcommand
- - [X] History rollbackplayer (player) (time) subcommand
- - [ ] History rollbackstaff (staff) (time/all) subcommand
- [X] Ipinfo command
- - [ ] In alts GUI, add functionality to allow staff to view the account's punishment history
- [ ] Report command
- [ ] Reports command
- [ ] Togglesigns command

### Alt Alerts
- [X] Warn staff when a player joins with punished alts

### Sign Notifications
- [ ] Send sign contents to all staff

### Skript Support
- [ ] Make Skript support a separate plugin
- [ ] Condition if player is netuno banned
- [ ] Condition if player is netuno ipbanned
- [ ] Condition if player is netuno muted
- [ ] Condition if player is netuno ipmuted
- [ ] Expression ban length
- [ ] Expression ipban length
- [ ] Expression ipmute length
- [ ] Expression mute length

### API
- [X] Better API player model
- [X] Better API punishment model
- [X] Better alt searching

### Config
- [ ] Change config updater to a better one
- - Want to use ConfigUpdater (maybe?)

### Fixed Bugs
- [X] Unpunishments aren't actually working
- [X] Unpunishments require two args, when they should only require one
- [X] Punishment GUIs don't load their items all at the same time
- - Want to synchronize them so the GUI updates with all items at the same time
- [X] If the punishment has zero seconds remaining, the time remaining in the message is blank
- [X] After a punishment expires, their active status in the database should be zero, but it remains at one
- [X] In alts GUI, skulls' names are red, even though they shouldn't be (as they don't have any active punishments)
- [X] In alts GUI, blinking is working, but it is in the wrong index
- [X] Need to ensure IP punishments work even if the target doesn't have any alts
- [X] Silent punishments executed through the punishment GUI are not actually silent
- [X] Errors when console tries to execute punishment commands
- [X] IP punishments aren't working (or, at least, IP mutes aren't working)
- - They work for the target, but none of their alts are properly punished
- [X] In the database, for punishments that were executed by console, their active column remains at one even though the punishment has expired
- [X] Punishment GUIs take time to load and don't load at the same time- try and synchronize them