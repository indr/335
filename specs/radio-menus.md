# Radio Menus Specs

The radio menu consists of these main menu items:

 - New Stations
 - Genres
 - Countries
 - Languages

## New Stations

Lists the 50 latest inserted or updated radio stations sorted by date descending.

Title: Stations name  
Subtitle: Stations country, languages and genres

## Genres

Lists genres starting with top 15 genres sorted alphabetically based on number of radio stations. `Show more Genres` lists the top 50 genres alphabetically. `Show all Genres` lists all genres alphabetically which have a minimum of 9 radio stations.

Specific genres are filtered from each list. See [res/values/content.xml](../app/src/main/res/values/content.xml).

Title: Genres name  
Subtitle: Number of stations in genre

## Genre

Lists stations of a specified genre. Starts with the top 20 top voted stations sorted alphabetically. `Show more Stations` lists alls stations which have a positive sum of votes sorted alphabetically. `Show all Stations` lists all stations sorted alphabetically.

Title: Stations name  
Subtitle: Stations country and language

## Countries

Lists countries starting with the English speaking countries Australia, Canada, New Zealand, United Kingdom and United States of America. `Show all Countries` lists all countries sorted alphabetically.

Title: Countries name  
Subtitle: Number of stations in country

## Country

Lists stations of specified country. Starts with the top 20 top voted stations sorted alphabetically. `Show more Stations` lists all stations which have a positive sum of votes sorted alphabetically. `Show all Stations` lists all stations sorted alphabetically.

Title: Stations name  
Subtitle: Stations language and genres

## Languages

Lists all languages sorted alphabetically.

Title: Stations name  
Subtitle: Number of stations in language

## Language

Lists stations of specified language. Starts with the top 20 highest voted stations sorted alphabetically. `Show more Stations` lists all stations which have a positive sum of votes sorted alphabetically. `Show all Stations` lists all stations sorted alphabetically.

Title: Stations name  
Subtitle: Stations country and genres

## Station

Shows details of specified station.

 - Play command
 - AddPlaylist command
 - Favorite command
 - Country
 - Languages
 - Genres
 - Votes (Sum, positives and negatives)
 - Updated
 - Link to Website (List interface only)
 
