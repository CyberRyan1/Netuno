# Netuno

---

## Skript Documentation
Netuno does offer support for Skript. Documentation for everything Netuno supports can be found below.

---

### Conditions

---

**Is Netuno Muted**\
Checks if a player is currently muted by Netuno.\
`%offlineplayer% is [currently] netuno muted`\
  ```
  command /checkmuted [<offlineplayer>]:
      trigger:
          if arg is netuno muted:
              send "%arg% is muted"
  ```
---

**Is Netuno Banned**\
Checks if a player is currently banned by Netuno.\
`%offlineplayer% is [currently] netuno banned`\
  ```
  command /checkbanned [<offlineplayer>]:
      trigger:
          if arg is netuno banned:
              send "%arg% is banned"
  ```
---
**Is Netuno IP Muted**\
Checks if a player is currently IP muted by Netuno.\
`%offlineplayer% is [currently] netuno ipmuted`\
  ```
  command /checkipmuted [<offlineplayer>]:
      trigger:
          if arg is netuno ipmuted:
              send "%arg% is ipmuted"
  ```
---
**Is Netuno IP Banned**\
Checks if a player is currently IP banned by Netuno.\
`%offlineplayer% is [currently] netuno ipbanned`\
  ```
  command /checkipbanned [<offlineplayer>]:
      trigger:
          if arg is netuno ipbanned:
              send "%arg% is ipbanned"
  ```

---

### Expressions

---
**Length of Netuno Mute**\
Gets the length of a Netuno mute.\
`length of netuno mute [of] %offlineplayer%`
  ```
  command /getmutelength [<offlineplayer>]:
      trigger:
          send "%length of netuno mute of arg%"
  ```
---
**Length of Netuno Ban**\
Gets the length of a Netuno ban.\
`length of netuno ban [of] %offlineplayer%`
  ```
  command /getbanlength [<offlineplayer>]:
      trigger:
          send "%length of netuno ban of arg%"
  ```
---
**Length of Netuno IP Mute**\
Gets the length of a Netuno IP mute.\
`length of netuno ipmute [of] %offlineplayer%`
  ```
  command /getipmutelength [<offlineplayer>]:
      trigger:
          send "%length of netuno ipmute of arg%"
  ```
---
**Length of Netuno IP Ban**\
Gets the length of a Netuno IP ban.\
`length of netuno ipban [of] %offlineplayer%`
  ```
  command /getipbanlength [<offlineplayer>]:
      trigger:
          send "%length of netuno ipban of arg%"
  ```
---