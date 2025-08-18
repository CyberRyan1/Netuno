# To-Do List

### Ideas/Other
- [X] Update this TODO list (check things off)
- [X] When a player's punishment expires, send a notification to online staff
- [X] Combine NetunoAPI and Netuno into a single Intellij project
- [ ] Research information about velocity/bungee support (?)
- [ ] Create an addon plugin for discord support (i.e. punishment logging, etc.) (?)
- [X] See TODO comments in PreLoginListener class
- [X] Add better exception handling for CompletableFutures, as they silently swallow any errors thrown within them
- [ ] Add a staff chat (?)
- [ ] Add a staff mode (?)
- [ ] Add a vanish system (?)
- [X] Rename Punishment to NetunoPunishment
- [X] Rename NetunoService.DEBUG_PRINTER_NPLAYER variable to NetunoService.DEBUG_PRINTER_NETUNOPLAYER
- [X] Add debug printing for the staff cache (see NetunoService)
- [X] Add debug printing for the reports cache (see NetunoService)
- [X] Redo the NetunoService.getPlayer() and NetunoService.getStaff() methods to better utilize the cache (see ReportService.getReportsAgainst() method)
- [X] Ensure all sounds are working as intended (see sounds in the config.yml)
- - [X] Staff sounds
- - [X] Target sounds
- - [X] Global sounds
- [ ] Ensure all config variables are correctly implemented (and add better notes in the config for what variables they can use where)
- - [X] Make replacing config variables into an actual class
- [X] Add command prevention when a player is muted/ipmuted

### Bugs (unfixed)
- [ ] History list GUI does not work correctly when it is used for the first time after the server starts
- - No clue how to fix this
- [ ] There might be issues with sending punishment notifications, i.e. being sent them repeatedly
- [X] Unipmute sounds don't work correctly
- [X] Target does not receive sounds when they are punished
- [X] IP punishment broadcasts are sent to player's alts twice, one being the global broadcast and another being the target broadcast
- [X] Chatting is extremely laggy (I think it happens when you have many punishments or some IP mutes)
- [ ] (maybe) Want to cache the last punishment ID so that NetunoPunishment#execute can be a bit faster
- [ ] IP punishments don't work if the player doesn't have a stored IP, even though they should

### Backend
- [X] Better punishment model
- [X] Better alt searching algorithm
- [X] Better player model
- [X] Databases
- - [X] IP database
- - [X] Punishment database
- - [X] Togglesigns database
- - - [X] Make the togglesigns database into a "staff settings" database
- - - - Making the togglesigns database into a settings database will also allow us to save other settings, i.e. chat mute status, chat delay status, etc.
- - [X] Reports database
- [X] Update to latest version of spigot/paper
- [X] Update java version
- [X] Update CyberCore to latest version of spigot/paper
- [X] Update CyberCore's java version
- [X] Add API event dispatching (where needed)
- [X] Rename NPlayer class to NetunoPlayer

### Commands
- [X] Netuno command
- - [X] Help subcommand
- - [X] Debug subcommand
- - [X] Reload subcommand
- [X] Punishment commands (i.e. /ban, /mute, etc.)
- [X] Punish command (and GUI)
- - [X] Instant punishments
- - - [X] Instant punishments for console
- [X] Chat management commands
- - [X] Slow subcommand
- - [X] Clear subcommand
- - [X] Mute subcommand
- - [X] Status subcommand
- - [X] Implement functionality for chatmute and chatslow
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
- - [X] History rollbackstaff (staff) (time/all) subcommand
- [X] Ipinfo command
- - [X] In alts GUI, add functionality to allow staff to view the account's punishment history
- [X] Report command
- [X] Viewreports command
- - Renamed /reports to /viewreports
- [X] Togglesigns command
- [X] Watchlist command

### Alt Alerts
- [X] Warn staff when a player joins with punished alts

### Punishment expiration alerts
- [X] Notify staff when a player joins/chats after their punishment expires

### Sign Notifications
- [X] Send sign contents to all staff

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
- [X] Change config updater to a better one
- - Want to use ConfigUpdater

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
- [X] Error when trying to press the arrows in the /netuno help command
- - [X] Error when doing /netuno help 2
- [X] Forgot to add support for hover text in punished alt notifications