## Developer Info

Below is a list of all the assumptions, notes, etc. that I make
as I am working on Netuno. I figured writing these down would be
helpful in the future

---

### Punishments

All information regarding punishments can be found here

**Definitions**

- Original punishment _(in context of an IP punishment)_ refers to the player who was originally punished by a staff member
-

**Assumptions**

- For IP punishments, the original punishment will have a reference ID set to `ApiPunishment.DEFAULT_REFERENCE_ID`
- For IP punishments, any non-original punishments will have a reference ID equal to the punishment ID of the original punishment
- For IP punishments, in the `PunishmentsDatabase#getPunishments( String playerUuid )` method, if a non-original punishment has a reference ID to a punishment that no longer exists in the database, then the non-original punishment is deleted from the database
- Before a punishment is added to the database via `PunishmentsDatabase#addPunishment( Punishment punishment )`, it's ID is set to `ApiPunishment.DEFAULT_ID`. When this method is called, the passed punishment is given the correct ID
- 