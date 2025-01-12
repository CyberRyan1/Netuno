# To-Do List

### Other
- [X] Update this TODO list (check things off)
- [ ] When a player's punishment expires, send a notification to online staff
- [ ] Combine NetunoAPI and Netuno into a single Intellij project

### Bugs (unfixed)
- [X] Unpunishments aren't actually working
- [X] Unpunishments require two args, when they should only require one
- [X] Punishment GUIs don't load their items all at the same time
- - Want to synchronize them so the GUI updates with all items at the same time
- [X] If the punishment has zero seconds remaining, the time remaining in the message is blank
- [X] After a punishment expires, their active status in the database should be zero, but it remains at one
- [ ] (MAYBE) For IP punishments, the alts of the targeted player may not be punished correctly (I think we are forgetting to update their punishments)
- [ ] In alts GUI, skulls' names are red, even though they shouldn't be (as they don't have any active punishments)
- [ ] In alts GUI, blinking is working, but it is in the wrong index
- [ ] Need to ensure IP punishments work even if the target doesn't have any alts

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
- [ ] Ipinfo command
- [ ] Report command
- [ ] Reports command
- [ ] Togglesigns command

### Alt Alerts
- [ ] Warn staff when a player joins with punished alts

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
- (nothing here yet)
