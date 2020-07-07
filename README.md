# Eating Companion Finder

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This mobile app lets users find people near them to grab a bite with. Users can set up profiles, message each other, and view nearby restaurants.

### App Evaluation
- **Category:** Social, Food
- **Mobile:** Mainly/mostly mobile, for people on the go
- **Story:** Don't need to feel lonely at meals, and you can meet people in a casual/open environment. You can choose who to connect with based on profiles.
- **Market:** Anyone who wants to meet new people or have someone to eat with. Can be used for networking or casual meetups.
- **Habit:** Users might get used to having someone else to eat with and feel excited to meet new people. There could also be deals featured on the app to incentivize users to eat at certain restaurants with others
- **Scope:** Can start out as an app with basic profiles, restaurants nearby, and messaging and then later expand to have deals and try to match/recommend people based on interests

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create a new account
* User can login
* User can set up a profile (photos, bio)
* User can see users near them
* User can message other users
* User can see nearby restaurants
* User can see other profiles

**Optional Nice-to-have Stories**

* User can see restaurant deals
* User can see recommended users based on common interests
* User can post photos of food/have a food feed
* User can comment on other food posts
* User can receive notifications for messages/deals
* User can add friends to see their posts in their feed
* User can see restaurant food in their feed

### 2. Screen Archetypes

* Login screen
    * User can login
* Registration Screen
    * User can create a new account
* Nearby Restaurants Screen
    * User can see nearby restaurants
* Nearby Users Screen
    * User can see nearby users
* Message Screen
    * User can message other users
* Profile
    * User can set up a profile (photos, bio)
    * User can see other profiles
* Camera Screen
    * User can take pictures
    * User can upload pictures

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Restaurants Nearby
* Users Nearby
* Messages
* User Profile
* Camera

**Flow Navigation** (Screen to Screen)

* Login screen
   => Home screen
* Registration screen
   => Home screen
* Home screen
   => Detailed post screen
* Nearby Users screen
  => Profile screen
* Nearby Restaurants screen
  => Nearby Users screen
  => Detailed restaurant screen
* Messages screen
  => Profile screen
  => Detailed message screen
* Self Profile Screen
  => Edit profile screen
  => Detailed post screen
* Other User Profile screen
  => Message screen
  => Detailed post screen
* Camera screen
  => Post preview screen

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="wireframe.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

https://www.figma.com/file/h1cTCJ0Bk4bR0vQOLJ1JLv/Eating-Companion-App?node-id=0%3A1

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
