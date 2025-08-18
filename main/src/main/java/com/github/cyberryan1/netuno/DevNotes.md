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



Player1 joins:
- 1.2.3.4 <- current IP
- 2.5.6.7

Player2 joins:
- 1.2.3.4
- 6.7.8.9 <- current IP

Player3 joins:
- 3.4.5.6 <- current IP
- 6.7.8.9

Player4 joins:
- 2.3.6.7

Player5 joins:
- 9.8.7.0
- 3.2.4.1

searchAlts( player )

    let foundAccounts be a list // <- this is what we return # Player1
    let searchedIps be a list // <- a list of IPs that we have searched
    let unsearchedAccounts be a list // <- a list of accounts that has not been searched
    add player to unsearchedAccounts 

    while ( size of unsearchedAccounts != 0 )
        let account be the first element in unsearchedAccounts # Player1
        let accountStoredIps be account's stored ips # 1.2.3.4 and 2.5.6.7
        
        add account to foundAccounts
        
        for ( storedIp in accountStoredIps ) # storedIp = 1.2.3.4
            if storedIp in searchedIps: continue 
            add storedIp to searchedIps

            let accountsOnStoredIp be the accounts # Player1, Player2
                        who have joined with ip storedIp

            for ( acc in accountsOnStoredIp ) # Player1
                if acc is not in foundAccounts AND acc is not in unsearchedAccounts
                    add acc to unsearchedAccounts

        remove account from unsearchedAccounts

### The below code works according to the provided test cases

```java
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    static List<Player> playerList = new ArrayList<>();

    public static void main( String args[] ) {
        Player one = new Player( "one", List.of( "1.2.3.4", "5.6.7.8" ) );
        Player two = new Player( "two", List.of( "1.2.3.4", "3.4.5.6" ) );
        Player three = new Player( "three", List.of( "3.4.5.6", "9.8.5.0", "2.3.4.5" ) );
        Player four = new Player( "four", List.of( "0.0.0.0" ) );
        Player five = new Player( "five", List.of( "0.0.0.0" ) );
        Player six = new Player( "six", List.of( "0.0.0.0", "3.3.3.3" ) );
        Player seven = new Player( "seven", List.of( "3.5.7.9" ) );
        Player eight = new Player( "eight", List.of( "1.3.6.7", "5.8.9.2" ) );
        
        playerList.addAll( List.of( one, two, three, four, five, six, seven, eight ) );
        
        System.out.println( "searchAlts( one ) -> expected to yield one, two, three" );
        printList( searchAlts( one ) );
        System.out.println( "searchAlts( two ) -> expected to yield one, two, three" );
        printList( searchAlts( two ) );
        System.out.println( "searchAlts( three ) -> expected to yield one, two, three" );
        printList( searchAlts( three ) );
        System.out.println( "searchAlts( four ) -> expected to yield four, five, six" );
        printList( searchAlts( four ) );
        System.out.println( "searchAlts( five ) -> expected to yield four, five, six" );
        printList( searchAlts( five ) );
        System.out.println( "searchAlts( six ) -> expected to yield four, five, six" );
        printList( searchAlts( six ) );
        System.out.println( "searchAlts( seven ) -> expected to yield seven" );
        printList( searchAlts( seven ) );
        System.out.println( "searchAlts( eight ) -> expected to yield eight" );
        printList( searchAlts( eight ) );
    }

    public static List<Player> getAccountsOnIp( String ip ) {
        return playerList.stream()
                .filter( player -> player.joinedIps.contains( ip ) )
                .collect( Collectors.toList() );
    }

    public static List<Player> searchAlts( Player player ) {
        List<Player> foundAccounts = new ArrayList<>();
        List<String> searchedIps = new ArrayList<>();
        List<Player> unsearchedAccounts = new ArrayList<>();
        unsearchedAccounts.add( player );

        while ( unsearchedAccounts.size() > 0 ) {
            Player account = unsearchedAccounts.get( 0 );
            List<String> accountStoredIps = account.joinedIps;

            foundAccounts.add( account );

            for ( String storedIp : accountStoredIps ) {
                if ( searchedIps.contains( storedIp ) ) continue;
                searchedIps.add( storedIp );

                List<Player> accountsOnStoredIp = getAccountsOnIp( storedIp );
                for ( Player acc : accountsOnStoredIp ) {
                    if ( foundAccounts.contains( acc ) == false && unsearchedAccounts.contains( acc ) == false ) {
                        unsearchedAccounts.add( acc );
                    }
                }
            }

            unsearchedAccounts.remove( account );
        }
        
        return foundAccounts;
    }
    
    public static void printList( List<Player> list ) {
        for ( Player player : list ) {
            System.out.print( player.username + " | " );
        }
        System.out.println();
    }
}

class Player {

    public List<String> joinedIps = new ArrayList<>();
    public String username = null;

    public Player( String username, List<String> joinedIps ) {
        this.username = username;
        this.joinedIps = joinedIps;
    }
}
```