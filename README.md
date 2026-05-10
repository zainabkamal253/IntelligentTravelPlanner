# Intelligent Travel Planner — v5.0

**SEECS NUST — BESE-16-A**
Muhammad Saadain Zahid · Armish Bibi · Zainab Kamal

A Java Swing OOP project for planning trips around Pakistan, with interest-aware
recommendations, day-by-day itineraries, budget tracking, route planning, and
mock booking.

---

## How to run

```bash
javac IntelligentTravelPlanner.java
java IntelligentTravelPlanner
```

Default login: `admin / admin123` (or register a new user from the login screen).

> **Important:** if you have old `data/trips_*.dat` files from v4.x, delete the
> `data/` folder before launching. The `DayPlan` class now stores structured
> `Activity` objects instead of plain strings, so old saves are not compatible.
> The app will not crash on old files — it will just show an empty trip list.

---

## What changed from v4 → v5

### Bug fixes
- **Location fetching when adding a trip** — `PlacesService` had a malformed
  class structure (an extra `}` ended the class early, so the city-coordinate
  cache loaded from `pakistan_cities.csv` was never consulted). Rewritten with
  a clean local-first lookup: Pakistani cities resolve instantly from the CSV
  before the code ever hits a network API.
- **Dashboard had every button drawn twice** — buttons were added once
  individually and again inside a styling loop. Now added exactly once.
- **Itineraries ignored the chosen interest** — the engine used a hardcoded
  fallback keyed only by city, so "adventure in Lahore" returned the same
  historical sites as "historical in Lahore". Rewritten end-to-end (see below).

### New: interest-aware recommendations
A new `TouristDB` singleton loads `tourist_spots.csv` once at startup and
indexes ~200 places by city. Its `placesFor(city, interest)` method maps the
chosen interest to one or more CSV categories:

| User picks   | CSV categories searched     |
|--------------|-----------------------------|
| adventure    | Adventure, Nature           |
| food         | Food                        |
| historical   | Historical, Cultural        |
| nature       | Nature, Adventure           |
| shopping     | Cultural, Food              |
| cultural     | Cultural, Historical        |

So picking *adventure in Lahore* now actually returns adventure/nature spots
instead of Badshahi Mosque.

### New: card-based itinerary viewer
The old itinerary view was a plain `JTextArea` text dump. The new
`ItineraryViewerFrame` builds a vertically scrollable column of day cards:

- Each day has a coloured accent bar in the interest's theme colour
  (orange for adventure, amber for food, purple for historical, green for
  nature, teal for cultural, pink for shopping).
- Each day has a **theme** chosen by the engine — e.g. *Adrenaline kickoff*,
  *Heritage walk*, *Foodie crawl*.
- Each activity is its own row with a time chip, an emoji icon, a bold title,
  and a description, instead of a flat `Morning: ...` string.
- Activity slots, phrasings, and "flourish" lines are randomized per day so
  no two days read the same.
- Header tiles show destination, dates, budget, and people count.
- An *Export to .txt* button produces a plain-text version for offline use.

### Other improvements
- Destination field is now a `JComboBox` pre-populated with every city in
  `pakistan_cities.csv` (still freely typeable for cities not in the dataset).
- New `Activity` model class — slot, time, emoji, title, description.
- New `TouristPlace` model class — name, category, description, city, emoji.
- `DayPlan` now carries a `theme` and a `List<Activity>` instead of strings.
- `Itinerary` carries a one-line `summary` describing the trip.
- No place is recommended twice in the same trip.
- Better empty-state handling everywhere.

---

## OOP concepts demonstrated

| Concept                | Where                                                  |
|------------------------|--------------------------------------------------------|
| Inheritance            | `User` → `RegularUser`, `AdminUser`                    |
| Abstract classes       | `User` (abstract `getRole()`)                          |
| Interfaces             | `Bookable`, `Recommendable`, `APIService`, `TripObserver` |
| Polymorphism           | `Recommendable.recommendPlaces` returns `List<TouristPlace>` used uniformly |
| Encapsulation          | All model classes are private-field + getter/setter   |
| Generics               | `FileHandler<T extends Serializable>`                 |
| Singleton pattern      | `TouristDB.getInstance()`                             |
| Exception handling     | Custom `AuthException`, `BudgetExceededException` + try/catch around all I/O and API calls |
| Collections framework  | `Map`, `List`, `Set`, `LinkedHashMap`, `HashMap`      |
| File I/O & serialisation | `FileHandler` (object streams), CSV readers          |
| Multithreading         | `SwingWorker` for itinerary generation, weather, geocoding (UI never freezes) |
| GUI                    | Swing — `JFrame`, `JPanel`, `BoxLayout`, `GridBagLayout`, custom painted accent bars |
| Algorithms             | Dijkstra in `RoutePlanner` for shortest path between cities |

---

## File layout

```
Travel Planner/
├── IntelligentTravelPlanner.java   ← all source (one file, ~2700 lines)
├── pakistan_cities.csv             ← 279 cities with coordinates
├── tourist_spots.csv               ← ~200 places, categorized
├── data/                           ← created at runtime for users.dat / trips_*.dat
└── README.md
```

The two CSVs **must** sit next to the `.class` file at runtime — they are
loaded from the working directory.
