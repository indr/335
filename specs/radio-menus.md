# Radio Menus Specs

The radio menu consists of these main menu items:

 - New Stations
 - Genres
 - Countries
 - Languages

## New Stations

Lists inserted or updated radio stations sorted by date descending. Starts with 20 stations and shows option to `Show more Stations` which adds the next 20 stations to the list.

Title: Station name
Subtitle: Update date and time, country, language

## Genres

Lists genres starting with top 15 genres sorted alphabetically based on number of radio stations and an option to `Show more Genres`.

Show more Genres: Lists the top 50 genres alphabetically with an option to `Show all Genres`.

Show all Genres: Lists all genres alphabetically which have more than 2 radio stations.

Specific genres are filtered from each list. See [res/strings/content.xml](../app/src/main/res/strings/content.xml).

Title: Genre name
Subtitle: Number of stations in genre

## Genre

Lists stations of a specified genre. Starts with the top 20 top voted stations sorted alphabetically. `Show all Stations` lists all stations sorted alphabetically.

Show all Stations: Lists all stations sorted alphabetically by station name.

Title: Station name
Subtitle: Stations country and language

## Countries

Lists countries starting with the English speaking countries Australia, Canada, New Zealand, United Kingdom and United States of America. `Show all Countries` lists all countries sorted alphabetically.

Title: Country
Subtitle: Number of stations in country

## Country

Lists stations of specified country. Starts with the top 20 top voted stations sorted alphabetically. `Show all Stations` lists all stations sorted alphabetically.

Title: Station name
Subtitle: Stations language and genres

## Languages

Lists all languages sorted alphabetically.

Title: Station name
Subtitle: Number of stations in language

## Language

Lists stations of specified language. Starts with the top 20 highest voted stations sorted alphabetically. `Show all Stations` lists all stations sorted alphabetically.

Title: Station name
Subtitle: Stations language and genres

## Station

Shows details of specified station.

 - Play command
 - AddPlaylist command
 - Favorite command
 - Country
 - Language
 - Genres
 - Votes
 - Updated
 - Link to Website (List interface only)
 
