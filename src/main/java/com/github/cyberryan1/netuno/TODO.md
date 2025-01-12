# To-Do List

### Other
- [X] Update this TODO list (check things off)
- [ ] When a player's punishment expires, send a notification to online staff
- [ ] Combine NetunoAPI and Netuno into a single Intellij project
- [ ] Research information about velocity/bungee support (?)
- [ ] Create an addon plugin for discord support (i.e. punishment logging, etc.) (?)
- [ ] See TODO comments in PreLoginListener class

### Bugs (unfixed)
- [ ] Need to ensure IP punishments work even if the target doesn't have any alts
- [ ] Errors when console tries to execute punishment commands
- [ ] Forgot to add support for hover text in punished alt notifications
- [X] IP punishments aren't working (or, at least, IP mutes aren't working)
- - They work for the target, but none of their alts are properly punished

### Backend
- [X] Better punishment model
- [X] Better alt searching algorithm
- [X] Better player model
- [ ] Databases
- - [X] IP database
- - [X] Punishment database
- - [ ] Togglesigns database
- - [ ] Reports database

### Commands
- [X] Netuno command
- [X] Punishment commands (i.e. /ban, /mute, etc.)
- [X] Punish command (and GUI)
- [ ] Chat management commands
- - [ ] Chatslow command
- - [ ] Clearchat command
- - [ ] Mutechat command
- [ ] History command
- - [ ] History list subcommand
- - [ ] History edit subcommand
- - [ ] History reset subcommand
- - [ ] A way to view the punishments that were executed by a certain staff member
- - [ ] A way to rollback the punishments executed by a certain staff member
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
