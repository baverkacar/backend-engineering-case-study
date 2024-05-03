# Backend Engineering Case Study

# Table of Contents
1.  [Used Technologies](#used-technologies)
2. [System Design](#system-design-)
3. [Table Design](#mysql-tables)
4. [Architecture Solution](#architecture-solution)
5. [System Design Solution](#system-design-solutions)
    - [Data Stored in Redis](#data-stored-in-redis)
    - [Advantages and Disadvantages](#advantages)
    - [Schedulers](#schedulers)
    - [**NOTE**](#note)
    - [How can we approve](#how-could-i-design-better)
6. [How to Download and Run](#how-to-download-and-run)
7. [Folder Structure](#folder-structure-)
8. [Postman Endpoints](#postman-endpoint-collection)

   


## Used Technologies

1. **Spring** 
2. **Redis** 
3. **MySQL** 
4. **Docker**

## System Design 

![System Design](https://github.com/baverkacar/backend-engineering-case-study/blob/main/image/system-design.jpeg)

## MySQL Tables

![Table Design](https://github.com/baverkacar/backend-engineering-case-study/blob/main/image/mysql-db.png)

## Architecture Solution

- The project is structured using a layered architecture approach, which was both a starting point provided in the starter code and a practical choice for rapid development. 
- Although a more complex and comprehensive structure could benefit from a **Hexagonal** architecture, the layered architecture was sufficient for the needs of this project.

## System Design Solutions

One of the primary concerns during the development of this project was to minimize the load on the database, anticipating high HTTP traffic. To address this, I opted to utilize Redis's real-time caching capabilities, which allowed me to reduce SQL queries and offload critical operations to Redis.

### Data Stored in Redis

#### Group Information:
1. **Group Leaderboard (Sorted Set):** Group rankings are stored in Redis using Sorted Sets, allowing O(1) access time to user rankings. This structure is beneficial as data is added in a sorted manner.
2. **User Country Data in Groups (Set):** Stored in Sets, this helps in maintaining group activity status and ensures that no users from the same country are added to the same group twice.

#### Tournament Information:
1. **Active Tournament Check:** A boolean value indicating the presence of an active tournament is stored, allowing for quick checks without SQL queries.
2. **Active Tournament ID:** This is stored to provide quick access to the active tournament's ID without querying the SQL database.
3. **Country Leaderboard (Sorted Set):** This information is stored to quickly update scores for countries whenever a user levels up.

### Advantages
- **High Access Speed:** Since Redis stores data in RAM, the access speed is very high, significantly improving response times for data retrieval.

### Disadvantages
- **Single-threaded:** Redis is single-threaded, which can reduce processing speed under multiple operations.
- **Data Loss Risk:** Redis stores data in RAM initially and writes it to disk later, which poses a risk of data loss in the event of power failure or system shutdowns.

### Schedulers

I used the scheduler jobs for the beginning and end of the tournament. In this way, scheduled jobs running at 00.00 UTC and 20.00 UTC every day enabled the process to be executed in an asynchronous manner.

- When the tournament starts, it is added to the **tournaments** SQL table with tournament active status. Additionally, country leadership rankings, active tournament information, and active tournament ID are added to redis.
- When the tournament ends, the information that the tournament is over is written to the **tournaments** SQL table. Additionally, users who will win rewards are added to the **tournaments_rewards** table. Active tournament information is being updated on Redis.

### Note:

- Thanks to javadoc, you can see what kind of workflow is followed at which endpoint. Javadoc of all functions is available.


### How could I design better?
- Maybe I could create a structure that could work better under high load by using redis pub/sub or another message broker (kafka, redis).


## How to Download and Run

Follow these steps to get the project running on your local machine:

### 1. Clone the Repository
Clone the project repository from GitHub using the following Git command:
```bash
git clone <repository-url>
```
### 2. Run the Application Using Docker

```bash
docker-compose up --build
```

### 3. Swagger

After run go to swagger docs and try any endpoint: **[Swagger Page](http://localhost:8080/swagger-ui/index.html#/)** 

## Folder Structure 

```bash
├── BackendEngineeringCaseStudyApplication.java
├── config
│   └── RedisConfig.java
├── controller
│   ├── StatusController.java
│   ├── TournamentController.java
│   ├── UserController.java
│   └── advice
│       └── ControllerExceptionHandler.java
├── domain
│   ├── GroupInfo.java
│   ├── Tournament.java
│   ├── TournamentGroups.java
│   ├── TournamentRewards.java
│   └── User.java
├── enums
│   └── Country.java
├── exception
│   ├── NoActiveTournamentException.java
│   ├── UnClaimedRewardFoundException.java
│   ├── UserCanNotEnterTournamentException.java
│   ├── UserDidNotEnteredTournamentException.java
│   ├── UserEnteredTournamentBeforeException.java
│   ├── UserExistsException.java
│   └── UserNotFoundException.java
├── mapper
│   └── UserMapper.java
├── model
│   ├── ExceptionModel.java
│   ├── leaderboard
│   │   ├── CountryLeaderBoard.java
│   │   └── GroupLeaderBoard.java
│   └── user
│       ├── CreateUserRequest.java
│       └── UserProgressResponse.java
├── repository
│   ├── GroupInfoRepository.java
│   ├── TournamentGroupsRepository.java
│   ├── TournamentRepository.java
│   ├── TournamentRewardsRepository.java
│   └── UserRepository.java
├── scheduler
│   └── TournamentScheduler.java
└── service
    ├── LeaderBoardService.java
    ├── RedisService.java
    ├── TournamentService.java
    ├── UserService.java
    └── impl
        ├── LeaderBoardServiceImpl.java
        ├── RedisServiceImpl.java
        ├── TournamentServiceImpl.java
        └── UserServiceImpl.java

```

## [POSTMAN ENDPOINT COLLECTION](https://github.com/baverkacar/backend-engineering-case-study/blob/main/Backend-Case-Study.postman_collection.json)