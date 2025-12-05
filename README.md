# OtakuHub - Anime Watchlist App

## Overview
OtakuHub is an Android application designed for anime enthusiasts to discover, track, and manage their anime watchlist. The app provides a user-friendly interface to browse anime titles, view details, and manage a personal profile.

## Features
- **User Authentication:** Secure Login and Sign Up screens powered by Firebase Authentication.
    - Ergonomic design with centered UI.
    - Password visibility toggle.
- **Browse Anime:** Discover new anime titles.
- **Anime Details:** View detailed information about selected anime.
- **User Profile:** Manage user settings and view profile information.

## Technology Stack
- **Language:** Java
- **UI Components:** ConstraintLayout, ScrollView, Material Design (TextInputLayout), RecyclerView.
- **Networking:** Retrofit 2 (with Gson converter).
- **Image Loading:** Glide.
- **Backend/Auth:** Firebase (Auth, Database).

## Setup & Installation
1. Clone the repository.
2. Open in Android Studio.
3. Sync Gradle project.
4. Ensure `google-services.json` is configured for Firebase.
5. Run on an emulator or physical device.

## Recent Updates
- Redesigned Login and Sign Up pages for better ergonomics and accessibility.
- Implemented password visibility toggles.
- Optimized navigation bar spacing across multiple screens (Browse, Profile, Details) for better visual balance.
